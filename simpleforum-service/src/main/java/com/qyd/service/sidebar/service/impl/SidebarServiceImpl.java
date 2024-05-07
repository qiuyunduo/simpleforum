package com.qyd.service.sidebar.service.impl;

import com.google.common.base.Splitter;
import com.qyd.api.model.enums.ConfigTypeEnum;
import com.qyd.api.model.enums.SidebarStyleEnum;
import com.qyd.api.model.enums.rank.ActivityRankTimeEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;
import com.qyd.api.model.vo.rank.dto.RankItemDTO;
import com.qyd.api.model.vo.recommend.RateVisitDTO;
import com.qyd.api.model.vo.recommend.SideBarDTO;
import com.qyd.api.model.vo.recommend.SideBarItemDTO;
import com.qyd.core.util.JsonUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.config.service.ConfigService;
import com.qyd.service.rank.service.UserActivityRankService;
import com.qyd.service.sidebar.service.SidebarService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 邱运铎
 * @date 2024-04-22 21:09
 */
@Service
public class SidebarServiceImpl implements SidebarService {

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private UserActivityRankService userActivityRankService;

    /**
     * 使用caffeine本地缓存，来处理侧边栏不怎么变动的信息
     * <p>
     * cacheNames -> 类似缓存前缀的概念
     * key -> SpEL 表达式，可以从传参中获取，来构建缓存的key
     * cacheManager -> 缓存管理器，如果全局只有一个时，可以省略
     *
     * @return
     */
    @Override
    @Cacheable(key = "'homeSidebar'", cacheManager = "caffeineCacheManager", cacheNames = "home")
    public List<SideBarDTO> queryHomeSideBarList() {
        List<SideBarDTO> list = new ArrayList<>();
        // 获取首页公告侧边栏信息
        list.add(noticeSideBar());
        // 获取首页教程侧边栏信息
        list.add(columnSideBar());
        // 获取首页热门文章
        list.add(hotArticles());
        // 获取首页用户月度活跃度排行榜
        SideBarDTO rankSideBar = rankList();
        if (rankSideBar != null) {
            list.add(rankSideBar);
        }
        return list;
    }

    /**
     * 公告信息
     *
     * @return
     */
    public SideBarDTO noticeSideBar() {
        List<ConfigDTO> noticeList = configService.getConfigList(ConfigTypeEnum.NOTICE);
        List<SideBarItemDTO> items = new ArrayList<>(noticeList.size());
        noticeList.forEach(configDTO -> {
            List<Integer> configTags;
            if (StringUtils.isBlank(configDTO.getTags())) {
                configTags = Collections.emptyList();
            } else {
                configTags = Splitter.on(",").splitToStream(configDTO.getTags())
                        .map(s -> Integer.parseInt(s.trim()))
                        .collect(Collectors.toList());
            }
            items.add(new SideBarItemDTO()
                    .setName(configDTO.getName())
                    .setTitle(configDTO.getContent())
                    .setUrl(configDTO.getJumpUrl())
                    .setTime(configDTO.getCreateTime().getTime())
                    .setTags(configTags)
            );
        });
        return new SideBarDTO()
                .setTitle("关于技术派")
                // TODO 知识星球的
                .setImg("https://cdn.tobebetterjavaer.com/paicoding/main/paicoding-zsxq.jpg")
                .setUrl("https://paicoding.com/article/detail/169")
                .setItems(items)
                .setStyle(SidebarStyleEnum.NOTICE.getStyle());
    }

    /**
     * 推荐教程的侧边栏
     *
     * @return
     */
    private SideBarDTO columnSideBar() {
        List<ConfigDTO> columnList = configService.getConfigList(ConfigTypeEnum.COLUMN);
        List<SideBarItemDTO> items = new ArrayList<>(columnList.size());
        columnList.forEach(configDTO -> {
            SideBarItemDTO item = new SideBarItemDTO();
            item.setName(configDTO.getName());
            item.setTitle(configDTO.getContent());
            item.setUrl(configDTO.getJumpUrl());
            item.setImg(configDTO.getBannerUrl());
            items.add(item);
        });
        return new SideBarDTO().setTitle("精选教程")
                .setItems(items)
                .setStyle(SidebarStyleEnum.COLUMN.getStyle());

    }

    /**
     * 热门文章侧边栏
     *
     * @return
     */
    private SideBarDTO hotArticles() {
        PageListVo<SimpleArticleDTO> vo = articleReadService.queryHotArticlesForRecommend(PageParam.newPageInstance(1, 8));
        List<SideBarItemDTO> items = vo.getList().stream()
                .map(s -> new SideBarItemDTO()
                        .setTitle(s.getTitle())
                        .setUrl("/article/detail/" + s.getId())
                        .setTime(s.getCreateTime().getTime()))
                .collect(Collectors.toList());
        return new SideBarDTO().setTitle("热门文章")
                .setItems(items)
                .setStyle(SidebarStyleEnum.ARTICLES.getStyle());
    }

    /**
     * 用户活跃度月度排行榜
     *
     * @return
     */
    private SideBarDTO rankList() {
        List<RankItemDTO> monthRankItems = userActivityRankService.queryRankList(ActivityRankTimeEnum.MONTH, 8);
        // service层做了为空返回Collections.emptyList(), 处理，所以这里直接使用isEmpty判断
        if (monthRankItems.isEmpty()) {
            return null;
        }
        SideBarDTO sidebar = new SideBarDTO().setTitle("月度活跃排行榜")
                .setStyle(SidebarStyleEnum.ACTIVE_RANK.getStyle());
        List<SideBarItemDTO> sidebarItemList = monthRankItems.stream()
                .map(s -> new SideBarItemDTO()
                        .setName(s.getUser().getName())
                        .setUrl(String.valueOf(s.getUser().getUserId()))
                        .setImg(s.getUser().getAvatar())
                        .setTime(s.getScore().longValue()))
                .collect(Collectors.toList());
        sidebar.setItems(sidebarItemList);
        return sidebar;
    }

    /**
     * 查询教程页面的侧边栏信息, 同样做缓存处理
     * 目前只存在一个订阅公众号侧边栏
     * 可以自己做扩展
     *
     * @return
     */
    @Override
    @Cacheable(key = "'columnSideBar'", cacheManager = "caffeineCacheManager", cacheNames = "column")
    public List<SideBarDTO> queryColumnSideBarList() {
        List<SideBarDTO> list = new ArrayList<>();
        list.add(subscribeSideBar());
        return list;
    }

    /**
     * 订阅公众号
     *
     * @return
     */
    private SideBarDTO subscribeSideBar() {
        return new SideBarDTO().setTitle("订阅")
                .setSubTitle("楼仔")
                .setImg("//cdn.tobebetterjavaer.com/paicoding/a768cfc54f59d4a056f79d1c959dcae9.jpg")
                .setContent("10本校招必刷八股文")
                .setStyle(SidebarStyleEnum.SUBSCRIBE.getStyle());
    }

    /**
     * 获取文章详情页面侧边栏
     * 同样进行缓存设置，但需要根据当前文章判断对应的缓存
     * 因为不同的文章缓存的侧边栏推荐文章是不一样的，
     * 主要是作者这里的业务设计是当前文章不能出现在推荐文章栏中）
     *  -------
     * 但其实这里我理解是： 当前文章出现在右侧推荐栏也没问题的
     * 这样就可以将缓存key设为该文章的作者，以作者的维度来进行缓存，更节省空间和其他资源
     * 并且对业务影响并不大。
     *
     * @param author        文章作者Id
     * @param articleId     文章id
     * @return
     */
    @Override
    @Cacheable(key = "'sideBar_'+#articleId", cacheManager = "caffeineCacheManager", cacheNames = "article")
    public List<SideBarDTO> queryArticleDetailSideBarList(Long author, Long articleId) {
        List<SideBarDTO> list = new ArrayList<>();
        //  这里需要注意，不能直接使用 pdfSideBar() 方式调用，这样或导致缓存不生效
        // 自己理解，因为这里缓存是通过注解实现的，而注解是通过aop思想实现的一种方式，所以会有代理
        // 在调用自身内部方法不会走到代理，所以导致aop没法拦截该调用，导致缓存失效
        list.add(SpringUtil.getBean(SidebarServiceImpl.class).pdfSideBar());
        list.add(recommendByAuthor(author, articleId, PageParam.DEFAULT_PAGE_SIZE));
        return list;
    }

    /**
     * PDF 优质资源
     * 这个是不怎么变动的，也进行缓存设置
     *
     * @return
     */
    @Cacheable(key = "'sideBar'", cacheManager = "caffeineCacheManager", cacheNames = "article")
    public SideBarDTO pdfSideBar() {
        List<ConfigDTO> pdfList = configService.getConfigList(ConfigTypeEnum.PDF);
        List<SideBarItemDTO> items = new ArrayList<>();
        pdfList.forEach(configDTO -> {
            SideBarItemDTO dto = new SideBarItemDTO();
            dto.setName(configDTO.getName());
            dto.setUrl(configDTO.getJumpUrl());
            dto.setImg(configDTO.getBannerUrl());
            RateVisitDTO visit;
            if (StringUtils.isNotBlank(configDTO.getExtra())) {
                visit = JsonUtil.toObj(configDTO.getExtra(), RateVisitDTO.class);
            } else {
                visit = new RateVisitDTO();
            }
            // 这里访问统计，似乎只需要页面加载，然后缓存不存在，所有的pdf资源访问数就+1
            // 这里应该是模仿pdf阅读业务，并非实际业务实现
            visit.incrVisit();
            // 更新阅读计数
            configService.updateVisited(configDTO.getId(), JsonUtil.toStr(visit));
            dto.setVisit(visit);
            items.add(dto);
        });
        return new SideBarDTO().setTitle("优质PDF")
                .setItems(items)
                .setStyle(SidebarStyleEnum.PDF.getStyle());
    }

    /**
     * 作者文章推荐列表
     * 排除当前正在阅读的文章
     *
     * @param authorId      文章作者
     * @param articleId     当前正在阅读的文章
     * @param size          推荐文章数量
     * @return
     */
    private SideBarDTO recommendByAuthor(Long authorId, Long articleId, long size) {
        List<SimpleArticleDTO> hotArticles = articleDao.listAuthorHotArticles(authorId, PageParam.newPageInstance(PageParam.DEFAULT_PAGE_NUM, size));
        List<SideBarItemDTO> items = hotArticles.stream()
                .filter(s -> !s.getId().equals(articleId))
                .map(s -> new SideBarItemDTO()
                        .setTitle(s.getTitle())
                        .setUrl("/article/detail" + s.getId())
                        .setTime(s.getCreateTime().getTime()))
                .collect(Collectors.toList());
        return new SideBarDTO().setTitle("相关文章")
                .setItems(items)
                .setStyle(SidebarStyleEnum.RECOMMEND.getStyle());
    }
}
