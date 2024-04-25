package com.qyd.api.model.enums.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 教程阅读类型枚举
 *
 * @author 邱运铎
 * @date 2024-04-25 12:15
 */
@Getter
@AllArgsConstructor
public enum ColumnTypeEnum {
    FREE(0, "免费"),
    LOGIN(1, "登录阅读"),
    TIME_FREE(2, "限时免费"),
    STAR_READ(3, "星球阅读")
    ;

    private final int type;
    private final String desc;

    public static ColumnTypeEnum fromCode(int code) {
        for (ColumnTypeEnum typeEnum : values()) {
            if (typeEnum.getType() == code) {
                return typeEnum;
            }
        }
        return ColumnTypeEnum.FREE;
    }

}
