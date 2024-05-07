package com.qyd.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-05-05 18:43
 */
@Data
@ApiModel("教程排序，根据 id 和 新填的排序")
public class SortColumnArticleByIDReq implements Serializable {

    @ApiModelProperty("要排序的id")
    private Long id;

    @ApiModelProperty("新的排序")
    private Integer sort;
}
