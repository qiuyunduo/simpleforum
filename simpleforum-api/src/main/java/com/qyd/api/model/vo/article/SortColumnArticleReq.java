package com.qyd.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-05-05 18:40
 */
@Data
@ApiModel("教程排序")
public class SortColumnArticleReq implements Serializable {

    @ApiModelProperty("排序前的文章 id")
    private Long activeId;

    @ApiModelProperty("排序后的文章 id")
    private Long overId;
}
