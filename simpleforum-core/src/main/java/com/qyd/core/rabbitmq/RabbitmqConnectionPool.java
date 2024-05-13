package com.qyd.core.rabbitmq;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

/**
 * rabbitmq连接池
 *
 * @author 邱运铎
 * @date 2024-05-08 11:14
 */
public class RabbitmqConnectionPool {

    /**
     * 这里的RabbitmqConnection每个对象中都new 了一个rabbitmqFactory工厂，有点冗余
     * 我认为这里连接池中应该是Connection对象
     * RabbitmqConnection中不应该包含创建工厂的代码
     * todo: 改进去除掉每个连接中存在的连接工厂
     */
    private static BlockingQueue<RabbitmqConnection> pool;

    public static void initRabbitmqConnectionPool(String host, int port, String username,
                                                  String password, String virtualhost, Integer poolSize) {
        pool = new LinkedBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            pool.add(new RabbitmqConnection(host, port, username, password, virtualhost));
        }
    }

    /**
     * 从连接池获取连接
     * @return
     * @throws InterruptedException
     */
    public static RabbitmqConnection getConnection() throws InterruptedException {
        return pool.take();
    }

    /**
     * 归还连接给连接池
     *
     * @param connection
     */
    public static void returnConnection(RabbitmqConnection connection) {
        pool.add(connection);
    }

    public static void close() {
        pool.forEach(RabbitmqConnection::close);
    }
}
