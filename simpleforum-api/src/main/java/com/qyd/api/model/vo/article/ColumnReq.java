package com.qyd.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * 保存Column 请求参数
 *
 * @author 邱运铎
 * @date 2024-04-28 0:08
 */
@Data
public class ColumnReq implements Serializable {
    private static final long serialVersionUID = -1905354351926426970L;

    /**
     * 专栏id
     */
    private Long columnId;

    /**
     * 专栏名
     */
    private String column;

    /**
     * 作者
     */
    private Long author;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 专栏封面
     */
    private String cover;

    /**
     * 状态： 未发布，连载，完结
     */
    private Integer state;

    /**
     * 专栏排序字段，值越小越靠前
     */
    private Integer section;

    /**
     * 专栏预计文章数
     */
    private Integer nums;

    /**
     * 专栏类型，免费，登录，收费
     */
    private Integer type;

    /**
     * 专栏限时免费开始时间
     */
    private Long freeStartTime;

    /**
     * 专栏限时免费结束时间
     */
    private Long freeEndTime;
}
