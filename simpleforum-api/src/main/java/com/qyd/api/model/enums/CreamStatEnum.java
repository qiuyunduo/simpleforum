package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 加精状态枚举
 *
 * @author 邱运铎
 * @date 2024-05-18 16:31
 */
@Getter
@AllArgsConstructor
public enum CreamStatEnum {

    NOT_CREAM(0, "不加精"),
    CREAM(1, "加精"),
    ;

    private final Integer code;
    private final String desc;

    public static CreamStatEnum fromCode(Integer code) {
        for (CreamStatEnum stat : CreamStatEnum.values()) {
            if (stat.getCode().equals(code)) {
                return stat;
            }
        }
        return CreamStatEnum.NOT_CREAM;
    }
}
