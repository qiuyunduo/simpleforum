package com.qyd.service.article.repository.params;

import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-05 19:26
 */
@Data
public class ColumnArticleParams {

    // 教程 ID
    private Long columnId;

    // 文章 ID
    private Long articleId;

    // section 顺序，也可以认为是章节数
    private Integer section;
}
