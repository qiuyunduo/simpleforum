package com.qyd.web.front.home.vo;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.api.model.vo.recommend.CarouseDTO;
import com.qyd.api.model.vo.recommend.SideBarDTO;
import com.qyd.api.model.vo.user.dto.UserStatisticInfoDTO;
import lombok.Data;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-08 17:28
 */
@Data
public class IndexVo {

    /**
     * 分类列表
     */
    private List<CategoryDTO> categories;

    /**
     * 当前选中的分类
     */
    private String currentCategory;

    /**
     * 当前选中的类目ID
     */
    private Long categoryId;

    /**
     * top 文章列表
     */
    private List<ArticleDTO> topArticles;

    /**
     * 文章列表
     */
    private PageListVo<ArticleDTO> articles;

    /**
     * 登录用户信息
     */
    private UserStatisticInfoDTO user;

    /**
     * 侧边栏信息
     */
    private List<SideBarDTO> sideBarItems;

    /**
     * 轮播图
     */
    private List<CarouseDTO> homeCarouseList;

}
