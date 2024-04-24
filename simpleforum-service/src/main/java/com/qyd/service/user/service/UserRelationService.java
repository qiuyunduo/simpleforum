package com.qyd.service.user.service;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.user.UserRelationReq;
import com.qyd.api.model.vo.user.dto.FollowUserInfoDTO;

import java.util.List;
import java.util.Set;

/**
 * 用户关系Service接口
 *
 * @author 邱运铎
 * @date 2024-04-24 22:45
 */
public interface UserRelationService {

    /**
     * 获取用户关注的用户，
     *
     * @param userId
     * @param pageParam
     * @return
     */
    PageListVo<FollowUserInfoDTO> getUserFollowList(Long userId, PageParam pageParam);

    /**
     * 用户的粉丝
     *
     * @param userId
     * @param pageParam
     * @return
     */
    PageListVo<FollowUserInfoDTO> getUserFansList(Long userId, PageParam pageParam);

    /**
     * 更新当前登录用户与列表中的用户的关注关系
     *
     * @param followList
     * @param loginUserId
     */
    void updateUserFollowRelation(PageListVo<FollowUserInfoDTO> followList, Long loginUserId);

    /**
     * 根据登录用户从给定的用户列表中，找出已关注的用户id
     * 这里主要的业务场景是在查看他人的个人主页中关注和粉丝用户的时候
     *
     * @param userIds
     * @param loginUserId
     * @return
     */
    Set<Long> getFollowUserId(List<Long> userIds, Long loginUserId);

    /**
     * 保存用户关系: 关注or取消关注
     *
     * @param req
     * @throws Exception
     */
    void saveUserRelation(UserRelationReq req);
}
