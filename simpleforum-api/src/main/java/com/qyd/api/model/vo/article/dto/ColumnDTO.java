package com.qyd.api.model.vo.article.dto;

import com.qyd.api.model.enums.column.ColumnStatusEnum;
import com.qyd.api.model.enums.column.ColumnTypeEnum;
import com.qyd.api.model.vo.user.dto.ColumnFootCountDTO;
import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-04-25 12:41
 */
@Data
public class ColumnDTO {

    /**
     * 专栏id
     */
    private Long columnId;

    /**
     * 专栏名
     */
    private String column;

    /**
     * 专栏介绍
     */
    private String introduction;

    /**
     * 专栏封面
     */
    private String cover;

    /**
     * 发布时间
     */
    private Long publishTime;

    /**
     * 专栏排序
     */
    private Integer section;

    /**
     * 专栏状态
     * 0-未发布，1-连载中，3-已完结
     *
     * @see ColumnStatusEnum#getCode()
     */
    private Integer state;

    /**
     * 专栏预计的文章数
     */
    private Integer nums;

    /**
     * 专栏阅读类型
     *
     * @see ColumnTypeEnum#getType()
     */
    private Integer type;

    /**
     * 专栏限免开始时间
     */
    private Long freeStartTime;

    /**
     * 专栏限免结束时间
     */
    private Long freeEndTIme;

    /**
     * 专栏作者id
     */
    private Long author;

    /**
     * 专栏作者
     */
    private String authorName;

    /**
     * 专栏作者头像
     */
    private String authorAvatar;

    /**
     * 专栏的作者个人简介
     */
    private String authorProfile;

    /**
     * 专栏的统计计数相关信息
     */
    private ColumnFootCountDTO count;
}
