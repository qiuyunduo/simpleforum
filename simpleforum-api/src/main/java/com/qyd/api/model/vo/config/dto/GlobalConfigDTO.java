package com.qyd.api.model.vo.config.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-04-22 19:37
 */
@Data
public class GlobalConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 全局配置项ID
    private Long id;

    // 全局配置项目键
    private String keywords;

    // 全局配置项值
    private String value;

    // 全局配置项备注信息
    private String comment;
}
