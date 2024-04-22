package com.qyd.service.sidebar.service;

import com.qyd.api.model.vo.recommend.SideBarDTO;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-22 20:42
 */
public interface SidebarService {

    /**
     * 插叙首页的侧边栏信息
     *
     * @return
     */
    List<SideBarDTO> queryHomeSideBarList();

    /**
     * 查询教程侧边栏信息
     *
     * @return
     */
    List<SideBarDTO> queryColumnSideBarList();

    /**
     * 查询文章详情页面侧边栏信息
     *
     * @param author        文章作者Id
     * @param articleId     文章id
     * @return
     */
    List<SideBarDTO> queryArticleDetailSideBarList(Long author, Long articleId);
}
