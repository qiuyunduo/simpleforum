package com.qyd.service.chatai;

import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.ai.AISourceEnum;
import com.qyd.api.model.vo.chat.ChatRecordsVo;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.chatai.service.ChatServiceFactory;
import com.qyd.service.chatai.service.impl.chatgpt.ChatGptIntegration;
import com.qyd.service.chatai.service.impl.xunfei.XunFeiIntegration;
import com.qyd.service.user.service.conf.AiConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 聊天的们门面类
 *
 * @author 邱运铎
 * @date 2024-05-09 18:23
 */
@Slf4j
@Service
public class ChatFacade {

    @Resource
    private AiConfig aiConfig;
    @Resource
    private ChatServiceFactory chatServiceFactory;

    /**
     * 基于Guava的单实例缓存
     */
    private Supplier<AISourceEnum> aiSourceCache;

    /**
     * 返回推荐的AI模型
     *
     * @return
     */
    public AISourceEnum getRecommendAiSource() {
        if (aiSourceCache == null) {
            refreshAiSourceCache(Collections.emptySet());
        }
        AISourceEnum sourceEnum = aiSourceCache.get();
        if (sourceEnum == null) {
            refreshAiSourceCache(getRecommendAiSource(Collections.emptySet()));
        }
        return aiSourceCache.get();
    }

    /**
     * 返回推荐的AI模型
     *
     * @param except
     * @return
     */
    private AISourceEnum getRecommendAiSource(Set<AISourceEnum> except) {
        AISourceEnum source;
        try {
            ChatGptIntegration.ChatGptConfig config = SpringUtil.getBean(ChatGptIntegration.ChatGptConfig.class);
            if (!except.contains(AISourceEnum.CHAT_GPT_3_5) && !CollectionUtils.isEmpty(config.getConf()
                    .get(config.getMain()).getKeys())) {
                source = AISourceEnum.CHAT_GPT_3_5;
            } else if (!except.contains(AISourceEnum.XUN_FEI_AI) && StringUtils.isNotBlank(SpringUtil.getBean(XunFeiIntegration.XunFeiConfig.class)
                    .getApiKey())) {
                source = AISourceEnum.XUN_FEI_AI;
            } else {
                source = AISourceEnum.PAI_AI;
            }
        } catch (Exception e) {
            source = AISourceEnum.PAI_AI;
        }

        if (source != AISourceEnum.PAI_AI && aiConfig.getSource().contains(source)) {
            Set<AISourceEnum> totalExcepts = Sets.newHashSet(except);
            totalExcepts.add(source);
            return getRecommendAiSource(totalExcepts);
        }
        log.info("当前选中的AI模型: {}", source);
        return source;
    }

    public void refreshAiSourceCache(AISourceEnum ai) {
        aiSourceCache = Suppliers.memoizeWithExpiration(() -> ai, 10, TimeUnit.MINUTES);
    }

    public void refreshAiSourceCache(Set<AISourceEnum> except) {
        refreshAiSourceCache(getRecommendAiSource(except));
    }

    /**
     * 高度封装的AI聊天访问入口，对于使用者而言，只需要提问，定义接受返回结果的回调即可
     *
     * @param question  提出的问题
     * @param callback  定义异步聊天接口返回时的回调策略
     * @return  表示同步直接返回的结果
     */
    public ChatRecordsVo autoChat(String question, Consumer<ChatRecordsVo> callback) {
        AISourceEnum source = getRecommendAiSource();
        return autoChat(source, question, callback);
    }

    /**
     * 自动根据AI的支持方式，选择同步/异步的交互方式
     *
     * @param source
     * @param question
     * @param callback
     * @return
     */
    public ChatRecordsVo autoChat(AISourceEnum source, String question, Consumer<ChatRecordsVo> callback) {
        if (source.asyncSupport() && chatServiceFactory.getChatService(source).asyncFirst()) {
            // 支持异步且该模型是设置的异步优先的场景，自动选择异步方式进行聊天
            return asyncChat(source, question, callback);
        }
        return chat(source, question, callback);
    }

    /**
     * 开始聊天
     *
     * @param source
     * @param question
     * @return
     */
    public ChatRecordsVo chat(AISourceEnum source, String question) {
        return chatServiceFactory.getChatService(source).chat(ReqInfoContext.getReqInfo().getUserId(), question);
    }

    /**
     * 开始聊天
     *
     * @param source
     * @param question
     * @param callback
     * @return
     */
    public ChatRecordsVo chat(AISourceEnum source, String question, Consumer<ChatRecordsVo> callback) {
        return chatServiceFactory.getChatService(source)
                .chat(ReqInfoContext.getReqInfo().getUserId(), question, callback);
    }

    /**
     * 异步聊天的方式
     *
     * @param source
     * @param question
     * @param callback
     * @return
     */
    public ChatRecordsVo asyncChat(AISourceEnum source, String question, Consumer<ChatRecordsVo> callback) {
        return chatServiceFactory.getChatService(source)
                .asyncChat(ReqInfoContext.getReqInfo().getUserId(), question, callback);
    }

    /**
     * 返回历史聊天记录
     *
     * @param source
     * @return
     */
    public ChatRecordsVo history(AISourceEnum source) {
        source = source == null ? getRecommendAiSource() : source;
        return chatServiceFactory.getChatService(source).getChatHistory(ReqInfoContext.getReqInfo().getUserId(), source);
    }

}
