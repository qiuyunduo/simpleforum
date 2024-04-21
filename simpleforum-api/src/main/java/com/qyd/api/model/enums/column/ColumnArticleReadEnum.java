package com.qyd.api.model.enums.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 专栏文章的阅读类型
 *
 * @author 邱运铎
 * @date 2024-04-10 22:30
 */
@AllArgsConstructor
@Getter
public enum ColumnArticleReadEnum {
    COLUMN_TYPE(0, "沿用专栏的类型"),
    LOGIN(1, "登录阅读"),
    TIME_FREE(2, "免费"),
    STAR_READ(3, "星球阅读"),
    ;

    private int read;

    private String desc;

    private static Map<Integer, ColumnArticleReadEnum> cache;

    static {
        cache = new HashMap<>();
        for (ColumnArticleReadEnum columnArticleReadEnum : values()) {
            cache.put(columnArticleReadEnum.getRead(), columnArticleReadEnum);
        }
    }

    public static ColumnArticleReadEnum valueOf(int val) {
        return cache.getOrDefault(val, COLUMN_TYPE);
    }
}
