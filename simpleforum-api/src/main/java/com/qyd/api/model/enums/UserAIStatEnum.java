package com.qyd.api.model.enums;

import lombok.Getter;

/**
 * 派聪明用户状态枚举  --对于我来说这个暂时没用，我没法集成AI
 *
 * @author 邱运铎
 * @date 2024-04-11 1:01
 */
@Getter
public enum UserAIStatEnum {
    IGNORE(-1, "忽略"),
    // 审核中
    AUDITING(0, "审核中"),
    // 试用中
    TRYING(1, "试用中"),
    // 正式用户
    FORMAL(2, "正式用户"),
    // 未通过
    NOT_PASS(3, "未通过");

    UserAIStatEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static UserAIStatEnum fromCode(Integer code) {
        for (UserAIStatEnum value : UserAIStatEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return UserAIStatEnum.AUDITING;
    }
}
