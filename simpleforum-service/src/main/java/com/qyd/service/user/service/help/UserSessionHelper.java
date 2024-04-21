package com.qyd.service.user.service.help;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.qyd.core.cache.RedisClient;
import com.qyd.core.mdc.SelfTraceIdGenerator;
import com.qyd.core.util.JsonUtil;
import com.qyd.core.util.MapUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author 邱运铎
 * @date 2024-04-21 11:24
 */
@Slf4j
@Component
public class UserSessionHelper {

    @Component
    @Data
    @ConfigurationProperties("forum.jwt")
    public static class JwtProperties {
        /**
         * 签发人
         */
        private String issuer;

        /**
         * 密钥
         */
        private String secret;

        /**
         * 有效期， 毫秒时间戳
         */
        private Long expire;
    }

    private final JwtProperties jwtProperties;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    public UserSessionHelper(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        verifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
    }

    public String genSession(Long userId) {
        // 1. 生成jwt格式的会话，内部持有有效期，用户信息
        String session = JsonUtil.toStr(MapUtils.create("s", SelfTraceIdGenerator.generate(), "u", userId));
        String token = JWT.create()
                        .withIssuer(jwtProperties.getIssuer())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
                .withPayload(session)
                .sign(algorithm);

        // 2. 使用jwt生成的token时，后端可以不存储这个session信息，完全依赖jwt的信息
        // 但是需要考虑到用户登出，需要主动失效这个token, 而jwt本身无状态，所以在这里的redis做一个简单的token -> userId的缓存，用于双重判定
        RedisClient.setStrWithExpire(token, String.valueOf(userId), jwtProperties.getExpire() / 1000);
        return token;
    }

    public void removeSession(String session) {
        RedisClient.del(session);
    }

    /**
     * 更具会话获取用户信息
     *
     * @param session   jwt签发的token,叫token感觉更好些
     * @return
     */
    public Long getUserIdBySession(String session) {
        // jwt的校验方式，如果token非法或者过期，则直接验签失败
        try {
            DecodedJWT decodedJWT = verifier.verify(session);
            String pay = new String(Base64Utils.decodeFromString(decodedJWT.getPayload()));
            // jwt验证通过，获取对应的userId
            String userId = String.valueOf(JsonUtil.toObj(pay, HashMap.class).get("u"));

            // 从redis中获取userId, 解决用户登，后台失效jwt token 问题
            String user = RedisClient.getStr(session);
            if (user == null || !Objects.equals(userId, user)) {
                return null;
            }
            return Long.valueOf(user);
        } catch (Exception e) {
            log.info("jwt token校验失败！ token: {}, msg: {}", session, e.getMessage());
            return null;
        }
    }
}
