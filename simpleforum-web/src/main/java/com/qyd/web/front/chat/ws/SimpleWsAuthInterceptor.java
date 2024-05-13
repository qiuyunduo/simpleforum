package com.qyd.web.front.chat.ws;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.core.mdc.MdcUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.web.global.GlobalInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * v1. 简单版本聊天： 长连接的登录校验拦截器
 * HttpSessionHandshakeInterceptor 拦截ws请求前做的一些事情，经常通过实现覆盖其中的方法实现自定义的指定增强
 * ChannelInterceptor 拦截器拦截 ws 协议的请求/响应
 *
 * @author 邱运铎
 * @date 2024-05-13 1:39
 */
@Slf4j
public class SimpleWsAuthInterceptor extends HttpSessionHandshakeInterceptor implements ChannelInterceptor {

    @Override
    public boolean preReceive(MessageChannel channel) {
        return ChannelInterceptor.super.preReceive(channel);
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String session = ((ServletServerHttpRequest) request).getServletRequest().getParameter("session");
        ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
        SpringUtil.getBean(GlobalInitService.class).initLoginUser(session, reqInfo);
        ReqInfoContext.addReqInfo(reqInfo);
        if (reqInfo.getUserId() == null) {
            // 未登录，拒绝连接
            log.info("用户未登录，拒绝聊天！");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        log.info("{}， 开始了聊天！", reqInfo);
        MdcUtil.addTraceId();
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        ReqInfoContext.clear();
        MdcUtil.clear();
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
