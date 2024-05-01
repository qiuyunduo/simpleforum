package com.qyd.core.util;

import javax.net.ServerSocketFactory;
import java.net.ServerSocket;
import java.util.Random;

/**
 * @author 邱运铎
 * @date 2024-05-01 15:13
 */
public class SocketUtil {

    /**
     * 通过是否能正常建立一个socketServer服务来判断端口是否可用
     *
     * @param port
     * @return
     */
    public static boolean isPortAvailable(int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1);
            serverSocket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Random random = new Random();

    private static int findRandomPort(int minPort, int maxPort) {
        int portRange = maxPort - minPort;
        return minPort + random.nextInt(portRange + 1);
    }

    /**
     * 在指定范围内找一个可用的端口
     *
     * @param minPort   最小端口号
     * @param maxPort   最大端口号
     * @param defaultPort   默认端口号
     * @return  默认端口号可用直接返回默认，否则在最小端口和最大端口之间随机找一个端口
     */
    public static int findAvailableTcpPort(int minPort, int maxPort, int defaultPort) {
        if (isPortAvailable(defaultPort)) {
            return defaultPort;
        }

        if (maxPort <= minPort) {
            throw new IllegalArgumentException("maxPort should bigger than minPort!");
        }
        int portRange = maxPort - minPort;
        int searchCounter = 0;

        while (searchCounter <= portRange) {
            int candidatePort = findRandomPort(minPort, maxPort);
            ++searchCounter;
            if (isPortAvailable(candidatePort)) {
                return candidatePort;
            }
        }

        throw new IllegalStateException(String.format("Could not find an available %s port int the range [%s, %d] after %d attempts", SocketUtil.class, minPort, maxPort, searchCounter));
    }
}
