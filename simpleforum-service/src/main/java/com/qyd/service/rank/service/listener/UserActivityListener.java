package com.qyd.service.rank.service.listener;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.ArticleEventEnum;
import com.qyd.api.model.event.ArticleMsgEvent;
import com.qyd.api.model.vo.notify.NotifyMsgEvent;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.comment.repository.entity.CommentDO;
import com.qyd.service.rank.service.UserActivityRankService;
import com.qyd.service.rank.service.model.ActivityScoreBo;
import com.qyd.service.user.repository.entity.UserFootDO;
import com.qyd.service.user.repository.entity.UserRelationDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户活跃度相关的罅隙监听器
 *
 * @author 邱运铎
 * @date 2024-05-01 17:51
 */
@Component
public class UserActivityListener {

    @Autowired
    private UserActivityRankService userActivityRankService;

    /**
     * 根据用户操作行为，进行相应积分变化计算
     * 对于@Async开启异步，一般是方法返回值为null,因为不关心返回值所以让
     * 其异步执行即可，不必方法逻辑处理完，直接返回null,方法继续处理不影响主业务
     * @param msgEvent
     */
    @Async
    @EventListener(NotifyMsgEvent.class)
    public void notifyMsgListener(NotifyMsgEvent msgEvent) {
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        /**
         * 这里记录一个switch的知识点：
         * 在前面case中定义的变量，可以在后续case中使用该变量，但如果使用一定要赋值
         * 因为 switch case 中的变量作用域是当前定义变量case和之后的case.
         * 但如果把case中代码包裹在一个{}中，则case中的变量作用域就是该{}中，后续case无法使用该case定义的白能量
         * 例如下面case COLLECT: 中代码使用 {} 包裹，后续case中的foot变量均会报编译错误
         *
         */
        switch (msgEvent.getNotifyType()) {
            case COMMENT:
            case REPLY:
                CommentDO commentDO = (CommentDO) msgEvent.getContent();
                ActivityScoreBo activityScoreBo = new ActivityScoreBo();
                activityScoreBo.setRate(true);
                activityScoreBo.setArticleId(commentDO.getArticleId());
                userActivityRankService.addActivityScore(userId,activityScoreBo);
                break;
            case COLLECT:
                UserFootDO foot = (UserFootDO) msgEvent.getContent();
                activityScoreBo = new ActivityScoreBo();
                activityScoreBo.setCollect(true);
                activityScoreBo.setArticleId(foot.getDocumentId());
                userActivityRankService.addActivityScore(userId, activityScoreBo);
                break;
            case CANCEL_COLLECT:
                foot = (UserFootDO) msgEvent.getContent();
                activityScoreBo = new ActivityScoreBo();
                activityScoreBo.setCollect(false);
                activityScoreBo.setArticleId(foot.getDocumentId());
                userActivityRankService.addActivityScore(userId, activityScoreBo);
                break;
            case PRAISE:
                foot = (UserFootDO) msgEvent.getContent();
                activityScoreBo = new ActivityScoreBo();
                activityScoreBo.setPraise(true);
                activityScoreBo.setArticleId(foot.getDocumentId());
                userActivityRankService.addActivityScore(userId, activityScoreBo);
                break;
            case CANCEL_PRAISE:
                foot = (UserFootDO) msgEvent.getContent();
                activityScoreBo = new ActivityScoreBo();
                activityScoreBo.setPraise(false);
                activityScoreBo.setArticleId(foot.getDocumentId());
                userActivityRankService.addActivityScore(userId, activityScoreBo);
                break;
            case FOLLOW:
                UserRelationDO relation = (UserRelationDO) msgEvent.getContent();
                activityScoreBo = new ActivityScoreBo();
                activityScoreBo.setFollow(true);
                activityScoreBo.setArticleId(relation.getUserId());
                userActivityRankService.addActivityScore(userId, activityScoreBo);
                break;
            case CANCEL_FOLLOW:
                relation = (UserRelationDO) msgEvent.getContent();
                activityScoreBo = new ActivityScoreBo();
                activityScoreBo.setFollow(false);
                activityScoreBo.setArticleId(relation.getUserId());
                userActivityRankService.addActivityScore(userId, activityScoreBo);
                break;
            default:
        }
    }

    /**
     * 发布文章，更新对应的积分
     *
     * @param event
     */
    @Async
    @EventListener(ArticleMsgEvent.class)
    public void publishArticleListener(ArticleMsgEvent<ArticleDO> event) {
        ArticleEventEnum type = event.getType();
        if (type == ArticleEventEnum.ONLINE) {
            userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(),
                    new ActivityScoreBo()
                            .setPublishArticle(true)
                            .setArticleId(event.getContent().getId()));
        }
    }
}
