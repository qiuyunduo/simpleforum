package com.qyd.api.model.vo.article.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-05-05 17:36
 */
@Data
@Accessors(chain = true)
public class SimpleColumnDTO implements Serializable {
    private static final long serialVersionUID = -6447768958687130569L;

    @ApiModelProperty("专栏id")
    private Long columnId;

    @ApiModelProperty("专栏名")
    private String column;

    @ApiModelProperty("封面")
    private String cover;
}
