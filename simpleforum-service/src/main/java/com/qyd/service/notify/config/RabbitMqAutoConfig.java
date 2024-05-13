package com.qyd.service.notify.config;

import com.qyd.core.async.AsyncUtil;
import com.qyd.core.config.RabbitmqProperties;
import com.qyd.core.rabbitmq.RabbitmqConnectionPool;
import com.qyd.service.notify.service.RabbitmqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 程序启动后同时开启rabbitmq消费者进程
 *
 * @author 邱运铎
 * @date 2024-05-08 11:21
 */
@Configuration
@ConditionalOnProperty(value = "rabbitmq.switchFlag")
@EnableConfigurationProperties(RabbitmqProperties.class)
public class RabbitMqAutoConfig implements ApplicationRunner {

    @Resource
    private RabbitmqService rabbitmqService;

    @Autowired
    private RabbitmqProperties rabbitmqProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String host = rabbitmqProperties.getHost();
        Integer port = rabbitmqProperties.getPort();
        String userName = rabbitmqProperties.getUsername();
        String password = rabbitmqProperties.getPassword();
        String virtualhost = rabbitmqProperties.getVirtualhost();
        Integer poolSize = rabbitmqProperties.getPoolSize();
        RabbitmqConnectionPool.initRabbitmqConnectionPool(host, port, userName, password, virtualhost, poolSize);
        AsyncUtil.execute(() -> rabbitmqService.processConsumerMsg());
    }
}
