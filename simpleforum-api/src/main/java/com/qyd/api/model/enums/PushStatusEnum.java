package com.qyd.api.model.enums;

import lombok.Getter;

/**
 * 文章发布状态枚举
 *
 * @author 邱运铎
 * @date 2024-04-09 14:54
 */
@Getter
public enum PushStatusEnum {
    OFFLINE(0, "未发布"),
    ONLINE(1, "已发布"),
    REVIEW(2, "审核");

    PushStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;

    public static PushStatusEnum fromCode(int code) {
        for (PushStatusEnum pushStatusEnum : PushStatusEnum.values()) {
            if (pushStatusEnum.getCode() == code) {
                return pushStatusEnum;
            }
        }
        return PushStatusEnum.OFFLINE;
    }
}
