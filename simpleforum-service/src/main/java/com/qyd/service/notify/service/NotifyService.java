package com.qyd.service.notify.service;

import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.notify.dto.NotifyMsgDTO;
import com.qyd.service.user.repository.entity.UserFootDO;

import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-04-24 18:05
 */
public interface NotifyService {

    /**
     * 查询用户未读消息总数量
     *
     * @param userId
     * @return
     */
    int queryUserNotifyMsgCount(Long userId);

    /**
     * 查询通知列表
     *
     * @param userId
     * @param type
     * @param page
     * @return
     */
    PageListVo<NotifyMsgDTO> queryUserNotices(Long userId, NotifyTypeEnum type, PageParam page);

    /**
     * 查询各个维度（评论，收藏，点赞，系统等）的未读消息数
     *
     *
     * @param userId
     * @return
     */
    Map<String, Integer> queryUnreadCounts(long userId);

    /**
     * 保存通知
     *
     * @param foot
     * @param notifyTypeEnum
     */
    void saveArticleNotify(UserFootDO foot, NotifyTypeEnum notifyTypeEnum);
}
