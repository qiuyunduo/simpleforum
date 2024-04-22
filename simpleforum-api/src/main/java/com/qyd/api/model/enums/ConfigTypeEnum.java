package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置类型枚举
 *
 * @author 邱运铎
 * @date 2024-04-22 16:41
 */
@Getter
@AllArgsConstructor
public enum ConfigTypeEnum {
    EMPTY(0, ""),
    HOME_PAGE(1, "首页Banner"),
    SIDE_PAGE(2, "侧边Banner"),
    ADVERTISEMENT(3, "广告Banner"),
    NOTICE(4, "公告"),
    COLUMN(5, "教程"),
    PDF(6, "电子书");

    private final Integer code;
    private final String desc;

    private static Map<Integer, ConfigTypeEnum> cache;

    static {
        cache = new HashMap<>();
        for (ConfigTypeEnum config : values()) {
            cache.put(config.getCode(), config);
        }
    }

    public static ConfigTypeEnum fromCode(Integer code) {
        return cache.getOrDefault(code, EMPTY);
    }
}
