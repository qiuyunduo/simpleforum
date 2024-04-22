package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置类型枚举
 *
 * @author 邱运铎
 * @date 2024-04-22 16:47
 */
@Getter
@AllArgsConstructor
public enum ConfigTagEnum {
    EMPTY(0, ""),
    HOT(1, "热门"),
    OFFICIAL(2, "官方"),
    COMMENT(3, "推荐"),
    ;

    private final Integer code;
    private final String desc;

    private static Map<Integer, ConfigTagEnum> cache;

    static {
        cache = new HashMap<>();
        for (ConfigTagEnum tag : values()) {
            cache.put(tag.getCode(), tag);
        }
    }

    public static ConfigTagEnum fromCode(Integer code) {
        return cache.getOrDefault(code, EMPTY);
    }
}
