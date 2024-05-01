package com.qyd.core.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 邱运铎
 * @date 2024-04-29 16:08
 */
@Slf4j
public class AlarmUtil extends AppenderBase<ILoggingEvent> {
    private static final long INTERVAL = 10 * 1000 * 60;
    private long lastAlarmTime = 0;

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (canAlarm()) {
            EmailUtil.sendMail(iLoggingEvent.getLoggerName(),
                    SpringUtil.getConfig("alarm.user", "2582840488@qq.com"),
                    iLoggingEvent.getFormattedMessage());
            log.warn("sendEmail to Alarm user！！！！！！！！！！！！！！！！！！！！");
        }
    }

    private boolean canAlarm() {
        // 做一个简单的频率过滤，十分钟只允许发送一条报警
        long now = System.currentTimeMillis();
        if (now - lastAlarmTime >= INTERVAL) {
            lastAlarmTime = now;
            return true;
        } else {
            return false;
        }
    }
}
