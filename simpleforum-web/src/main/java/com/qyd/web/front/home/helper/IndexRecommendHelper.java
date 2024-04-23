package com.qyd.web.front.home.helper;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.ConfigTypeEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;
import com.qyd.api.model.vo.recommend.CarouseDTO;
import com.qyd.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.qyd.core.common.CommonConstants;
import com.qyd.core.async.AsyncUtil;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.CategoryService;
import com.qyd.service.config.service.ConfigService;
import com.qyd.service.sidebar.service.SidebarService;
import com.qyd.service.user.service.UserService;
import com.qyd.web.front.home.vo.IndexVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 首页推荐相关
 *
 * 通过这种构造器（还有一种在setter方法上）上进行注入，可以使得idea不报黄线警告
 * 这样也是Spring团队推荐的注入方式，像下面这种通过字段注入其实不被推荐，所以会被黄线警告
 * @Autowired
 * public IndexRecommendHelper(CategoryService categoryService) {
 *    this.categoryService = categoryService;
 *  }
 * @author 邱运铎
 * @date 2024-04-08 17:28
 */
@Component
public class IndexRecommendHelper {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleReadService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private SidebarService sidebarService;

    @Autowired
    private ConfigService configService;

    /**
     * 网站首页响应
     *
     * @param activeTab
     * @return
     */
    public IndexVo buildIndexVo(String activeTab) {
        IndexVo vo = new IndexVo();
        CategoryDTO category = categories(activeTab, vo);
        vo.setCategoryId(category.getCategoryId());
        vo.setCurrentCategory(category.getCategory());

        // 并行调度实例，提高响应性能
        AsyncUtil.concurrentExecutor("首页响应")
                .runAsyncWithTimeRecord(() -> vo.setArticles(articleList(category.getCategoryId())), "文章列表")
                .runAsyncWithTimeRecord(() -> vo.setTopArticles(topArticleList(category)), "置顶文章")
                .runAsyncWithTimeRecord(() -> vo.setHomeCarouseList(homeCarouseList()), "轮播图")
                .runAsyncWithTimeRecord(() -> vo.setSideBarItems(sidebarService.queryHomeSideBarList()), "侧边栏")
                .runAsyncWithTimeRecord(() -> vo.setUser(loginInfo()), "用户信息")
                .allExecuted()
                .prettyPrint();
        return vo;
    }

    /**
     * 根据关键词搜索相应文章
     * todo 源网站这个动作时失效的，可以根据看下什么问题
     *
     * @param key
     * @return
     */
    public IndexVo buildSearchVo(String key) {
        IndexVo vo = new IndexVo();
        vo.setArticles(articleService.queryArticlesBySearchKey(key, PageParam.newPageInstance()));
        vo.setSideBarItems(sidebarService.queryHomeSideBarList());
        return vo;
    }

    /**
     * 轮播图
     * todo 目前没看到有地方有用到，待发现
     *
     * @return
     */
    private List<CarouseDTO> homeCarouseList() {
        List<ConfigDTO> configList = configService.getConfigList(ConfigTypeEnum.HOME_PAGE);
        return configList.stream()
                .map(configDTO -> new CarouseDTO()
                        .setName(configDTO.getName())
                        .setImgUrl(configDTO.getBannerUrl())
                        .setActionUrl(configDTO.getJumpUrl()))
                .collect(Collectors.toList());
    }

    /**
     * 获取分类下的文章（正常展示的）
     *
     * @param categoryId
     * @return
     */
    private PageListVo<ArticleDTO> articleList(Long categoryId) {
        return articleService.queryArticlesByCategory(categoryId, PageParam.newPageInstance());
    }

    /**
     * 置顶top，文章列表
     *
     * @param category
     * @return
     */
    private List<ArticleDTO> topArticleList(CategoryDTO category) {
        List<ArticleDTO> topArticles = articleService.queryTopArticlesByCategory(category.getCategoryId() == 0 ? null : category.getCategoryId());
        if (topArticles.size() < PageParam.TOP_PAGE_SIZE) {
            // 当分类下文章数小于置顶数时，为了避免显示问题，直接不展示
            topArticles.clear();
            return topArticles;
        }

        // 查询分类对应的头图列表
        List<String> topPicList = CommonConstants.HOMEPAGE_TOP_PIC_MAP.getOrDefault(category.getCategory(),
                CommonConstants.HOMEPAGE_TOP_PIC_MAP.get(CommonConstants.CATEGORY_ALL));

        // 替换头图，下面做了一个数组越界的保护，避免当topPageSize数量变大，但是默认的cover图没有相应增大导致数据越界异常
        AtomicInteger index = new AtomicInteger(0);
        topArticles.forEach(s -> {
            s.setCover(topPicList.get(index.getAndIncrement() % topPicList.size()));
        });
        return topArticles;
    }

    /**
     * 返回分类列表
     *
     * @param active    选中的分类
     * @param vo        返回的结果
     * @return 返回选中的分类，当没有匹配时，返回默认的全部分类
     */
    private CategoryDTO categories(String active, IndexVo vo) {
        List<CategoryDTO> allCategories = categoryService.loadAllCategories();
        // 查询所有分类的对应的文章数量
        Map<Long, Long> categoryArticleCnt = articleService.queryArticleCountsByCategory();
        // 过滤到文章数为0的分类
        allCategories.removeIf(c -> categoryArticleCnt.getOrDefault(c.getCategoryId(), 0L) <= 0);

        // 刷新选中的分类
        AtomicReference<CategoryDTO> selectedCategory = new AtomicReference<>();
        allCategories.forEach(category -> {
            if (category.getCategory().equalsIgnoreCase(active)) {
                category.setSelected(true);
                selectedCategory.set(category);
            } else {
                category.setSelected(false);
            }
        });

        // 添加默认的全部分类
        allCategories.add(0, new CategoryDTO(0L, CategoryDTO.DEFAULT_TOTAL_CATEGORY));
        if (selectedCategory.get() == null) {
            selectedCategory.set(allCategories.get(0));
            allCategories.get(0).setSelected(true);
        }
        vo.setCategories(allCategories);
        return selectedCategory.get();
    }



    private UserStatisticInfoDTO loginInfo() {
        if (ReqInfoContext.getReqInfo() != null && ReqInfoContext.getReqInfo().getUserId() != null) {
            return userService.queryUserInfoWithStatistic(ReqInfoContext.getReqInfo().getUserId());
        }
        return null;
    }
}
