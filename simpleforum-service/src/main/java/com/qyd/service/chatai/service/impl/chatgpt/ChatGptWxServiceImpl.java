package com.qyd.service.chatai.service.impl.chatgpt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.ai.AISourceEnum;
import com.qyd.core.async.AsyncUtil;
import com.qyd.core.cache.RedisClient;
import com.qyd.service.chatai.constants.ChatConstants;
import com.qyd.service.chatai.service.ChatGptService;
import com.qyd.service.user.repository.entity.UserDO;
import com.qyd.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 邱运铎
 * @date 2024-05-10 22:25
 */
@Slf4j
@Service
public class ChatGptWxServiceImpl implements ChatGptService {

    @Resource
    private ChatGptIntegration chatGptHelper;

    private LoadingCache<Long, ChatRecordWxVo> chatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(50).build(new CacheLoader<Long, ChatRecordWxVo>() {
                @Override
                public ChatRecordWxVo load(Long userId) throws Exception {
                    return new ChatRecordWxVo();
                }
            });

    private boolean rateLimit(Long userId) {
        Integer cnt = RedisClient.hGet(ChatConstants.getAiRateKey(AISourceEnum.CHAT_GPT_3_5), String.valueOf(userId), Integer.class);
        if (cnt == null) {
            cnt = 0;
        }
        return cnt < ChatConstants.MAX_CHAT_GPT_QAS_CNT;
    }

    private Long incrCnt(Long userId) {
        return RedisClient.hIncr(ChatConstants.getAiRateKey(AISourceEnum.CHAT_GPT_3_5), String.valueOf(userId), 1);
    }

    @Autowired
    private UserService userService;

    @Override
    public boolean inChat(String wxUuid, String content) {
        if (content != null && content.toLowerCase().trim().startsWith("chat")) {
            return true;
        }

        UserDO user = userService.getWxUser(wxUuid);
        if (user == null) {
            return false;
        }

        // 存在会话记录， 表示在会话中
        ReqInfoContext.getReqInfo().setUserId(user.getId());
        chatCache.cleanUp();
        return chatCache.getIfPresent(user.getId()) != null;
    }

    @Override
    public String chat(String wxUuid, String content) {
        if (content.toLowerCase().trim().startsWith("chat")) {
            // 开始会话
            UserDO user = userService.getWxUser(wxUuid);
            if (user == null) {
                return ChatConstants.CHAT_REPLY_RECOMMEND;
            }
            chatCache.put(user.getId(), new ChatRecordWxVo());
            return ChatConstants.CHAT_REPLY_BEGIN;
        }

        // 正常对话
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        if (content.toLowerCase().trim().equalsIgnoreCase("end") || content.trim().startsWith("结束")) {
            // 结束会话
            chatCache.invalidate(userId);
            chatCache.cleanUp();
            return ChatConstants.CHAT_REPLY_OVER;
        }

        if (!rateLimit(userId)) {
            // 次数已经用完了，直接返回
            chatCache.cleanUp();
            return ChatConstants.CHAT_REPLY_CNT_OVER;
        }

        // 判断用户的上一次访问结果有没有正确返回，如果没有，那么这一次的交互不响应，直接返回上一次的返回结果
        ChatRecordWxVo chatRecord = chatCache.getUnchecked(userId);
        if (System.currentTimeMillis() - chatRecord.getQasTime() < ChatConstants.QAS_TIME_INTERVAL) {
            // 限制交互频率
            if (chatRecord.canReply()) {
                // 上次没有回复时，如果现在有结论了，那就回复一下
                return chatRecord.reply();
            } else {
                return ChatConstants.CHAT_REPLY_QAS_TOO_FAST;
            }
        }

        // 执行正常的提问、应答; 针对上次结果还没有拿到的场景做一个兼容, 只有拿到结果后，才继续相应后续的问答
        if (StringUtils.isBlank(chatRecord.getQas()) || chatRecord.isLastReturn()) {
            // 首次提问，后者上次的提问正确返回了结果
            ChatRecordWxVo newRecord = doQuery(userId, content, chatRecord);
            if (newRecord.canReply()) {
                return chatRecord.reply();
            } else {
                // 只有超时没拿到结果的场景，会走这里
                return ChatConstants.CHAT_REPLY_TIME_WAITING;
            }
        } else if(chatRecord.canReply()) {
            // 判断上次的结果是否已经获取到了
            return chatRecord.reply();
        } else {
            // 结果还没有拿到，继续等到
            return ChatConstants.CHAT_REPLY_TIME_WAITING;
        }
    }

    /**
     * 执行具体的chatGpt请求，并做一个超时限制
     *
     * @param userId
     * @param content
     * @param currentChat
     * @return
     */
    private ChatRecordWxVo doQuery(Long userId, String content, ChatRecordWxVo currentChat) {
        // 访问计数+1
        Long cnt = incrCnt(userId);

        // 重新构建当天的聊天记录
        ChatRecordWxVo newRecord = new ChatRecordWxVo().setPre(currentChat)
                .setQasIndex(Optional.ofNullable(cnt).orElse(1L).intValue());
        newRecord.setQas(content).setLastReturn(false).setQasTime(System.currentTimeMillis());
        currentChat.setNext(newRecord);
        chatCache.put(userId, newRecord);

        try {
            AsyncUtil.callWithTimeLimit(3500, TimeUnit.MILLISECONDS, () -> chatGptHelper.directReturn(userId, newRecord));
        } catch (TimeoutException | InterruptedException e) {
            // 超时中断的场景
            newRecord.setLastReturn(false);
        } catch (Exception e) {
            log.warn("chatGpt出现了非预期的异常！content: {}", content, e);
            newRecord.setLastReturn(true);
            if (newRecord.getSysErr() == null) {
                newRecord.setSysErr(e.getMessage());
            }
        }
        return newRecord;
    }
}