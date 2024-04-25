package com.qyd.service.sitemap.service;

import com.qyd.service.sitemap.model.SiteCntVo;
import com.qyd.service.sitemap.model.SiteMapVo;

import java.time.LocalDate;

/**
 * 站点统计相关服务
 * - 站点地图
 * - pv/uv
 *
 * @author 邱运铎
 * @date 2024-04-25 14:08
 */
public interface SitemapService {

    /**
     * 查询站点地图
     *
     * @return
     */
    SiteMapVo getSiteMap();

    /**
     * 刷新站点地图
     */
    void refreshSiteMap();

    /**
     * 保存用户访问信息
     *
     * @param visitIp   访问者IP
     * @param path      访问的资源路径
     */
    void saveVisitInfo(String visitIp, String path);

    /**
     * 查询站点某一天Or总的访问信息
     *
     * @param date  日期， 为空表示查询所有的站点信息
     * @param path  访问路径，为空时表示查站点信息
     * @return
     */
    SiteCntVo querySiteVisitInfo(LocalDate date, String path);
}
