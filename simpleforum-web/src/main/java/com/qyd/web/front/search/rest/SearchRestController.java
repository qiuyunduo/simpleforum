package com.qyd.web.front.search.rest;

import com.qyd.api.model.vo.NextPageHtmlVo;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.web.component.TemplateEngineHelper;
import com.qyd.web.front.search.vo.SearchArticleVo;
import com.qyd.web.global.BaseViewController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 搜索服务接口
 *
 * @author 邱运铎
 * @date 2024-05-09 17:40
 */
@RestController
@RequestMapping(path = "search/api")
public class SearchRestController extends BaseViewController {
    @Autowired
    private ArticleReadService articleReadService;

    @Resource
    private TemplateEngineHelper templateEngineHelper;

    /**
     * 根据关键词给出搜索下拉框
     *
     * @param key
     * @return
     */
    @GetMapping(path = "hint")
    public ResVo<SearchArticleVo> recommend(@RequestParam(value = "key", required = false) String key) {
        List<SimpleArticleDTO> list = articleReadService.querySimpleArticleBySearchKey(key);
        SearchArticleVo vo = new SearchArticleVo();
        vo.setKey(key);
        vo.setItems(list);
        return ResVo.ok(vo);
    }

    /**
     * 分类下的文章列表
     *
     * @param key
     * @return
     */
    @GetMapping(path = "list")
    public ResVo<NextPageHtmlVo> searchList(@RequestParam(value = "key", required = false) String key,
                                            @RequestParam(value = "page") Long page,
                                            @RequestParam(value = "size") Long size) {
        PageParam pageParam = buildPageParams(page, size);
        PageListVo<ArticleDTO> list = articleReadService.queryArticlesBySearchKey(key, pageParam);
        String html = templateEngineHelper.rendToVO("views/article-search-list/article/list", "articles", list);
        return ResVo.ok(new NextPageHtmlVo(html, list.getHasMore()));
    }
}
