package com.qyd.service.sitemap.constants;

import com.qyd.core.util.DateUtil;

import java.time.LocalDate;

/**
 * 站点相关地图
 *
 * @author 邱运铎
 * @date 2024-04-25 18:17
 */
public class SitemapConstants {

    public static final String SITE_VISIT_KEY = "visit_info";

    public static String day(LocalDate day) {
        return DateUtil.SIMPLE_DATE_FORMAT.format(day);
    }
}
