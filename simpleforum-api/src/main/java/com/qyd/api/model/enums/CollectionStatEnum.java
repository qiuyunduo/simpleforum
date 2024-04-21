package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏状态枚举
 *
 * @author 邱运铎
 * @date 2024-04-16 0:01
 */
@AllArgsConstructor
@Getter
public enum CollectionStatEnum {

    NOT_COLLECTION(0, "未收藏"),
    COLLECTION(1, "已收藏"),
    CANCEL_COLLECTION(2, "取消收藏");

    private final Integer code;
    private final String desc;

    private static Map<Integer, CollectionStatEnum> cache;

    static {
        cache = new HashMap<>();
        for (CollectionStatEnum item : values()) {
            cache.put(item.getCode(), item);
        }
    }

    public static CollectionStatEnum fromCode(Integer code) {
        return cache.getOrDefault(code, NOT_COLLECTION);
    }
}
