package com.qyd.web.front.search.vo;

import com.qyd.api.model.vo.article.dto.SimpleColumnDTO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-09 17:22
 */
@Data
@ApiModel(value = "专栏信息")
public class SearchColumnVo implements Serializable {
    private static final long serialVersionUID = -6836264764234537339L;

    @ApiModelProperty("搜索的关键词")
    private String key;

    @ApiModelProperty("专栏列表")
    private List<SimpleColumnDTO> items;
}
