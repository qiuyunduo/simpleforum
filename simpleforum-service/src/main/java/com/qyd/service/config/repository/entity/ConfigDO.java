package com.qyd.service.config.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import com.qyd.api.model.enums.ConfigTagEnum;
import com.qyd.api.model.enums.ConfigTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源配置表
 *
 * @author 邱运铎
 * @date 2024-04-22 17:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config")
public class ConfigDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * 资源类型 电子书，公告，教程，首页banner
     * @see ConfigTypeEnum#getCode()
     *
     */
    private Integer type;

    /**
     * 资源名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 资源的封面图标图片链接
     */
    private String bannerUrl;

    /**
     * 资源跳转链接
     */
    private String jumpUrl;

    /**
     * 资源内容
     */
    private String content;

    /**
     * 排序
     */
    @TableField("`rank`")
    private Integer rank;

    /**
     * 状态： 0-未发布 1-已发布
     */
    private Integer status;

    /**
     * 0-正常 1-已删除
     */
    private Integer deleted;

    /**
     * 配置资源对应的标签，英文逗号分隔
     *
     * @see ConfigTagEnum#getCode()
     */
    private String tags;

    /**
     * 扩展信息字段，记录阅读人数，下载次数，评分等
     */
    private String extra;
}
