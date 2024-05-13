package com.qyd.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RabbitMQ配置文件
 *
 * @author 邱运铎
 * @date 2024-05-08 11:15
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitmqProperties {

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 虚拟主机，路径
     */
    private String virtualhost;

    /**
     * rabbitmq连接池大小
     */
    private Integer poolSize;

    /**
     * rabbitmq启用开关 false-关闭， true-打开
     */
    private Boolean switchFlag;
}
