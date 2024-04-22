package com.qyd.api.model.vo.banner.dto;

import com.qyd.api.model.entity.BaseDTO;
import com.qyd.api.model.enums.ConfigTagEnum;
import lombok.Data;

/**
 * Banner
 *
 * @author 邱运铎
 * @date 2024-04-22 16:38
 */
@Data
public class ConfigDTO extends BaseDTO {

    /**
     * 类型
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 图片链接
     */
    private String bannerUrl;

    /**
     * 跳转链接
     */
    private String jumpUrl;

    /**
     * 内容
     */
    private String content;

    /**
     * 排序
     */
    private Integer rank;

    /**
     * 状态： 0-未发布， 1-已发布
     */
    private Integer status;

    /**
     * json格式的扩展信息
     */
    private String extra;

    /**
     * 配置相关的标签： 如火热，推荐，精选，等,使用英文逗号分隔
     *
     * @see ConfigTagEnum#getCode()
     */
    private String tags;
}
