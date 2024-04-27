package com.qyd.api.model.vo;

import com.qyd.api.model.vo.constants.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * response响应结果包装
 *
 * @author 邱运铎
 * @date 2024-04-27 13:35
 */
@Data
public class ResVo<T> implements Serializable {
    private static final long serialVersionUID = 7094254175598050454L;

    @ApiModelProperty(value = "返回结果说明", required = true)
    private Status status;

    @ApiModelProperty(value = "返回的实体结果", required = true)
    private T result;

    public ResVo() {
    }

    public ResVo(Status status) {
        this.status = status;
    }

    public ResVo(T t) {
        status = Status.newStatus(StatusEnum.SUCCESS);
        this.result = t;
    }

    public static <T> ResVo<T> ok(T t) {
        return new ResVo<T>(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> ResVo<T> fail(StatusEnum status, Object...args) {
        return new ResVo<>(Status.newStatus(status, args));
    }

    public static <T> ResVo<T> fail(Status status) {
        return new ResVo<>(status);
    }
}
