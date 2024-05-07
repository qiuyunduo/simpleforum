package com.qyd.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-05 17:40
 */
@Data
@ApiModel("教程查询")
public class SearchColumnReq {

    @ApiModelProperty("教程名称")
    private String column;

    @ApiModelProperty("请求页数，从1开始计数")
    private long pageNumber;

    @ApiModelProperty("请求页大小，默认 10 ")
    private long pageSize;
}
