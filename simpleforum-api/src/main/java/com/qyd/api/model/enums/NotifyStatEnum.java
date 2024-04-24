package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 邱运铎
 * @date 2024-04-24 22:39
 */
@Getter
@AllArgsConstructor
public enum NotifyStatEnum {
    UNREAD(0, "未读"),
    READ(1, "已读"),
    ;

    private int stat;
    private String msg;
}
