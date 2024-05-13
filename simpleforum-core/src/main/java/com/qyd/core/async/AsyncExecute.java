package com.qyd.core.async;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 异步执行
 *
 * @author 邱运铎
 * @date 2024-05-13 21:05
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncExecute {

    /**
     * 是否开启异步执行
     *
     * @return
     */
    boolean value() default true;

    /**
     * 超时时间， 默认3秒
     *
     * @return
     */
    int timeOut() default 3;

    /**
     * 超时时间单位，默认秒，配合上线的TimeOut使用
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 当出现超时返回的兜底策略， 支持SpEL
     * 如果返回的是空字符串，则表示出现异常
     *
     * @return
     */
    String timeOutRsp() default "";
}
