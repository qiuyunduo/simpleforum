package com.qyd.web.front.article.view;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.CategoryService;
import com.qyd.service.article.service.TagService;
import com.qyd.web.front.article.rest.ArticleRestController;
import com.qyd.web.front.article.vo.ArticleListVo;
import com.qyd.web.global.BaseViewController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文章列表视图
 *
 * @author 邱运铎
 * @date 2024-05-07 20:28
 */
@Controller
@RequestMapping(path = "article")
public class ArticleListViewController extends BaseViewController {
    private ArticleReadService articleService;
    private CategoryService categoryService;
    private TagService tagService;

    public ArticleListViewController(ArticleReadService articleService,
                                 CategoryService categoryService,
                                 TagService tagService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    /**
     * 查询某个分类下的文章列表
     *
     * @param category
     * @param model
     * @return
     */
    @GetMapping(path = "category/{category}")
    public String categoryList(@PathVariable("category") String category, Model model) {
        Long categoryId = categoryService.queryCategoryId(category);
        PageListVo<ArticleDTO> list = categoryId != null ? articleService.queryArticlesByCategory(categoryId, PageParam.newPageInstance()) : PageListVo.emptyVo();
        ArticleListVo vo = new ArticleListVo();
        vo.setArchives(category);
        vo.setArchiveId(categoryId);
        vo.setArticles(list);
        model.addAttribute("vo", vo);
        return "views/article-category-list/index";
    }

    /**
     * 查询某个标签下的文章列表
     *
     * @param tag
     * @param model
     * @return
     */
    @GetMapping(path = "tag/{tag}")
    public String tagList(@PathVariable("tag") String tag, Model model) {
        Long tagId = tagService.queryTagsId(tag);
        PageListVo<ArticleDTO> list = tagId != null ? articleService.queryArticlesByTag(tagId, PageParam.newPageInstance()) : PageListVo.emptyVo();
        ArticleListVo vo = new ArticleListVo();
        vo.setArchives(tag);
        vo.setArchiveId(tagId);
        vo.setArticles(list);
        model.addAttribute("vo", vo);
        return "views/article-tag-list/index";
    }
}
