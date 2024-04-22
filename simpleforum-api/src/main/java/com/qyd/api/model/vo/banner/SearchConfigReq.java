package com.qyd.api.model.vo.banner;

import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-04-22 19:17
 */
@Data
public class SearchConfigReq {

    /**
     * 配置资源类型
     */
    private Integer type;

    /**
     * 配置资源名称
     */
    private String name;

    /**
     * 分页
     */
    private Long pageNumber;
    private Long pageSize;
}
