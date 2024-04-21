package com.qyd.api.model.enums;

import lombok.Getter;

/**
 * 文章来源枚举类
 *
 * @author 邱运铎
 * @date 2024-04-10 21:22
 */
@Getter
public enum SourceTypeEnum {

    EMPTY(0, ""),
    REPRINT(1, "转载"),
    ORIGINAL(2, "原创"),
    TRANSLATION(3, "翻译");

    SourceTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static SourceTypeEnum fromCode(Integer code) {
        for (SourceTypeEnum sourceTypeEnum : SourceTypeEnum.values()) {
            if (sourceTypeEnum.getCode().equals(code)) {
                return sourceTypeEnum;
            }
        }
        return SourceTypeEnum.EMPTY;
    }
}
