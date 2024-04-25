package com.qyd.service.sitemap.service.impl;

import com.qyd.api.model.enums.ArticleEventEnum;
import com.qyd.api.model.event.ArticleMsgEvent;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.core.cache.RedisClient;
import com.qyd.core.util.DateUtil;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.sitemap.constants.SitemapConstants;
import com.qyd.service.sitemap.model.SiteCntVo;
import com.qyd.service.sitemap.model.SiteMapVo;
import com.qyd.service.sitemap.model.SiteUrlVo;
import com.qyd.service.sitemap.service.SitemapService;
import com.qyd.service.statistics.service.CountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 邱运铎
 * @date 2024-04-25 14:35
 */
@Slf4j
@Service
public class SitemapServiceImpl implements SitemapService {
    // 原作者这里 @Value("${view.site.host:https://paicoding.com}")，这应该是更新host
    @Value("${view.site.host}")
    private String host;
    private static final int SCAN_SIZE = 100;

    private static final String SITE_MAP_CACHE_KEY = "sitemap";

    @Resource
    private ArticleDao articleDao;

    @Resource
    private CountService countService;

    /**
     * 查询站点地图
     *
     * @return
     */
    @Override
    public SiteMapVo getSiteMap() {
        // key 文章id， value = 最后更新时间
        Map<String, Long> siteMap = RedisClient.hGetAll(SITE_MAP_CACHE_KEY, Long.class);
        if (CollectionUtils.isEmpty(siteMap)) {
            // 首次访问时，没有数据。全量初始化
            initSiteMap();
        }
        siteMap = RedisClient.hGetAll(SITE_MAP_CACHE_KEY, Long.class);
        SiteMapVo vo = initBasicSite();
        if (CollectionUtils.isEmpty(siteMap)) {
            return vo;
        }

        for (Map.Entry<String, Long> entry : siteMap.entrySet()) {
            vo.addUrl(new SiteUrlVo(host + "/article/detail/" + entry.getKey(), DateUtil.time2utc(entry.getValue())));
        }
        return vo;
    }

    /**
     * fixme: 加锁初始化，更推荐的是采用分布式锁
     */
    private synchronized void initSiteMap() {
        long lastId = 0L;
        RedisClient.del(SITE_MAP_CACHE_KEY);
        while (true) {
            List<SimpleArticleDTO> list = articleDao.getBaseMapper().listArticlesOrderById(lastId, SCAN_SIZE);
            // 刷新文章的统计信息
            list.forEach(s -> countService.refreshArticleStatisticInfo(s.getId()));

            // 刷新站点地图信息
            Map<String, Long> map = list.stream()
                    // 这里最后转为map的 (a, b) -> a 是用于处理在转换为map时key重复冲突时的处理对策。
                    .collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s.getCreateTIme().getTime(), (a, b) -> a));
            RedisClient.hMSet(SITE_MAP_CACHE_KEY, map);
            if (list.size() < SCAN_SIZE) {
                break;
            }
            lastId = list.get(list.size() - 1).getId();
        }
    }

    private SiteMapVo initBasicSite() {
        SiteMapVo vo = new SiteMapVo();
        String time = DateUtil.time2utc(System.currentTimeMillis());
        vo.addUrl(new SiteUrlVo(host + "/", time));
        vo.addUrl(new SiteUrlVo(host + "/column", time));
        vo.addUrl(new SiteUrlVo(host + "/admin-view", time));
        return vo;
    }

    /**
     * 重新刷新站点地图
     */
    @Override
    public void refreshSiteMap() {
        initSiteMap();
    }

    /**
     * 基于文章的上下线，自动更新站点地图
     *
     * @param event
     */
    @EventListener(ArticleMsgEvent.class)
    public void autoUpdateSiteMap(ArticleMsgEvent<ArticleDO> event) {
        ArticleEventEnum type = event.getType();
        if (type == ArticleEventEnum.ONLINE) {
            addArticle(event.getContent().getId());
        } else if (type == ArticleEventEnum.OFFLINE || type == ArticleEventEnum.DELETE) {
            rmArticle(event.getContent().getId());
        }
    }

    /**
     * 新增文章并上线
     *
     * @param articleId
     */
    private void addArticle(Long articleId) {
        RedisClient.hSet(SITE_MAP_CACHE_KEY, String.valueOf(articleId), System.currentTimeMillis());
    }

    /**
     * 删除文章，Or文章下线
     *
     * @param articleId
     */
    private void rmArticle(Long articleId) {
        RedisClient.hDel(SITE_MAP_CACHE_KEY, String.valueOf(articleId));
    }

    /**
     * 采用定时器方案，每天5:15分刷新站点地图，确保数据的一致性
     */
    @Scheduled(cron = "0 15 5 * * ?")
    public void autoRefreshCache() {
        log.info("开始刷新sitemap.xml的url地址，避免出现数据不一致问题!");
        refreshSiteMap();
        log.info("刷新完成! ");
    }

    /**
     * 保存站点数据模型
     * <p>
     * 站点统计hash:
     * - visit_info:
     * ---- pv: 站点的总pv
     * ---- uv: 站点的总uv
     * ---- pv_path: 站点某个资源的总访问pv
     * ---- uv_path: 站点某个资源的总访问uv
     * - visit_info_ip
     * ---- pv: 用户访问的站点总次数
     * ---- path_pv: 用户访问的路径总次数
     * - visit_info_20240425每日记录，一天一条记录
     * ---- pv: 12  # field = 月日_pv, pv的计算
     * ---- uv: 5   # field = 月日_uv, uv的计数
     * ---- pv_path: 2 # 资源的当天访问计数
     * ---- uv_path: # 资源当天的访问uv
     * ---- pv_ip: # 用户当天的访问次数
     * ---- pv_path_ip: # 用户对资源的当天访问次数
     *
     * @param visitIp   访问者IP
     * @param path      访问的资源路径
     */
    @Override
    public void saveVisitInfo(String visitIp, String path) {
        String globalKey = SitemapConstants.SITE_VISIT_KEY;
        String day = SitemapConstants.day(LocalDate.now());

        String todayKey = globalKey + "_" + day;

        // 用户的全局访问计数+1
        Long globalUserVisitCnt = RedisClient.hIncr(globalKey + "_" + visitIp, "pv", 1);
        // 用户的当天访问计数+1
        Long todyaUserVisitCnt = RedisClient.hIncr(todayKey, "pv" + visitIp, 1);

        RedisClient.PipelineAction pipelineAction = RedisClient.pipelineAction();
        if (globalUserVisitCnt == 1) {
            // 站点新用户
            // 今日的uv + 1
            pipelineAction.add(todayKey, "uv"
                    , (connection, key, field) -> {
                connection.hIncrBy(key, field, 1);
            });
            pipelineAction.add(todayKey, "uv" + path
                    , (connection, key, field) -> {
                connection.hIncrBy(key, field, 1);
            });

            // 全局站点的uv
            pipelineAction.add(globalKey, "uv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
            pipelineAction.add(globalKey, "uv" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
        }

    }

    @Override
    public SiteCntVo querySiteVisitInfo(LocalDate date, String path) {
        return null;
    }
}
