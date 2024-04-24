package com.qyd.service.notify.service.impl;

import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.notify.dto.NotifyMsgDTO;
import com.qyd.service.notify.repository.dao.NotifyMsgDao;
import com.qyd.service.notify.service.NotifyService;
import com.qyd.service.user.repository.entity.UserFootDO;
import com.qyd.service.user.repository.entity.UserRelationDO;
import com.qyd.service.user.service.UserRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-04-24 18:21
 */
@Service
public class NotifyServiceImpl implements NotifyService {

    @Resource
    private NotifyMsgDao notifyMsgDao;

    @Resource
    private UserRelationService userRelationService;

    @Override
    public int queryUserNotifyMsgCount(Long userId) {
        return 0;
    }

    @Override
    public PageListVo<NotifyMsgDTO> queryUserNotices(Long userId, NotifyTypeEnum type, PageParam page) {
        return null;
    }

    @Override
    public Map<String, Integer> queryUnreadCounts(long userId) {
        return null;
    }

    @Override
    public void saveArticleNotify(UserFootDO foot, NotifyTypeEnum notifyTypeEnum) {

    }
}
