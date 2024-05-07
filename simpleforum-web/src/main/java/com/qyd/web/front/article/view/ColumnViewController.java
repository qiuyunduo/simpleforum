package com.qyd.web.front.article.view;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.UserAIStatEnum;
import com.qyd.api.model.enums.column.ColumnArticleReadEnum;
import com.qyd.api.model.enums.column.ColumnTypeEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.*;
import com.qyd.api.model.vo.comment.dto.TopCommentDTO;
import com.qyd.api.model.vo.recommend.SideBarDTO;
import com.qyd.core.util.MarkdownConverter;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.ColumnService;
import com.qyd.service.comment.service.CommentReadService;
import com.qyd.service.sidebar.service.SidebarService;
import com.qyd.web.config.GlobalViewConfig;
import com.qyd.web.front.article.vo.ColumnVo;
import com.qyd.web.global.BaseViewController;
import com.qyd.web.global.SeoInjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 专栏入口
 *
 * @author 邱运铎
 * @date 2024-05-07 20:45
 */
@Controller
@RequestMapping(path = "column")
@RequiredArgsConstructor
public class ColumnViewController extends BaseViewController {
    private final ColumnService columnService;
    private final ArticleReadService articleReadService;
    private final CommentReadService commentReadService;
    private final SidebarService sidebarService;
    private final GlobalViewConfig globalViewConfig;

    /**
     * 专栏主页，展示专栏列表
     *
     * @param model
     * @return
     */
    @GetMapping(path = {"list", "/", "", "home"})
    public String list(Model model) {
        PageListVo<ColumnDTO> columns = columnService.listColumn(PageParam.newPageInstance());
        List<SideBarDTO> sideBars = sidebarService.queryColumnSideBarList();
        ColumnVo vo = new ColumnVo();
        vo.setColumns(columns);
        vo.setSideBarItems(sideBars);
        model.addAttribute("vo", vo);
        return "views/column-home/index";
    }

    /**
     * 专栏详情
     * 目前没有开发这个专门介绍专栏的页面，只有专栏列表和专栏阅读文章页面
     *
     * @param columnId
     * @param model
     * @return
     */
    @GetMapping(path = "{columnId}")
    public String column(@PathVariable("columnId") Long columnId, Model model) {
        ColumnDTO dto = columnService.queryColumInfo(columnId);
        model.addAttribute("vo", dto);
        return "views/column-index/index";
    }

    /**
     * 专栏文章的阅读界面
     *
     * @param columnId  专栏Id
     * @param section   节数（章节，排序都可以解释）从1开始
     * @param model
     * @return
     */
    @GetMapping(path = "{columnId}/{section}")
    public String articles(@PathVariable("columnId") Long columnId,
                           @PathVariable("section") Integer section,
                           Model model) {
        if (section <= 0) section = 1;
        // 查询专栏
        ColumnDTO column = columnService.queryBasicColumnInfo(columnId);

        ColumnArticleDO columnArticle = columnService.queryColumnArticle(columnId, section);
        Long articleId = columnArticle.getArticleId();
        // 文章信息
        ArticleDTO articleDTO = articleReadService.queryFullArticleInfo(articleId, ReqInfoContext.getReqInfo().getUserId());
        // 返回html格式的文档内容
        articleDTO.setContent(MarkdownConverter.markdownToHtml(articleDTO.getContent()));
        // 评论信息
        List<TopCommentDTO> comments = commentReadService.getArticleComments(articleId, PageParam.newPageInstance());
        // 热门评论
        TopCommentDTO hotComment = commentReadService.queryHotComment(articleId);
        // 专栏的所有文章列表
        List<SimpleArticleDTO> articles = columnService.queryColumnArticles(columnId);

        ColumnArticlesDTO vo = new ColumnArticlesDTO();
        vo.setArticle(articleDTO);
        vo.setComments(comments);
        vo.setHotComment(hotComment);
        vo.setColumn(columnId);
        vo.setSection(section);
        vo.setArticleList(articles);

        ArticleOtherDTO other = new ArticleOtherDTO();

        // 教程类型
        updateReadType(other, column, articleDTO, ColumnArticleReadEnum.valueOf(columnArticle.getReadType()));

        // 把文章翻页参数封装到这里
        // prev上一页 的 href跳转链接 和 是否显示的flag
        ColumnArticleFlipDTO flip = new ColumnArticleFlipDTO();
        flip.setPrevHref("/column/" + columnId + "/" + (section - 1));
        flip.setPrevShow(section > 1);
        // next下一页 的 href跳转链接 和 是否显示的flag
        flip.setNextHref("/column/" + columnId + "/" + (section + 1));
        flip.setNextShow(section < articles.size());
        other.setFlip(flip);
        vo.setOther(other);

        // 放入model中
        model.addAttribute("vo", vo);

        SpringUtil.getBean(SeoInjectService.class).initColumnSeo(vo, column);
        return "views/column-detail/index";
    }

    /**
     * 对于要求登录阅读的文章进行处理
     *
     * @param vo
     * @param column
     * @param articleDTO
     * @param articleReadEnum
     */
    private void updateReadType(ArticleOtherDTO vo, ColumnDTO column, ArticleDTO articleDTO,
                               ColumnArticleReadEnum articleReadEnum) {
        Long loginUser = ReqInfoContext.getReqInfo().getUserId();
        if (loginUser != null && loginUser.equals(articleDTO.getAuthor())) {
            vo.setReadType(ColumnTypeEnum.FREE.getType());
            return;
        }

        if (articleReadEnum == ColumnArticleReadEnum.COLUMN_TYPE) {
            // 专栏中额文章，没有特殊指定时，直接沿用专栏的规则
            if (column.getType() == ColumnTypeEnum.TIME_FREE.getType()) {
                // 如果是限时免费，看当前是否在限时时间内
                long now = System.currentTimeMillis();
                if (now > column.getFreeEndTime() || now < column.getFreeStartTime()) {
                    vo.setReadType(ColumnTypeEnum.LOGIN.getType());
                } else {
                    vo.setReadType(ColumnTypeEnum.FREE.getType());
                }
            } else {
                vo.setReadType(column.getType());
            }
        } else {
            // 直接使用文章特殊设置的规则
            vo.setReadType(articleReadEnum.getRead());
        }
        // 如果是请求 Or 登录阅读时，不返回全量的文章内容
        articleDTO.setContent(trimContent(vo.getReadType(), articleDTO.getContent()));
        // fix 关于 cover 封面， 文章详情的前段已经不显示了，这里直接删除
    }

    /**
     * 文章内容根据规则进行隐藏处理
     *
     * @param readType
     * @param content
     * @return
     */
    private String trimContent(int readType, String content) {
        if (readType == ColumnTypeEnum.STAR_READ.getType()) {
            // 判断登录用户是否绑定了星球， 如果是，则直接㱭完整的专栏内容
            if (ReqInfoContext.getReqInfo().getUser() != null
                    && ReqInfoContext.getReqInfo().getUser().getStarStatus() == UserAIStatEnum.FORMAL) {
                return content;
            }

            // 如果没有绑定星球，则返回 10% 的内容
            // 10% 从全局的配置参数中获取
            int count = Integer.parseInt(globalViewConfig.getStarArticleReadCount());
            return content.substring(0, content.length() * count / 100);
        }

        if (readType == ColumnTypeEnum.LOGIN.getType()
                && ReqInfoContext.getReqInfo().getUserId() == null) {
            // 如果是需要登录才能阅读的，但用户没有登录，则返回 20% 的内容
            int count = Integer.parseInt(globalViewConfig.getNeedLoginArticleReadCount());
            return content.substring(0, content.length() * count / 100);
        }

        return content;
    }
}
