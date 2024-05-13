package com.qyd.service.notify.service;

import com.rabbitmq.client.BuiltinExchangeType;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 邱运铎
 * @date 2024-05-08 10:51
 */
public interface RabbitmqService {

    boolean enabled();

    /**
     * 发布消息
     *
     * @param exchange      接受消息转发消息的交换机
     * @param exchangeType  交换机类型
     * @param routingKey    交换机到消息队列的路由Key
     * @param message       消息体
     * @throws IOException
     * @throws TimeoutException
     */
    void publishMsg(String exchange,
                    BuiltinExchangeType exchangeType,
                    String routingKey,
                    String message) throws IOException, TimeoutException;

    /**
     * 消费消息
     *
     * @param exchange      交换机
     * @param queue         消息队列
     * @param routingKey    路由key
     */
    void consumerMsg(String exchange,
                     String queue,
                     String routingKey);

    void processConsumerMsg();
}
