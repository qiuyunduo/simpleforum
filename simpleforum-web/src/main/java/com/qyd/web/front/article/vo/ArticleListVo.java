package com.qyd.web.front.article.vo;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-04 19:41
 */
@Data
public class ArticleListVo {

    /**
     * 归档类型
     * todo 归档的含义外延不理解
     * 目前从后面业务代码来看 似乎和文章分类是对应的， 这里对应分类名 CategoryName
     * 还可以是标签名 tagName
     * 那这就比较清晰了这里的归档就是指按照什么未读来对所有文章进行划分，
     * 目前是支持按照类别和标签划分。
     */
    private String archives;

    /**
     * 归档id
     * 这里对应分类id CategoryId
     * 还可以是 标签id tagId
     */
    private Long archiveId;

    private PageListVo<ArticleDTO> articles;
}
