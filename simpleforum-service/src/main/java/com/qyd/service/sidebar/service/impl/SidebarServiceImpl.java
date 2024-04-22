package com.qyd.service.sidebar.service.impl;

import com.google.common.base.Splitter;
import com.qyd.api.model.enums.ConfigTypeEnum;
import com.qyd.api.model.enums.SidebarStyleEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;
import com.qyd.api.model.vo.recommend.SideBarDTO;
import com.qyd.api.model.vo.recommend.SideBarItemDTO;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.config.service.ConfigService;
import com.qyd.service.sidebar.service.SidebarService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        list.add(noticeSideBar());
        list.add(columnSideBar());
        list.add(hotArticles());

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
                        .setUrl("/article/detial/" + s.getId())
                        .setTime(s.getCreateTIme().getTime()))
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
        return null;
    }

    @Override
    public List<SideBarDTO> queryColumnSideBarList() {
        return null;
    }

    @Override
    public List<SideBarDTO> queryArticleDetailSideBarList(Long author, Long articleId) {
        return null;
    }
}
