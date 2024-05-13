package com.qyd.web.front.article.rest;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.DocumentTypeEnum;
import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.enums.OperateTypeEnum;
import com.qyd.api.model.vo.*;
import com.qyd.api.model.vo.article.ArticlePostReq;
import com.qyd.api.model.vo.article.ContentPostReq;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.notify.NotifyMsgEvent;
import com.qyd.core.common.CommonConstants;
import com.qyd.core.mdc.MdcDot;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.core.util.JsonUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.CategoryDO;
import com.qyd.service.article.repository.entity.TagDO;
import com.qyd.service.article.service.*;
import com.qyd.service.notify.service.RabbitmqService;
import com.qyd.service.user.repository.entity.UserFootDO;
import com.qyd.service.user.service.UserFootService;
import com.qyd.web.component.TemplateEngineHelper;
import com.rabbitmq.client.BuiltinExchangeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * 返回json的文章相关数据,以及提供给knife4j的接口方便测试
 *
 * @author 邱运铎
 * @date 2024-05-04 19:46
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "article/api")
public class ArticleRestController {
    private final UserFootService userFootService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final ArticleReadService articleReadService;
    private final ArticleWriteService articleWriteService;
    private final TemplateEngineHelper templateEngineHelper;
    private final ArticleRecommendService articleRecommendService;
    private final RabbitmqService rabbitmqService;

    /**
     * 文章的关联推荐
     *
     * @param articleId
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("recommend")
    @MdcDot(bizCode = "#articleId")
    public ResVo<NextPageHtmlVo> recommend(@RequestParam(value = "articleId") Long articleId,
                                           @RequestParam(name = "page") Long page,
                                           @RequestParam(name = "size", required = false) Long size) {
        size = Optional.ofNullable(size).orElse(PageParam.DEFAULT_PAGE_SIZE);
        size = Math.min(size, PageParam.DEFAULT_PAGE_SIZE);
        PageListVo<ArticleDTO> articles = articleRecommendService.relatedRecommend(articleId, PageParam.newPageInstance(page, size));
        String html = templateEngineHelper.rendToVO("views/article-detail/article/list", "articles", articles);
        return ResVo.ok(new NextPageHtmlVo(html, articles.getHasMore()));
    }

    /**
     * 提交文章生成总结摘要
     *
     * @param req
     * @return
     */
    @PostMapping("generateSummary")
    public ResVo<String> generateSummary(@RequestBody ContentPostReq req) {
        return ResVo.ok(articleReadService.generateSummary(req.getContent()));
    }

    /**
     * 查询所有标签
     *
     * @param key
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping(path = "tag/list")
    public ResVo<PageVo<TagDTO>> queryTags(@RequestParam(name = "key", required = false) String key,
                                  @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageVo<TagDTO> tagDTOPageVo = tagService.queryTags(key, PageParam.newPageInstance(pageNumber, pageSize));
        return ResVo.ok(tagDTOPageVo);
    }

    /**
     * 获取所有的分类
     *
     * @param categoryId
     * @return
     */
    @GetMapping(path = "category/list")
    public ResVo<List<CategoryDTO>> getCategoryList(@RequestParam(name = "categoryId", required = false) Long categoryId) {
        List<CategoryDTO> list = categoryService.loadAllCategories();
        list.forEach(c -> c.setSelected(c.getCategoryId().equals(categoryId)));
        return ResVo.ok(list);
    }

    /**
     * 收藏，点赞等相关操作
     *
     * @param articleId
     * @param type  取值如下 @see
     * @see OperateTypeEnum#getCode()
     * @return
     */
    @MdcDot(bizCode = "#articleId")
    @Permission(role = UserRole.LOGIN)
    @GetMapping(path = "favor")
    public ResVo<Boolean> favor(@RequestParam(name = "articleId") Long articleId,
                                @RequestParam(name = "type") Integer type) throws IOException, TimeoutException {
        if (log.isDebugEnabled()) {
            // 这里答应点赞日志并非指当前操作是点赞，而是需要一个标记就进入到当前方法，这里可以是点赞收藏评论等
            log.debug("开始点赞: {}", type);
        }

        OperateTypeEnum operate = OperateTypeEnum.fromCode(type);
        if (operate == OperateTypeEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, type + "非法");
        }

        // 要求文章必须存在
        ArticleDO article = articleReadService.queryBasicArticle(articleId);
        if (article == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章不存在！");
        }

        UserFootDO foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, articleId, article.getUserId(),
                ReqInfoContext.getReqInfo().getUserId(),
                operate);
        // 点赞，收藏消息
        NotifyTypeEnum notifyType = OperateTypeEnum.getNotifyType(operate);

        // 点赞消息走 RabbitMQ，其他走 Java 内置消息机制
        if (notifyType.equals(NotifyTypeEnum.PRAISE) && rabbitmqService.enabled()) {
            rabbitmqService.publishMsg(
                    CommonConstants.EXCHANGE_NAME_DIRECT,
                    BuiltinExchangeType.DIRECT,
                    CommonConstants.QUEUE_KEY_PRAISE,
                    JsonUtil.toStr(foot)
            );
        } else {
            Optional.ofNullable(notifyType).ifPresent(notify -> SpringUtil.publishEvent(new NotifyMsgEvent<>(this, notify, foot)));
        }

        if (log.isDebugEnabled()) {
            log.info("点赞结束：{}", type);
        }
        return ResVo.ok(true);
    }

    /**
     * 发布文章，完成后跳转到详情页
     * - 这里有一个重定向的知识点
     * - fixme: 可以看作者的这个文章： 博文：* [5.请求重定向 | 一灰灰Learning](https://hhui.top/spring-web/02.response/05.190929-springboot%E7%B3%BB%E5%88%97%E6%95%99%E7%A8%8Bweb%E7%AF%87%E4%B9%8B%E9%87%8D%E5%AE%9A%E5%90%91/)
     *
     * @param req
     * @param response
     * @return
     */
    @MdcDot(bizCode = "#req.articleId")
    @PostMapping(path = "post")
    @Permission(role = UserRole.LOGIN)
    public ResVo<Long> post(@RequestBody ArticlePostReq req, HttpServletResponse response) throws IOException {
        Long id = articleWriteService.saveArticle(req, ReqInfoContext.getReqInfo().getUserId());
        // 如果使用后端重定向可以使用下面两种策略
        // 1. 在 controller 下面
//        return "redirect: /article/detail/" + id;
        // 2. 在restController下面
//        response.sendRedirect("/article/detail/" + id);
        // 这里让前端进行重定向处理
        return ResVo.ok(id);
    }

    /**
     * 文章删除
     *
     * @return
     */
    @MdcDot(bizCode = "#articleId")
    @Permission(role = UserRole.LOGIN)
    @RequestMapping(path = "delete")
    public ResVo<Boolean> delete(@RequestParam(name = "articleId") Long articleId) {
        articleWriteService.deleteArticle(articleId, ReqInfoContext.getReqInfo().getUserId());
        return ResVo.ok(true);
    }
}
