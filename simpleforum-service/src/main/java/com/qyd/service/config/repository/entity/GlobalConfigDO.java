package com.qyd.service.config.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 全局配置表
 *
 * @author 邱运铎
 * @date 2024-04-22 19:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("global_conf")
public class GlobalConfigDO extends BaseDO {
    private static final long serialVersionUID = -6122208316544171301L;

    /**
     * 配置项键
     */
    @TableField("`key`")
    private String key;

    /**
     * 配置项值
     */
    private String value;

    /**
     * 配置备注信息
     */
    private String comment;

    /**
     * 删除标记 0-未删除 1-已删除
     */
    private Integer deleted;
}
