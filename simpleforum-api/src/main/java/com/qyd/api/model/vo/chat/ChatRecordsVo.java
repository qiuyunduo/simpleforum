package com.qyd.api.model.vo.chat;

import com.qyd.api.model.enums.ai.AISourceEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天记录
 *
 * @author 邱运铎
 * @date 2024-05-09 18:27
 */
@Data
@Accessors(chain = true)
public class ChatRecordsVo implements Serializable, Cloneable {
    private static final long serialVersionUID = 6485639011811935815L;

    /**
     * AI类型，目前讯飞，chatGpt3.5(openAI)
     */
    private AISourceEnum source;

    /**
     * 当前用户最多的可问答的次数
     */
    private int maxCnt;

    /**
     * 使用的次数
     */
    private int usedCnt;

    /**
     * 聊天记录， 最新的在前面，最多返回50条
     */
    private List<ChatItemVo> records;

    @Override
    public ChatRecordsVo clone() {
        ChatRecordsVo vo = new ChatRecordsVo();
        vo.source = source;
        vo.maxCnt = maxCnt;
        vo.usedCnt = usedCnt;
        if (records != null) {
            vo.setRecords(records.stream().map(ChatItemVo::clone).collect(Collectors.toList()));
        }
        return vo;
    }

    /**
     * 判断是否拥有提问次数
     *
     * @return
     */
    public boolean hasQaCnt() {
        return maxCnt > usedCnt;
    }
}
