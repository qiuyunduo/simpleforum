package com.qyd.core.mdc;

import org.slf4j.MDC;

/**
 * MDC Slf4j 提供的一个用于存放诊断日志容器
 * 通过在日志配置文件中的 使用存入到该容器的字段，自定义形成需要的日志打印头
 * 用于对请求通过日志实现追踪
 *
 * @author 邱运铎
 * @date 2024-04-29 12:38
 */
public class MdcUtil {

    public static final String TRACE_ID_KEY = "traceId";

    public static void add(String key, String val) {
        MDC.put(key, val);
    }

    public static void addTraceId() {
        // traceId 的生成规则， 目前提供了两种生成策略, 可以使用自定义的也可以使用SkyWalking; 实际项目中选择一种即可
        MDC.put(TRACE_ID_KEY, SelfTraceIdGenerator.generate());
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static void reset() {
        String traceId = MDC.get(TRACE_ID_KEY);
        MDC.clear();
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void clear() {
        MDC.clear();
    }
}
