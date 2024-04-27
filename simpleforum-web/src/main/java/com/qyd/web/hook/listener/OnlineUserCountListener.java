package com.qyd.web.hook.listener;

import com.qyd.core.util.SpringUtil;
import com.qyd.service.statistics.service.UserStatisticService;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 通过监听session来实现实时人数统计
 *
 * @author 邱运铎
 * @date 2024-04-27 20:00
 */
@WebListener
public class OnlineUserCountListener implements HttpSessionListener {

    /**
     * 新增session, 在线人数统计数 +1
     *
     * @param se
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSessionListener.super.sessionCreated(se);
        SpringUtil.getBean(UserStatisticService.class).incrOnlineUserCnt(1);
    }

    /**
     * session失效，在线人数统计数 -1
     *
     * @param se
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSessionListener.super.sessionDestroyed(se);
        SpringUtil.getBean(UserStatisticService.class).incrOnlineUserCnt(-1);
    }
}
