package com.qyd.api.model.enums.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 邱运铎
 * @date 2024-04-21 16:42
 */
@Getter
@AllArgsConstructor
public enum AISourceEnum {
    /**
     * chatGpt 3.5
     */
    CHAT_GPT_3_5(0, "chatGpt3.5"),
    /**
     * chatGpt4
     */
    CHAT_GPT_4(1, "chatGpt4"),
    /**
     * 网站的模拟AI
     */
    PAI_AI(2, "技术派"),
    /**
     * 讯飞
     */
    XUN_FEI_AI(3, "讯飞") {
        @Override
        public boolean syncSupport() { return  false; }
    },
    ;


    private Integer code;
    private String name;

    /**
     * 是否支持同步
     *
     * @return
     */
    public boolean syncSupport() {
        return true;
    }

    /**
     * 是否支持异步
     *
     * @return
     */
    public boolean asyncSupport() {
        return true;
    }
}
