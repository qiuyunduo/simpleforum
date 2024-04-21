package com.qyd.api.model.exception;

import com.qyd.api.model.vo.Status;
import com.qyd.api.model.vo.constants.StatusEnum;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author 邱运铎
 * @date 2024-04-21 20:13
 */
public class ForumException extends RuntimeException{
    @Getter
    private Status status;

    public ForumException(Status status) {
        this.status = status;
    }

    public ForumException(int code, String msg) {
        this.status = Status.newStatus(code, msg);
    }

    public ForumException(StatusEnum statusEnum, Object...args) {
        this.status = Status.newStatus(statusEnum, args);
    }
}
