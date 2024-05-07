package com.qyd.web.front.article.view;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.ArticleOtherDTO;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.api.model.vo.comment.dto.TopCommentDTO;
import com.qyd.api.model.vo.recommend.SideBarDTO;
import com.qyd.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.core.util.MarkdownConverter;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.CategoryService;
import com.qyd.service.article.service.ColumnService;
import com.qyd.service.comment.service.CommentReadService;
import com.qyd.service.sidebar.service.SidebarService;
import com.qyd.service.user.service.UserService;
import com.qyd.web.front.article.vo.ArticleDetailVo;
import com.qyd.web.front.article.vo.ArticleEditVo;
import com.qyd.web.global.BaseViewController;
import com.qyd.web.global.SeoInjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 文章
 * todo: 所有的入口都放在一个controller，会导致功能划分非常混乱
 * ： 文章列表
 * ： 文章编辑
 * ： 文章详情
 * ----
 * - 返回视图 view
 * - 返回json数据
 *
 * @author 邱运铎
 * @date 2024-05-07 19:01
 */
@Controller
@RequestMapping(path = "article")
@RequiredArgsConstructor
public class ArticleViewController extends BaseViewController {
    private final ArticleReadService articleService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CommentReadService commentService;
    private final SidebarService sidebarService;
    private final ColumnService columnService;

    /**
     * 文章编辑页
     *
     * @param articleId
     * @param model
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @GetMapping(path = "edit")
    public String edit(@RequestParam(required = false) Long articleId, Model model) {
        ArticleEditVo vo = new ArticleEditVo();
        if (articleId != null) {
            ArticleDTO article = articleService.queryDetailArticleInfo(articleId);
            vo.setArticle(article);
            if (!Objects.equals(article.getAuthor(), ReqInfoContext.getReqInfo().getUserId())) {
                // 非作者本人 没有编辑的权限
                model.addAttribute("toast", "内容不存在");
                return "redirect:403";
            }

            List<CategoryDTO> categoryList = categoryService.loadAllCategories();
            categoryList.forEach(c -> {
                c.setSelected(c.getCategoryId().equals(article.getCategory().getCategoryId()));
            });
            vo.setCategories(categoryList);
            vo.setTags(article.getTags());
        } else {
            List<CategoryDTO> categoryList = categoryService.loadAllCategories();
            vo.setCategories(categoryList);
            vo.setTags(Collections.emptyList());
        }
        model.addAttribute("vo", vo);
        return "views/article-edit/index";
    }


    /**
     * 文章详情页
     * - 参数解析知识点
     * - fixme * [1.Get请求参数解析姿势汇总 | 一灰灰Learning](https://spring.hhui.top/spring-blog/2019/08/24/190824-SpringBoot%E7%B3%BB%E5%88%97%E6%95%99%E7%A8%8Bweb%E7%AF%87%E4%B9%8BGet%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0%E8%A7%A3%E6%9E%90%E5%A7%BF%E5%8A%BF%E6%B1%87%E6%80%BB/)
     *
     * @param articleId
     * @param model
     * @return
     * @throws IOException
     */
    @GetMapping("detail/{articleId}")
    public String detail(@PathVariable(name = "articleId") Long articleId, Model model) throws IOException {
        // 针对专栏文章，做一个重定向
        ColumnArticleDO columnArticle = columnService.getColumnArticleRelation(articleId);
        if (columnArticle != null) {
            return String.format("redict:/column/%d/%d", columnArticle.getColumnId(), columnArticle.getSection());
        }

        ArticleDetailVo vo = new ArticleDetailVo();
        // 文章相关信息
        ArticleDTO articleDTO = articleService.queryFullArticleInfo(articleId, ReqInfoContext.getReqInfo().getUserId());
        // 返回给前端页面时，转换为html格式
        articleDTO.setContent(MarkdownConverter.markdownToHtml(articleDTO.getContent()));
        vo.setArticle(articleDTO);

        // 评论信息
        List<TopCommentDTO> comments = commentService.getArticleComments(articleId, PageParam.newPageInstance(1L, 10L));
        vo.setComments(comments);

        // 热门评论
        TopCommentDTO hotComment = commentService.queryHotComment(articleId);
        vo.setHotComment(hotComment);

        // 其他信息封装
        ArticleOtherDTO other = new ArticleOtherDTO();
        // 作者信息
        UserStatisticInfoDTO user = userService.queryUserInfoWithStatistic(articleDTO.getAuthor());
        articleDTO.setAuthorName(user.getUserName());
        articleDTO.setAuthorAvatar(user.getPhoto());
        vo.setAuthor(user);

        vo.setOther(other);

        // 详情页的侧边推荐信息
        List<SideBarDTO> sideBars = sidebarService.queryArticleDetailSideBarList(articleDTO.getAuthor(), articleDTO.getArticleId());
        vo.setSideBarItems(sideBars);
        model.addAttribute("vo", vo);

        SpringUtil.getBean(SeoInjectService.class).initArticleSeo(vo);
        return "views/article-detail/index";
    }
 }