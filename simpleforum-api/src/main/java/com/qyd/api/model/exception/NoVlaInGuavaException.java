package com.qyd.api.model.exception;

/**
 * 缓存未命中异常
 *
 * @author 邱运铎
 * @date 2024-05-03 13:54
 */
public class NoVlaInGuavaException extends RuntimeException {

    public NoVlaInGuavaException(String msg) {
        super(msg);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
