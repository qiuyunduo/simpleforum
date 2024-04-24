package com.qyd.service.user.service.relation;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.FollowStateEnum;
import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.notify.NotifyMsgEvent;
import com.qyd.api.model.vo.user.UserRelationReq;
import com.qyd.api.model.vo.user.dto.FollowUserInfoDTO;
import com.qyd.core.util.MapUtils;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.user.converter.UserConverter;
import com.qyd.service.user.repository.dao.UserRelationDao;
import com.qyd.service.user.repository.entity.UserRelationDO;
import com.qyd.service.user.service.UserRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户关系Service
 *
 * @author 邱运铎
 * @date 2024-04-24 22:56
 */
@Service
public class UserRelationServiceImpl implements UserRelationService {

    @Resource
    private UserRelationDao userRelationDao;

    /**
     * 查询用户关注列表
     * 这里须知道作者在这里仅仅是展示10条数据，并没有进行分页处理
     * 下面的fans也是一样
     *
     * @param userId
     * @param pageParam
     * @return
     */
    @Override
    public PageListVo<FollowUserInfoDTO> getUserFollowList(Long userId, PageParam pageParam) {
        List<FollowUserInfoDTO> follows = userRelationDao.listUserFollows(userId, pageParam);
        return PageListVo.newVo(follows, pageParam.getPageSize());
    }

    @Override
    public PageListVo<FollowUserInfoDTO> getUserFansList(Long userId, PageParam pageParam) {
        List<FollowUserInfoDTO> fans = userRelationDao.listUserFans(userId, pageParam);
        return PageListVo.newVo(fans, pageParam.getPageSize());
    }

    @Override
    public void updateUserFollowRelation(PageListVo<FollowUserInfoDTO> followList, Long loginUserId) {
        if (loginUserId == null) {
            followList.getList().forEach(r -> {
                r.setRelationId(null);
                r.setFollowed(false);
            });
            return;
        }

        // 判断登录用户与给定的用户列表的关注关系
        Set<Long> userIds = followList.getList().stream()
                .map(FollowUserInfoDTO::getUserId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        List<UserRelationDO> relationList = userRelationDao.listUserRelations(loginUserId, userIds);
        Map<Long, UserRelationDO> relationDOMap = MapUtils.toMap(relationList, UserRelationDO::getUserId, r -> r);
        followList.getList().forEach(follow -> {
            UserRelationDO relation = relationDOMap.get(follow.getUserId());
            if (relation == null) {
                follow.setRelationId(null);
                follow.setFollowed(false);
            } else if (Objects.equals(relation.getFollowState(), FollowStateEnum.FOLLOW.getCode())) {
                follow.setRelationId(relation.getId());
                follow.setFollowed(true);
            } else {
                follow.setRelationId(relation.getId());
                follow.setFollowed(false);
            }
        });
    }

    /**
     * 根据登录用户从给定的用户列表中，找出已关注的用户id
     * 本质登录用户也是某用户的粉丝，所以可以称为某粉丝用户在某用户列表已关注的id
     *
     * @param userIds    用户列表（可以是关注列表，也可以是粉丝列表，或者其他场景）
     * @param fansUserId 当前粉丝id 目前主要是当前登录用户
     * @return  从userIds中找出返回fanUserId已关注的id列表
     */
    @Override
    public Set<Long> getFollowUserId(List<Long> userIds, Long fansUserId) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptySet();
        }
        List<UserRelationDO> relationList = userRelationDao.listUserRelations(fansUserId, userIds);
        Map<Long, UserRelationDO> relationMap = MapUtils.toMap(relationList, UserRelationDO::getUserId, r -> r);
        return relationMap.values().stream()
                // 这里过滤掉那些类型为CANCEL_FOLLOW的记录，因为这些也会被查出来
                // 思考： 这里在sql中直接对取消关注的进行过滤掉会不会更好一些
                .filter(s -> s.getFollowState().equals(FollowStateEnum.FOLLOW.getCode()))
                .map(UserRelationDO::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public void saveUserRelation(UserRelationReq req) {
        // 查询是否存在
        UserRelationDO userRelationDO = userRelationDao.getUserRelationByUserId(req.getUserId(), ReqInfoContext.getReqInfo().getUserId());
        if (userRelationDO == null) {
            userRelationDO = UserConverter.toDO(req);
            userRelationDao.save(userRelationDO);
            // 发布关注时间
            SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.FOLLOW, userRelationDO));
            return;
        }

        // 将是否关注状态重置
        userRelationDO.setFollowState(req.getFollowed() ? FollowStateEnum.FOLLOW.getCode() : FollowStateEnum.CANCEL_FOLLOW.getCode());
        userRelationDao.updateById(userRelationDO);
        // 发布关注，取消关注事件
        SpringUtil.publishEvent(new NotifyMsgEvent<>(this, req.getFollowed() ? NotifyTypeEnum.FOLLOW : NotifyTypeEnum.CANCEL_FOLLOW, userRelationDO));
    }
}
