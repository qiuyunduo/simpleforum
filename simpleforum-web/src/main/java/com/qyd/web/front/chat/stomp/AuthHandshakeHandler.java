package com.qyd.web.front.chat.stomp;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.service.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 握手处理器
 *
 * @author 邱运铎
 * @date 2024-05-13 12:28
 */
@Slf4j
public class AuthHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // case1: 根据cookie来识别用户，既可以实现所有用户连相同的ws地址，然后在 AuthHandshakeChannelInterceptor 中进行destination的转发
        ReqInfoContext.ReqInfo reqInfo = (ReqInfoContext.ReqInfo) attributes.get(LoginService.SESSION_KEY);
        if (reqInfo != null) {
            return reqInfo;
        }

        // case2: 根据路径来区分用户
        // 获取例如 ws://localhost/gpt/id 订阅地址中的最后一个用户 id 参数作为用户标识， 为实现发送信息给指定用户做准备
        String uri = request.getURI().toString();
        String uid = uri.substring(uri.lastIndexOf("/") + 1);
        log.info("{} -> {}", uri, uid);
        return () -> uid;
    }
}
