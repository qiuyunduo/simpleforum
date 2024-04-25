package com.qyd.api.model.vo.article.dto;

import com.qyd.api.model.enums.column.ColumnTypeEnum;
import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-04-25 11:11
 */
@Data
public class ArticleOtherDTO {

    /** 文章的阅读类型，
     * todo 具体类型是针对，文章分类例如：教程，博文。还是阅读的方式分类例如：浏览器，手机等
     * 已解决: 如下
     * @see ColumnTypeEnum#getType()
    */
    private Integer readType;

    // 教程翻页， 网站实际暂时没实现，所以也没用到
    private ColumnArticleFlipDTO flip;
}
