package com.qyd.service.chatai.service.impl.pai;

import com.qyd.api.model.enums.ChatAnswerTypeEnum;
import com.qyd.api.model.enums.ai.AISourceEnum;
import com.qyd.api.model.enums.ai.AiChatStatEnum;
import com.qyd.api.model.vo.chat.ChatItemVo;
import com.qyd.api.model.vo.chat.ChatRecordsVo;
import com.qyd.core.async.AsyncUtil;
import com.qyd.service.chatai.service.AbsChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

/**
 * 技术派价值一个亿(手动笑哭)
 *
 * @author 邱运铎
 * @date 2024-05-09 23:50
 */
@Service
public class PaiAIDemoServiceImpl extends AbsChatService {
    @Override
    public AiChatStatEnum doAnswer(Long user, ChatItemVo chat) {
        chat.initAnswer(qa(chat.getQuestion()));
        return AiChatStatEnum.END;
    }

    @Override
    public AiChatStatEnum doAsyncAnswer(Long user, ChatRecordsVo response, BiConsumer<AiChatStatEnum, ChatRecordsVo> consumer) {
        AsyncUtil.execute(() -> {
            AsyncUtil.sleep(15000);
            ChatItemVo item = response.getRecords().get(0);
            item.appendAnswer(qa(item.getQuestion()));
            consumer.accept(AiChatStatEnum.FIRST, response);

            AsyncUtil.sleep(1200);
            item.appendAnswer("\n" + qa(item.getQuestion()));
            item.setAnswerType(ChatAnswerTypeEnum.STREAM_END);
            consumer.accept(AiChatStatEnum.END, response);
        });
        return AiChatStatEnum.END;
    }

    @Override
    public AISourceEnum source() {
        return AISourceEnum.PAI_AI;
    }

    private String qa(String q) {
        String ans = q.replaceAll("吗", "");
        ans = StringUtils.replace(ans, "?", "!");
        ans = StringUtils.replace(ans, "？", "!");
        return ans;
    }

    @Override
    protected int getMaxQaCnt(Long user) {
        return 65535;
    }
}
