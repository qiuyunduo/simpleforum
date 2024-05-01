package com.qyd.core.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 解决跨域的三种办法
 * 1： @Configuration implements  WebMvcConfigurer
 *       And @Override -> addCorsMappings
 * 2: Filter + 改变请求头如下
 *
 * 3： controller 层 + 注解
 *    例如: @CrossOrigin(origins = "http://localhost:4000")
 *    该方法应该是粒度最小的
 *
 * @author 邱运铎
 * @date 2024-04-29 13:28
 */
public class CrossUtil {

    /**
     * 支持跨域
     *
     * @param request
     * @param response
     */
    public static void buildCors(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (StringUtils.isBlank(origin)) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            // Credentials 是否允许携带 cookie, 如果源头请求不是本网站发出，没必要携带cookie
            response.setHeader("Access-Control-Allow-Credentials", "false");
        } else {
            response.setHeader("Access-Control-Allow-Origin", origin);
            // 请求是从本网站发出，需要携带cookie,
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        // 配置跨域必填
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        // 配置跨域必填
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAd");
        // 设置Max-Age 在指定时间内不在进行请求预检， 因为经常发现开发中请求都是两条，一条OPTIONS， 一条正常请求，
        // 设置该字段可以避免每次都发出预检请求
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, X-Real-IP, X-Forwarded-For, d-uuid, User-Agent, x-zd-cs, Proxy-Client-IP, HTTP_CLIENT_IP, HTTP_X_FORWARDED_FOR");
    }
}
