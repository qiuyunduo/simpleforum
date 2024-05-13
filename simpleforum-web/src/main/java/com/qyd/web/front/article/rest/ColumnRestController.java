package com.qyd.web.front.article.rest;

import com.qyd.api.model.vo.NextPageHtmlVo;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.article.dto.ColumnDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.service.article.service.ColumnService;
import com.qyd.web.component.TemplateEngineHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author 邱运铎
 * @date 2024-05-08 19:44
 */
@RestController
@RequestMapping(path = "column/api")
public class ColumnRestController {
    @Autowired
    private ColumnService columnService;

    @Autowired
    private TemplateEngineHelper templateEngineHelper;

    /**
     * 翻页的专栏列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = "list")
    public ResVo<NextPageHtmlVo> list(@RequestParam(name = "page") Long page,
                                      @RequestParam(name = "size", required = false) Long size) {
        if (page <= 0) {
            page = 1L;
        }
        size = Optional.ofNullable(size).orElse(PageParam.DEFAULT_PAGE_SIZE);
        size = Math.min(size, PageParam.DEFAULT_PAGE_SIZE);
        PageListVo<ColumnDTO> list = columnService.listColumn(PageParam.newPageInstance(page, size));
        String html = templateEngineHelper.rendToVO("biz/column/list", "columns", list);
        return ResVo.ok(new NextPageHtmlVo(html, list.getHasMore()));
    }

    @GetMapping(path = "menu/{column}")
    public ResVo<NextPageHtmlVo> columnMenus(@PathVariable("column") Long columnId) {
        List<SimpleArticleDTO> articleList = columnService.queryColumnArticles(columnId);
        String html = templateEngineHelper.rendToVO("biz/column/menus", "menu", articleList);
        return ResVo.ok(new NextPageHtmlVo(html, false));
    }
}
