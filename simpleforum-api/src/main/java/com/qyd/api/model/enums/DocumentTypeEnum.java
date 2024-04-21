package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-04-15 21:55
 */
@AllArgsConstructor
@Getter
public enum DocumentTypeEnum {
    EMPTY(0, ""),
    ARTICLE(1, "文章"),
    COMMENT(2, "评论");

    private final Integer code;
    private final String desc;

    private static Map<Integer, DocumentTypeEnum> cache;

    static {
        cache = new HashMap<>();
        for (DocumentTypeEnum item : values()) {
            cache.put(item.getCode(), item);
        }
    }

    public static DocumentTypeEnum from(Integer code) {
        return cache.getOrDefault(code, EMPTY);
    }
}
