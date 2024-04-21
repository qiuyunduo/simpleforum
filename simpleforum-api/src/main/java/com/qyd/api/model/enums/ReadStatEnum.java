package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 阅读状态枚举
 *
 * @author 邱运铎
 * @date 2024-04-15 23:50
 */
@AllArgsConstructor
@Getter
public enum ReadStatEnum {
    NOT_READ(0, "未读"),
    READ(1, "已读");

    private final Integer code;
    private final String desc;

    private static Map<Integer, ReadStatEnum> cache;

    static {
        cache = new HashMap<>();
        for (ReadStatEnum item : values()) {
            cache.put(item.getCode(), item);
        }
    }

    public static ReadStatEnum fromCode(Integer code) {
        return cache.getOrDefault(code, NOT_READ);
    }
}
