package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-05-10 23:31
 */

@Getter
@AllArgsConstructor
public enum ChatSocketStateEnum {
    /**
     * ws连接建立成功
     */
    ESTABLISHED(0, "Established"),

    /**
     * websocket传送的消息
     */
    PAYLOAD(1, "Payload"),

    /**
     * websocket连接关闭
     */
    CLOSED(2, "Closed"),

    ;
    private final Integer code;
    private final String desc;

    private static Map<Integer, ChatSocketStateEnum> cache;

    static {
        cache = new HashMap<>();
        for (ChatSocketStateEnum state : values()) {
            cache.put(state.getCode(), state);
        }
    }

    public static ChatSocketStateEnum typeOf(int type) {
        return cache.getOrDefault(type, null);
    }

    public static ChatSocketStateEnum typeOf(String type) {
        return valueOf(type.toUpperCase().trim());
    }
}
