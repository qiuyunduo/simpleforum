package com.qyd.api.model.vo.chat;

import com.qyd.api.model.enums.ChatAnswerTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 一次AI问答的聊天记录
 *
 * @author 邱运铎
 * @date 2024-04-21 16:48
 */
@Data
@Accessors(chain = true)
public class ChatItemVo implements Serializable, Cloneable {
    private static final long serialVersionUID = 7230339040247758226L;

    /**
     * 唯一的聊天id, 不要求存在，主要用于简化流式输出时，前端对返回结果的处理
     */
    private String chatUid;

    /**
     * 提问的内容
     */
    private String question;

    /**
     * 提问的时间点
     */
    private String questionTime;

    /**
     * 回答的内容
     */
    private String answer;

    /**
     * 回答的时间点
     */
    private String answerTime;

    /**
     * 回答的内容类型，文本、JSON、字符串
     */
    private ChatAnswerTypeEnum answerType;

    /**
     * 记录问题及记录时间
     *
     * @param question
     * @return
     */
    public ChatItemVo initQuestion(String question) {
        this.question = question;
        this.questionTime = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss").format(LocalDateTime.now());
        return this;
    }

    public ChatItemVo initAnswer(String answer) {
        this.answer = answer;
        this.answerType = ChatAnswerTypeEnum.TEXT;
        setAnswerTime();
        return this;
    }

    /**
     * 流式的追加返回
     *
     * @param answer
     * @return
     */
    public ChatItemVo appendAnswer(String answer) {
        if (this.answer == null || this.answer.isEmpty()) {
            this.answer = answer;
            this.chatUid = UUID.randomUUID().toString().replace("-", "");
        } else {
            this.answer += answer;
        }
        this.answerType = ChatAnswerTypeEnum.STREAM;
        setAnswerTime();
        return this;
    }

    public ChatItemVo setAnswerTime() {
        this.answerTime = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss").format(LocalDateTime.now());
        return this;
    }

    @Override
    public ChatItemVo clone() {
        ChatItemVo itemVo = new ChatItemVo();
        itemVo.question = question;
        itemVo.questionTime = questionTime;
        return itemVo;
    }
}
