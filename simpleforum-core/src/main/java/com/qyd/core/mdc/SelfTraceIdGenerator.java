package com.qyd.core.mdc;

import com.google.common.base.Splitter;
import com.qyd.core.util.IpUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.SocketException;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 自定义的traceId生成器
 * <p>
 * 生成规则参考 <a href="https://help.aliyun.com/document_detail/151840.html"/>
 *
 * @author 邱运铎
 * @date 2024-04-21 11:40
 */
@Slf4j
public class SelfTraceIdGenerator {
    private final static Integer MIN_AUTO_NUMBER = 1000;
    private final static Integer MAX_AUTO_NUMBER = 10000;
    private static volatile Integer autoIncreaseNumber = MIN_AUTO_NUMBER;

    /**
     * <p>
     * 生成32未traceId, 规则是 服务器 IP + 产生ID时的时间 + 自增序列 + 当前进程号
     * IP 8位 39.105.208.175 -> 2769d0af
     * 产生ID时的时间 13位：毫秒时间戳 -> 1403169275002
     * 自增序列 4位: 1000 - 9999 循环
     * 当前进程号 5位: PID
     * </p>
     *
     * @return ac13e001.1685348263825.095001000
     */
    public static String generate() {
        StringBuilder traceId = new StringBuilder();
        try {
            traceId.append(convertIP(IpUtil.getLocalIp4Address())).append(".");
            // 2. 时间戳 - 13位
            traceId.append(Instant.now().toEpochMilli()).append(".");
            // 3. 当前进程号 - 5位
            traceId.append(getProcessId());
            // 4. 自增序列 - 4位
            traceId.append(getAutoIncreaseNumber());
        } catch (SocketException e) {
            log.error("generate trace id error!", e);
            return UUID.randomUUID().toString().replaceAll("-", "");
        }
        return traceId.toString();
    }

    /**
     * IP转换为十六进制 - 8位
     *
     * @param ip    39.105.208.175
     * @return      2769d0af
     */
    private static String convertIP(String ip) {
        return Splitter.on(".").splitToStream(ip)
                // 将ip地址每段的十进制转为十六进制
                .map(s -> String.format("%02x", Integer.valueOf(s)))
                .collect(Collectors.joining());
    }

    /**
     * 使得自增序列在1000-999之间寻喜欢  -4位
     *
     * @return  自增序列号
     */
    private static int getAutoIncreaseNumber() {
        if (autoIncreaseNumber >= MAX_AUTO_NUMBER) {
            autoIncreaseNumber = MIN_AUTO_NUMBER;
            return autoIncreaseNumber;
        } else {
            return autoIncreaseNumber++;
        }
    }

    /**
     * @return 5位当前进程号
     */
    private static String getProcessId() {
        // 获取当前进程信息 ManagementFactory.getRuntimeMXBean()
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String processId = runtime.getName().split("@")[0];
        return String.format("%05d", Integer.parseInt(processId));
    }

    // 测试一下
    public static void main(String[] args) {
        String t = generate();
        System.out.println(t);
        String t2 = generate();
        System.out.println(t2);

        System.out.println(UUID.randomUUID());

        String trace = SkyWalkingTraceIdGenerator.generate();
        System.out.println(trace);
    }
}
