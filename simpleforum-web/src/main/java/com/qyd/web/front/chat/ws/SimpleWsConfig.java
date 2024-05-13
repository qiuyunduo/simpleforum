package com.qyd.web.front.chat.ws;

import com.qyd.web.front.chat.rest.SimpleChatGptHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * v1.0 基础版本的websocket长连接相关配置
 *
 * @author 邱运铎
 * @date 2024-05-13 1:35
 */
//@Configuration
//@EnableWebSocket
public class SimpleWsConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/chatGpt")
                .setAllowedOrigins("*")
                .addInterceptors(new SimpleWsAuthInterceptor());
    }

    @Bean
    public WebSocketHandler chatWebSocketHandler() {
        return new SimpleChatGptHandler();
    }
}
