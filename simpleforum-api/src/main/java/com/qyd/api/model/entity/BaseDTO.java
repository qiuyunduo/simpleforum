package com.qyd.api.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
 * @author 邱运铎
 * @date 2024-04-11 0:56
 */
@Data
public class BaseDTO {
    @ApiModelProperty(value = "业务主键")
    private Long id;

    @ApiModelProperty(value = "创建时间")
    private Date crateTime;

    @ApiModelProperty(value = "最后编辑时间")
    private Date updateTime;
}
