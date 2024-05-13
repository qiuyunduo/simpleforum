package com.qyd.service.chatai.service;

import com.google.common.collect.Maps;
import com.qyd.api.model.enums.ai.AISourceEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-05-09 18:24
 */
@Component
public class ChatServiceFactory {
    private final Map<AISourceEnum, ChatService> chatServiceMap;

    public ChatServiceFactory(List<ChatService> chatServicesList) {
        chatServiceMap = Maps.newHashMapWithExpectedSize(chatServicesList.size());
        for (ChatService chatService : chatServicesList) {
            chatServiceMap.put(chatService.source(), chatService);
        }
    }

    public ChatService getChatService(AISourceEnum aiSource) {
        return chatServiceMap.get(aiSource);
    }
}
