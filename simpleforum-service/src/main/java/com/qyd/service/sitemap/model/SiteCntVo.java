package com.qyd.service.sitemap.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 站点计数
 *
 * @author 邱运铎
 * @date 2024-04-25 14:29
 */
@Data
public class SiteCntVo implements Serializable {
    private static final long serialVersionUID = 363038661423310634L;

    /**
     * 日期
     */
    private String day;

    /**
     * 路径，全站时。path为null
     */
    private String path;

    /**
     * 站点 page view 点击数
     */
    private Integer pv;

    /**
     * 站点unique view 点击数
     */
    private Integer uv;
}
