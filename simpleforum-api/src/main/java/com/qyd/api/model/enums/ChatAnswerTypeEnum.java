package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 邱运铎
 * @date 2024-04-21 16:51
 */
@Getter
@AllArgsConstructor
public enum ChatAnswerTypeEnum {

    /**
     * 纯文本
     */
    TEXT(0, "TEXT"),
    /**
     * JSON格式
     */
    JSON(1, "JSON"),
    /**
     * 流式返回
     */
    STREAM(2, "STREAM"),
    /**
     * 流式结束
     */
    STREAM_END(3, "STREAM_END"),
    ;

    private Integer code;
    private String desc;
}
