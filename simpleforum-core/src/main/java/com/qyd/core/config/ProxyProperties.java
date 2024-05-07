package com.qyd.core.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-07 11:44
 */
@Data
@ConfigurationProperties(prefix = "net")
public class ProxyProperties {

    private List<ProxyType> proxy;

    @Data
    @Accessors(chain = true)
    public static class ProxyType {
        /**
         * 代理类型
         */
        private Proxy.Type type;

        /**
         * 代理IP
         */
        private String ip;

        /**
         * 代理端口
         */
        private Integer port;
    }
}
