package com.qyd.api.model.exception;

import com.qyd.api.model.vo.Status;
import com.qyd.api.model.vo.constants.StatusEnum;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author 邱运铎
 * @date 2024-04-27 16:37
 */
public class ForumAdviceException extends RuntimeException {

    @Getter
    private Status status;

    public ForumAdviceException(Status status) {
        this.status = status;
    }

    public ForumAdviceException(int code, String msg) {
        this.status = Status.newStatus(code, msg);
    }

    public ForumAdviceException(StatusEnum statusEnum, Object...args) {
        this.status = Status.newStatus(statusEnum, args);
    }
}
