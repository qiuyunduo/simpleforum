package com.qyd.api.model.vo.config;

import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-04-22 19:28
 */
@Data
public class SearchGlobalConfigReq {

    // 全局配置项键
    private String keywords;

    // 全局配置项值
    private String value;

    // 配置项备注说明
    private String comment;

    // 分页
    private Long pageNumber;
    private Long pageSize;
}
