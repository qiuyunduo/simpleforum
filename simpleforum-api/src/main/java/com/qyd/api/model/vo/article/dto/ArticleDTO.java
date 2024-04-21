package com.qyd.api.model.vo.article.dto;

import com.qyd.api.model.vo.user.dto.ArticleFootCountDTO;
import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import lombok.Data;
import com.qyd.api.model.enums.SourceTypeEnum;
import java.io.Serializable;
import java.util.List;

/**
 * 文章信息
 *
 * DTO 定义返回给web前端的实体类（VO）
 *
 * @author 邱运铎
 * @date 2024-04-10 21:40
 */
@Data
public class ArticleDTO implements Serializable {
    private static final long serialVersionUID = -793906904770296838L;

    private Long articleId;

    /**
     * 文章类型：1-博文，2-问答
     */
    private Integer articleType;

    /**
     * 作者uid
     */
    private Long author;

    /**
     * 作者名
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String authorAvatar;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 短标题
     */
    private String shortTitle;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章封面
     * todo 此字段和 picture 头图字段区别在哪还是就是头图字段，待确定
     */
    private String cover;

    /**
     * 正文
     */
    private String content;

    /**
     * 文章来源
     *  todo 此字段和 ArticleDO 中的 Integer 类型的 source 一样，但这里使用 String 类型
     * @see SourceTypeEnum
     */
    private String sourceType;

    /**
     * 原文地址
     */
    private String sourceUrl;

    /**
     * 文章状态： 0-未发布 1-已发布
     */
    private Integer status;

    /**
     * 是否官方
     */
    private Integer officialStat;

    /**
     * 是否置顶
     */
    private Integer toppingStat;

    /**
     * 是否加精
     */
    private Integer creamStat;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 最后更新时间
     */
    private Long lastUpdateTime;

    /**
     * 分类
     */
    private CategoryDTO category;

    /**
     * 标签
     */
    private List<TagDTO> tags;

    /**
     * 表示当前查看的用户是否已经点赞过
     */
    private Boolean praised;

    /**
     * 表示当前用户是否评论过
     */
    private Boolean commented;

    /**
     * 表示当前用户是否已收藏
     */
    private Boolean collected;

    /**
     * 文章对应的统计计数
     */
    private ArticleFootCountDTO count;

    /**
     * 点赞用户信息
     */
    private List<SimpleUserInfoDTO> praisedUsers;
}
