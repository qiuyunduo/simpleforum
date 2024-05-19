package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 置顶状态枚举
 *
 * @author 邱运铎
 * @date 2024-05-18 16:26
 */
@Getter
@AllArgsConstructor
public enum ToppingStatEnum {

    NOT_TOPPING(0, "不置顶"),
    TOPPING(1, "置顶"),
    ;

    private final Integer code;
    private final String desc;

    public static ToppingStatEnum fromCode(Integer code) {
        for (ToppingStatEnum stat : ToppingStatEnum.values()) {
            if (stat.getCode().equals(code)) {
                return stat;
            }
        }
        return ToppingStatEnum.NOT_TOPPING;
    }
}
