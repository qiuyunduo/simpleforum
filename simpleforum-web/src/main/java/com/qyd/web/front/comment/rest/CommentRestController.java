package com.qyd.web.front.comment.rest;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.DocumentTypeEnum;
import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.enums.OperateTypeEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.comment.CommentSaveReq;
import com.qyd.api.model.vo.comment.dto.TopCommentDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.notify.NotifyMsgEvent;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.core.util.NumUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.article.conveter.ArticleConverter;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.comment.repository.entity.CommentDO;
import com.qyd.service.comment.service.CommentReadService;
import com.qyd.service.comment.service.CommentWriteService;
import com.qyd.service.user.repository.entity.UserFootDO;
import com.qyd.service.user.service.UserFootService;
import com.qyd.web.component.TemplateEngineHelper;
import com.qyd.web.front.article.vo.ArticleDetailVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 评论
 *
 * @author 邱运铎
 * @date 2024-05-09 14:59
 */
@RestController
@RequestMapping(path = "comment/api")
@RequiredArgsConstructor
public class CommentRestController {
    private final ArticleReadService articleReadService;
    private final CommentReadService commentReadService;
    private final CommentWriteService commentWriteService;
    private final UserFootService userFootService;
    private final TemplateEngineHelper templateEngineHelper;

    /**
     * 评论列表页
     *
     * @param articleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(path = "list")
    public ResVo<List<TopCommentDTO>> list(Long articleId, Long pageNum, Long pageSize) {
        if (NumUtil.nullOrZero(articleId)) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章id为空");
        }
        pageNum = Optional.ofNullable(pageNum).orElse(PageParam.DEFAULT_PAGE_NUM);
        pageSize  = Optional.ofNullable(pageSize).orElse(PageParam.DEFAULT_PAGE_SIZE);
        List<TopCommentDTO> result = commentReadService.getArticleComments(articleId, PageParam.newPageInstance(pageNum, pageSize));
        return ResVo.ok(result);
    }

    /**
     * 保存评论
     *
     * @param req
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping("post")
    public ResVo<String> save(@RequestBody CommentSaveReq req) {
        if (req.getArticleId() == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章id为空");
        }
        ArticleDO article = articleReadService.queryBasicArticle(req.getArticleId());
        if (article == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章不存在");
        }

        // 保存评论
        req.setUserId(ReqInfoContext.getReqInfo().getUserId());
        req.setCommentContent(StringEscapeUtils.escapeHtml3(req.getCommentContent()));
        commentWriteService.saveComment(req);

        // 返回新的评论信息，用于实时更新详情页的评论列表
        ArticleDetailVo vo = new ArticleDetailVo();
        vo.setArticle(ArticleConverter.toDto(article));
        // 评论信息
        List<TopCommentDTO> commentDTOS = commentReadService.getArticleComments(req.getArticleId(), PageParam.newPageInstance());
        vo.setComments(commentDTOS);

        // 热门评论
        TopCommentDTO hotComment = commentReadService.queryHotComment(req.getArticleId());
        vo.setHotComment(hotComment);
        String content = templateEngineHelper.render("views/article-detail/comment/index", vo);
        return ResVo.ok(content);
    }

    /**
     * 删除评论
     *
     * @param commentId
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @RequestMapping(path = "delete")
    public ResVo<Boolean> delete(Long commentId) {
        commentWriteService.deleteComment(commentId, ReqInfoContext.getReqInfo().getUserId());
        return ResVo.ok(true);
    }

    /**
     * 收藏、点赞相关操作
     *
     * @param commentId
     * @param type 如下@see
     * @see OperateTypeEnum#getCode()
     * @return
     */
    public ResVo<Boolean> favor(@RequestParam(name = "commentId") Long commentId,
                                @RequestParam(name = "type") Integer type) {
        OperateTypeEnum operate = OperateTypeEnum.fromCode(type);
        if (operate == OperateTypeEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "非法type: " + type);
        }

        // 要求文章必须存在
        CommentDO comment = commentReadService.queryComment(commentId);
        if (comment == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "评论不存在");
        }

        UserFootDO foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT,
                commentId,
                comment.getUserId(),
                ReqInfoContext.getReqInfo().getUserId(),
                operate);
        // 点赞、收藏消息
        NotifyTypeEnum notifyType = OperateTypeEnum.getNotifyType(operate);
        Optional.ofNullable(notifyType).ifPresent(notify -> SpringUtil.publishEvent(new NotifyMsgEvent<>(this, notify, foot)));
        return ResVo.ok(true);
    }
}
