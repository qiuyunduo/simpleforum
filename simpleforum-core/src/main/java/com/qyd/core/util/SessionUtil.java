package com.qyd.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-26 17:59
 */
public class SessionUtil {
    // cookie设置过期时间 30天
    private static final int COOKIE_AGE = 30 * 86400;

    public static Cookie newCookie(String key, String session) {
        // path = "/" 将cookie设置在tomcat的根目录下，在该目录下的所有应用都可以获得该cookie
        // cookie 默认是存储在当前访问路径下
        return newCookie(key, session, "/", COOKIE_AGE);
    }

    public static Cookie newCookie(String key, String session, String path, int maxAge) {
        Cookie cookie = new Cookie(key, session);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public static Cookie delCookie(String key) {
        return delCookie(key, "/");
    }

    public static Cookie delCookie(String key, String path) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        return cookie;
    }

    /**
     * 根据 key 查询 cookie
     *
     * @param request
     * @param name
     * @return
     */
    public static Cookie findCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> StringUtils.equalsAnyIgnoreCase(cookie.getName(), name))
                .findFirst()
                .orElse(null);
    }

    public static String findCookieByName(ServerHttpRequest request, String name) {
        List<String> list = request.getHeaders().get("cookie");
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (String item : list) {
            String[] elements = StringUtils.split(item, ";");
            for (String element : elements) {
                String[] objs = StringUtils.split(element, "=");
                if (objs.length == 2 && StringUtils.equalsAnyIgnoreCase(objs[0].trim(), name)) {
                    return objs[1].trim();
                }
            }
        }
        return null;
    }
}
