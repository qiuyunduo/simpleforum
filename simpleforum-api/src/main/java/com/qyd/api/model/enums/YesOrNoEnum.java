package com.qyd.api.model.enums;

import lombok.Getter;

/**
 * 状态的枚举
 *
 * @author 邱运铎
 * @date 2024-04-09 15:28
 */
@Getter
public enum YesOrNoEnum {
    NO(0, "N", "否", "no"),
    YES(1, "Y", "是", "yes");

    YesOrNoEnum(int code, String desc, String cnDesc, String enDesc) {
        this.code = code;
        this.desc = desc;
        this.cnDesc = cnDesc;
        this.enDesc = enDesc;
    }

    private final int code;
    private final String desc;
    private final String cnDesc;
    private final String enDesc;

    public static YesOrNoEnum from(int code) {
        for (YesOrNoEnum yesOrNoEnum : YesOrNoEnum.values()) {
            if (yesOrNoEnum.getCode() == code) {
                return yesOrNoEnum;
            }
        }
        return YesOrNoEnum.NO;
    }

    /**
     * 判断code是否符合该枚举类的定义， 主要用于某些场景字段为赋值的情况
     *
     * @param code
     * @return
     */
    public static boolean equalYN(Integer code) {
        if (code == null) {
            return false;
        }
        if (code != null && (code.equals(YES.code) || code.equals(NO.code))) {
            return true;
        }
        return false;
    }

    public static boolean isYes(Integer code) {
        if (code == null) {
            return false;
        }

        return YesOrNoEnum.YES.getCode() == code;
    }
}
