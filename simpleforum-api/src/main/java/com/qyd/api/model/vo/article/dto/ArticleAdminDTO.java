package com.qyd.api.model.vo.article.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章信息
 * DTO 定义返回给 admin 后端的实体类（VO）
 *
 * @author 邱运铎
 * @date 2024-04-10 23:21
 */
@Data
public class ArticleAdminDTO implements Serializable {
    private static final long serialVersionUID = -793906904770296838L;

    private Long articleId;

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
     * 封面
     */
    private String cover;

    /**
     * 文章状态： 0-未发布，1-已发布
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
     * 更新时间
     */
    private Date updateTime;
}
