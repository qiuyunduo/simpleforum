package com.qyd.web.front.search.view;

import com.qyd.web.front.home.helper.IndexRecommendHelper;
import com.qyd.web.front.home.vo.IndexVo;
import com.qyd.web.global.BaseViewController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * 搜索服务入口
 * 作者目前只支持文章搜索
 *
 * @author 邱运铎
 * @date 2024-05-09 17:21
 */
@Controller
public class SearchViewController extends BaseViewController {

    @Resource
    private IndexRecommendHelper indexRecommendHelper;

    /**
     * 查询文章列表
     *
     * @return
     */
    @GetMapping(path = "search")
    public String searchArticleList(@RequestParam(value = "key") String key,
                                    Model model) {
        if (!StringUtils.isBlank(key)) {
            IndexVo vo = indexRecommendHelper.buildSearchVo(key);
            model.addAttribute("vo", vo);
        }
        return "views/article-search-list/index";
    }
}
