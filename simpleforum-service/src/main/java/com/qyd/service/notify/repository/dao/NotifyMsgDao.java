package com.qyd.service.notify.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyd.api.model.enums.NotifyStatEnum;
import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.notify.dto.NotifyMsgDTO;
import com.qyd.service.notify.repository.entity.NotifyMsgDO;
import com.qyd.service.notify.repository.mapper.NotifyMsgMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 邱运铎
 * @date 2024-04-24 18:23
 */
@Repository
public class NotifyMsgDao extends ServiceImpl<NotifyMsgMapper, NotifyMsgDO> {

    /**
     * 查询消息记录， 用于幂等过滤
     * 个人猜测应该是在创建消息的时候现在数据库判断是否已经有了该条消息
     * 避免重复创建，保证幂等
     *
     * @param msg
     * @return
     */
    public NotifyMsgDO getByUserIdRelatedIdAndType(NotifyMsgDO msg) {
        List<NotifyMsgDO> list = lambdaQuery()
                .eq(NotifyMsgDO::getNotifyUserId, msg.getNotifyUserId())
                .eq(NotifyMsgDO::getOperateUserId, msg.getOperateUserId())
                .eq(NotifyMsgDO::getType, msg.getType())
                .eq(NotifyMsgDO::getRelatedId, msg.getRelatedId())
                .page(new Page<>(0, 1))
                .getRecords();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 查询用户的消息通知数量
     *
     * @param userId
     * @param state
     * @return
     */
    public int countByUserIdAndStat(long userId, Integer state) {
        return lambdaQuery()
                .eq(NotifyMsgDO::getNotifyUserId, userId)
                .eq(state != null , NotifyMsgDO::getState, state)
                .count().intValue();
    }

    /**
     * 查询用户各类型的未读消息数量
     *
     * @param userId
     * @param state
     * @return
     */
    public Map<Integer, Integer> groupCountByUserIdAndStat(long userId, Integer state) {
        QueryWrapper<NotifyMsgDO> wrapper = new QueryWrapper<>();
        wrapper.select("type, count(*) as cnt");
        wrapper.eq("notify_user_id", userId);
        if (state != null) {
            wrapper.eq("state", state);
        }
        wrapper.groupBy("type");
        List<Map<String, Object>> maps = listMaps(wrapper);
        Map<Integer, Integer> result = new HashMap<>();
        maps.forEach(s -> {
            result.put(Integer.valueOf(s.get("type").toString()), Integer.valueOf(s.get("cnt").toString()));
        });
        return result;
    }

    /**
     * 查询用户消息列表
     *
     * @param userId
     * @param type
     * @param page
     * @return
     */
    public List<NotifyMsgDTO> listNotifyMsgByUserIdAndType(long userId, NotifyTypeEnum type, PageParam page) {
        switch (type) {
            case REPLY:
            case COMMENT:
            case COLLECT:
            case PRAISE:
                return baseMapper.listArticleRelatedNotices(userId, type.getType(), page);
            default:
                return baseMapper.listNormalNotices(userId, type.getType(), page);
        }
    }

    /**
     * 设置消息为已读
     *
     * @param list
     */
    public void updateNotifyMsgToRead(List<NotifyMsgDTO> list) {
        List<Long> ids = list.stream()
                // filter 过滤中的条件是用来匹配符合该条件的放入stream中，而非将符合条件的过滤掉。
                .filter(s -> s.getState() == NotifyStatEnum.UNREAD.getStat())
                .map(NotifyMsgDTO::getMsgId)
                .collect(Collectors.toList());
        if (!ids.isEmpty()) {
            baseMapper.updateNoticeRead(ids);
        }
    }
}
