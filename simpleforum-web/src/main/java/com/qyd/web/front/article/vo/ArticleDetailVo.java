package com.qyd.web.front.article.vo;

import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.ArticleOtherDTO;
import com.qyd.api.model.vo.comment.dto.TopCommentDTO;
import com.qyd.api.model.vo.recommend.SideBarDTO;
import com.qyd.api.model.vo.user.dto.UserStatisticInfoDTO;
import lombok.Data;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-25 11:09
 */
@Data
public class ArticleDetailVo {
    /**
     * 文章信息
     */
    private ArticleDTO article;

    /**
     * 评论信息
     */
    private List<TopCommentDTO> comments;

    /**
     * 热门评论
     */
    private TopCommentDTO hotComment;

    /**
     * 作者相关的信息
     */
    private UserStatisticInfoDTO author;

    // 其他的信息，比如说翻页，阅读类型 -- 目前没有看到应用场景
    private ArticleOtherDTO other;

    /**
     * 侧边栏信息
     */
    private List<SideBarDTO> sideBarItems;
}
