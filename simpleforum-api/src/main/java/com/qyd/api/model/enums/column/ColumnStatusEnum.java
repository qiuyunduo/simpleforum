package com.qyd.api.model.enums.column;

import lombok.Getter;

/**
 * 专栏发布状态枚举
 *
 * @author 邱运铎
 * @date 2024-04-25 12:58
 */
@Getter
public enum ColumnStatusEnum {
    OFFLINE(0, "未发布"),
    CONTINUE(1, "连载"),
    OVER(2, "已完结");

    ColumnStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;

    public static ColumnStatusEnum fromCode(int code) {
        for (ColumnStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return ColumnStatusEnum.OFFLINE;
    }
}
