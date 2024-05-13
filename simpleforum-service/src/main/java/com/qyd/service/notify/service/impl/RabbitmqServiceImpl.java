package com.qyd.service.notify.service.impl;

import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.core.common.CommonConstants;
import com.qyd.core.rabbitmq.RabbitmqConnection;
import com.qyd.core.rabbitmq.RabbitmqConnectionPool;
import com.qyd.core.util.JsonUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.notify.service.NotifyService;
import com.qyd.service.notify.service.RabbitmqService;
import com.qyd.service.user.repository.entity.UserFootDO;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author 邱运铎
 * @date 2024-05-08 11:08
 */
@Slf4j
@Service
public class RabbitmqServiceImpl implements RabbitmqService {
    @Autowired
    private NotifyService notifyService;

    @Override
    public boolean enabled() {
        return "true".equalsIgnoreCase(SpringUtil.getConfig("rabbitmq.switchFlag"));
    }

    @Override
    public void publishMsg(String exchange, BuiltinExchangeType exchangeType, String routingKey, String message) throws IOException, TimeoutException {
        try {
            // 创建链接
            RabbitmqConnection rabbitmqConnection = RabbitmqConnectionPool.getConnection();
            Connection connection = rabbitmqConnection.getConnection();
            // 创建消息通道
            Channel channel = connection.createChannel();
            // 声明exchange中的消息为可持久化，不自动删除
            channel.exchangeDeclare(exchange, exchangeType, true, false, null);
            // 发布消息
            channel.basicPublish(exchange, routingKey, null, message.getBytes());
            log.info("Publish msg: {}", message);
            channel.close();
            RabbitmqConnectionPool.returnConnection(rabbitmqConnection);
        } catch (InterruptedException | IOException |TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consumerMsg(String exchange, String queue, String routingKey) {
        try {
            // 创建链接
            RabbitmqConnection rabbitmqConnection = RabbitmqConnectionPool.getConnection();
            Connection connection = rabbitmqConnection.getConnection();
            // 创建消息信道
            final Channel channel = connection.createChannel();
            // 消息队列
            channel.queueDeclare(queue, true, false, false, null);
            // 这里按照正常情况时不需要对exchange进行声明创建的，因为消费消息默认是在生产消息之后运行的。而生产消息运行之后exchange一定是先创建出来
            // 这里的消费消息其实按照正常逻辑是在生产消息之后才会执行，但是本程序在应用启动后会开启一个线程循环拉取该消息队列中的消息
            // 这样会导致在应用启动时，因为没有触发生产消息的情况，导致exchange没有先行创建出来，导致此处创建队列后绑定exchange出现异常。
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT,true, false, null);
            // 绑定队列到交换机
            channel.queueBind(queue, exchange, routingKey);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    log.info("Consumer msg: {}", message);

                    // 获取Rabbitmq消息，并保存到DB, 这里来看主要是点赞
                    // 说明： 这里仅作为实例， 如果有多种类型的消息，可以根据消息判定， 简单的用if...else 复杂的用工厂 + 策略
                    notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.PRAISE);

                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            // 取消自动ack
//            channel.basicConsume(queue, false, consumer);
            channel.close();
            RabbitmqConnectionPool.returnConnection(rabbitmqConnection);
        } catch (InterruptedException | IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启rabbitmq中的消费者进程
     */
    @Override
    public void processConsumerMsg() {
        log.info("Begin to processConsumerMsg");

        Integer stepTotal = 1;
        Integer step = 0;

        // todo: 这种方式非常 Low, 后续会改造成阻塞 I/O 模式
        while (true) {
            step++;
            try {
                log.info("processConsumerMsg cycle.");
                consumerMsg(CommonConstants.EXCHANGE_NAME_DIRECT, CommonConstants.QUEUE_NAME_PRAISE,
                        CommonConstants.QUEUE_KEY_PRAISE);
                if (step.equals(stepTotal)) {
                    Thread.sleep(10000);
                    step = 0;
                }
            } catch (Exception e) {

            }
        }
    }
}
