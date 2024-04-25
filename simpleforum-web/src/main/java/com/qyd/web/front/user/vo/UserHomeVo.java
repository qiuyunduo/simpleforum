package com.qyd.web.front.user.vo;

import com.qyd.api.model.enums.FollowSelectEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.TagSelectDTO;
import com.qyd.api.model.vo.user.dto.FollowUserInfoDTO;
import com.qyd.api.model.vo.user.dto.UserStatisticInfoDTO;
import lombok.Data;

import java.util.List;

/**
 * 个人主页信息vo
 *
 * @author 邱运铎
 * @date 2024-04-25 13:20
 */
@Data
public class UserHomeVo {
    /**
     * 对应个人主页的，文章、浏览记录、关注、收藏
     */
    private String homeSelectType;
    /**
     * todo 代表什么不清楚
     */
    private List<TagSelectDTO> homeSelectTags;

    /**
     * 关注列表/粉丝列表
     */
    private PageListVo<FollowUserInfoDTO> followList;

    /**
     * 在homeSelectType为关注时，选中的是关注列表还是粉丝列表
     *
     * @see FollowSelectEnum#getCode()
     */
    private String followSelectType;

    /**
     * todo 代表什么不理解
     */
    private List<TagSelectDTO> followSelectTags;

    private UserStatisticInfoDTO userHome;

    /**
     * 文章列表
     */
    private PageListVo<ArticleDTO> homeSelectList;
}
