package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户关系列表
 *
 * @author 邱运铎
 * @date 2024-04-25 13:27
 */
@Getter
@AllArgsConstructor
public enum FollowSelectEnum {
    FOLLOW("follow", "关注列表"),
    FANS("fans", "粉丝列表"),
    ;

    private final String code;
    private final String desc;

    private static Map<String, FollowSelectEnum> cache;

    static {
        cache = new HashMap<>();
        for (FollowSelectEnum select : values()) {
            cache.put(select.code, select);
        }
    }

    public static FollowSelectEnum fromCode(String code) {
        return cache.getOrDefault(code, FOLLOW);
    }
}
