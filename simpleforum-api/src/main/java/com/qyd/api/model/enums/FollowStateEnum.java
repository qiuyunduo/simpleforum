package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 关注枚举类
 *
 * @author 邱运铎
 * @date 2024-04-20 20:01
 */
@Getter
@AllArgsConstructor
public enum FollowStateEnum {

    EMPTY(0, ""),
    FOLLOW(1, "关注"),
    CANCEL_FOLLOW(2, "取消关注")
    ;

    private final Integer code;
    private final String desc;

    private static Map<Integer, FollowStateEnum> cache;

    static {
        cache = new HashMap<>();
        for (FollowStateEnum stateEnum : values()) {
            cache.put(stateEnum.getCode(), stateEnum);
        }
    }

    public static FollowStateEnum fromCode(Integer code) {
        return cache.getOrDefault(code, EMPTY);
    }
}
