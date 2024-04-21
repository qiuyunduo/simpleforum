package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 评论状态枚举
 *
 * @author 邱运铎
 * @date 2024-04-16 0:03
 */
@AllArgsConstructor
@Getter
public enum CommentStatEnum {

    NOT_COMMENT(0, "未评论"),
    COMMENT(1, "已评论"),
    DELETE_COMMENT(2, "删除评论");

    private final Integer code;
    private final String desc;

    private static Map<Integer, CommentStatEnum> cache;

    static {
        cache = new HashMap<>();
        for (CommentStatEnum item : values()) {
            cache.put(item.getCode(), item);
        }
    }

    public static CommentStatEnum fromCode(Integer code) {
        return cache.getOrDefault(code, NOT_COMMENT);
    }
}
