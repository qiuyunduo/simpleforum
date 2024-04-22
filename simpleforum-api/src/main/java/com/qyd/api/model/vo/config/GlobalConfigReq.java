package com.qyd.api.model.vo.config;

import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-04-22 19:42
 */
@Data
public class GlobalConfigReq {
    // 全局配置项名称
    private String keywords;
    // 全局配置项值
    private String value;
    // 全局配置项备注说明
    private String comment;
    // 全局配置项id
    private Long id;
}
