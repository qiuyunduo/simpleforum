package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-04-25 17:36
 */
@Getter
@AllArgsConstructor
public enum ArticleEventEnum {
    CREATE(1, "创建"),
    ONLINE(2, "发布"),
    REVIEW(3, "审核"),
    DELETE(4, "删除"),
    OFFLINE(5, "下线")
    ;

    private int type;
    private String msg;

    private static Map<Integer, ArticleEventEnum> mapper;

    static {
        mapper = new HashMap<>();
        for (ArticleEventEnum type : values()) {
            mapper.put(type.type, type);
        }
    }

    public static ArticleEventEnum typeOf(int type) {
        return mapper.get(type);
    }

    public static ArticleEventEnum typeOf(String type) {
        return valueOf(type.toUpperCase().trim());
    }
}
