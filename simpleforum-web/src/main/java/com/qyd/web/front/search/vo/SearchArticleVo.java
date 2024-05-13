package com.qyd.web.front.search.vo;

import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-05-09 17:21
 */
@Data
@ApiModel(value = "文章信息")
public class SearchArticleVo implements Serializable {
    private static final long serialVersionUID = 8561240565087040217L;

    @ApiModelProperty(value = "搜索的关键词")
    private String key;

    @ApiModelProperty(value = "文章列表")
    private List<SimpleArticleDTO> items;
}
