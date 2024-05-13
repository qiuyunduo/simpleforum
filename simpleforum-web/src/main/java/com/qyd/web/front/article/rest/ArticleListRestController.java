package com.qyd.web.front.article.rest;

import com.qyd.api.model.vo.NextPageHtmlVo;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.web.component.TemplateEngineHelper;
import com.qyd.web.global.BaseViewController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文章列表查询相关 api
 *
 * @author 邱运铎
 * @date 2024-05-08 13:59
 */
@RestController
@RequestMapping(path = "article/api/list")
public class ArticleListRestController extends BaseViewController {
    @Autowired
    private ArticleReadService articleService;

    @Autowired
    private TemplateEngineHelper templateEngineHelper;

    /**
     * 分类下的文章列表
     *
     * @return
     */
    @GetMapping(path = "category/{category}")
    public ResVo<NextPageHtmlVo> categoryList(@PathVariable("category") Long categoryId,
                                              @RequestParam(name = "page") Long page,
                                              @RequestParam(name = "size", required = false) Long size) {
        PageParam pageParam = buildPageParams(page, size);
        PageListVo<ArticleDTO> list = articleService.queryArticlesByCategory(categoryId, pageParam);
        String html = templateEngineHelper.rendToVO("views/article-category-list/article/list", "articles", list);
        return ResVo.ok(new NextPageHtmlVo(html, list.getHasMore()));
    }

    /**
     * 标签下的文章列表
     *
     * @param tagId
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = "tag/{tag}")
    public ResVo<NextPageHtmlVo> tagList(@PathVariable("tag") Long tagId,
                                         @RequestParam(name = "page") Long page,
                                         @RequestParam(name = "size", required = false) Long size) {
        PageParam pageParam = buildPageParams(page, size);
        PageListVo<ArticleDTO> list = articleService.queryArticlesByTag(tagId, PageParam.newPageInstance(page, size));
        String html = templateEngineHelper.rendToVO("views/article-tag-list/article/list", "articles", list);
        return ResVo.ok(new NextPageHtmlVo(html, list.getHasMore()));

    }
}
