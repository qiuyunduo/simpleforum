package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户主页关注相关条目枚举
 *
 * @author 邱运铎
 * @date 2024-05-09 0:01
 */
@Getter
@AllArgsConstructor
public enum FollowTypeEnum {
    FOLLOW("follow", "我关注的用户"),
    FANS("fans", "关注我的粉丝");

    private final String code;
    private final String desc;

    public static FollowTypeEnum fromCode(String code) {
        for (FollowTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equalsIgnoreCase(code)) {
                return typeEnum;
            }
        }
        return FOLLOW;
    }
}
