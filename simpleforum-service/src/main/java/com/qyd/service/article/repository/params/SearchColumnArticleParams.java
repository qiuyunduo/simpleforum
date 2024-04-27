package com.qyd.service.article.repository.params;

import com.qyd.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专栏查询入参
 *
 * @author 邱运铎
 * @date 2024-04-27 23:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchColumnArticleParams extends PageParam {

    /**
     * 专栏名称
     */
    private String column;

    /**
     * 专栏id
     */
    private Long columnId;

    /**
     * 文章标题
     */
    private String articleTitle;
}
