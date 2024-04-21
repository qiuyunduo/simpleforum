package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 点赞状态枚举
 *
 * @author 邱运铎
 * @date 2024-04-15 23:59
 */
@AllArgsConstructor
@Getter
public enum PraiseStatEnum {
    NOT_PRAISE(0, "未点赞"),
    PRAISE(1, "已点赞"),
    CANCEL_PRAISE(2, "取消点赞");

    private final Integer code;
    private final String desc;

    private static Map<Integer, PraiseStatEnum> cache;

    static {
        cache = new HashMap<>();
        for (PraiseStatEnum item : values()) {
            cache.put(item.getCode(), item);
        }
    }

    public static PraiseStatEnum fromCode(Integer code) {
        return cache.getOrDefault(code, NOT_PRAISE);
    }
}
