package com.qyd.api.model.exception;

import com.qyd.api.model.vo.constants.StatusEnum;

/**
 * @author 邱运铎
 * @date 2024-04-21 20:12
 */
public class ExceptionUtil {
    public static ForumException of(StatusEnum status, Object...args) {
        return new ForumException(status, args);
    }
}
