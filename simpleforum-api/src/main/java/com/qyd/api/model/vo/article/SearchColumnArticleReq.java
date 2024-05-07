package com.qyd.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-05 18:37
 */
@Data
@ApiModel("教程配套的文章查询")
public class SearchColumnArticleReq {

    @ApiModelProperty("教程名称")
    private String column;

    @ApiModelProperty("教程id")
    private Long columnId;

    @ApiModelProperty("文章标题")
    private String articleTitle;

    @ApiModelProperty("请求页数，从1开始计数")
    private long pageNumber;

    @ApiModelProperty("请求页大小，默认为 10")
    private long pageSize;
}
