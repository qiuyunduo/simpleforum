package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-04-15 20:54
 */
@AllArgsConstructor
@Getter
public enum OfficialStatEnum {

    NOT_OFFICIAL(0, "非官方"),
    OFFICIAL(1, "官方");

    private final Integer code;
    private final String desc;

    private static Map<Integer, OfficialStatEnum> cache;

    static {
        cache = new HashMap<>();
        for (OfficialStatEnum item : values()) {
            cache.put(item.getCode(), item);
        }
    }

    public static OfficialStatEnum fromCode(Integer code) {
        return cache.getOrDefault(code, NOT_OFFICIAL);

    }
}
