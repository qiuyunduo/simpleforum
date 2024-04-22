package com.qyd.api.model.vo.article.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import com.qyd.api.model.enums.column.ColumnArticleReadEnum;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 文章推荐
 *
 * @author 邱运铎
 * @date 2024-04-10 22:27
 */
@Data
@Accessors(chain = true)
public class SimpleArticleDTO implements Serializable {
    private static final long serialVersionUID = 3646376715620165839L;

    @ApiModelProperty("文章ID")
    private Long id;

    @ApiModelProperty("文章标题")
    private String title;

    @ApiModelProperty("专栏ID")
    private String columnId;

    @ApiModelProperty("专栏标题")
    private String colum;

    @ApiModelProperty("文章排序")
    private Integer sort;

    @ApiModelProperty("创建时间")
    private Timestamp createTIme;

    /**
     * @see ColumnArticleReadEnum#getRead()
     */
    @ApiModelProperty("阅读模式")
    private Integer readType;
}
