package com.qyd.service.user.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyd.api.model.enums.FollowStateEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.user.dto.FollowUserInfoDTO;
import com.qyd.service.user.repository.entity.UserRelationDO;
import com.qyd.service.user.repository.mapper.UserRelationMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 用户相关的DB操作
 *
 * @author 邱运铎
 * @date 2024-04-20 18:30
 */
@Repository
public class UserRelationDao extends ServiceImpl<UserRelationMapper, UserRelationDO> {

    /**
     * 查询用户的关注列表
     *
     * @param followUserId
     * @param pageParam
     * @return
     */
    public List<FollowUserInfoDTO> listUserFollows(Long followUserId, PageParam pageParam) {
        return baseMapper.queryUserFollowList(followUserId, pageParam);
    }

    /**
     * 查询用户的粉丝列表，即关注UserId的用户
     *
     * @param userId
     * @param pageParam
     * @return
     */
    public List<FollowUserInfoDTO> listUserFans(Long userId, PageParam pageParam) {
        return baseMapper.queryUserFansList(userId, pageParam);
    }

    /**
     * 查询followUserId与给定的用户列表的关联关系
     * todo 什么地方会用到该方法，暂不清楚业务，该方法业务看代码是
     *  查找某个用户的所有关注且该关注的用户需要在给定的集合中
     * 上面问题已解决。该方式适用于的场景如下：
     *  登录用户在查看他人个人主页的关注用户和粉丝用户时
     *  查找当前用户的关注和粉丝中那些是当前登录用户也关注的，。
     *  补充一个场景： 在首页查看关注消息的情况也会用到该方法
     *
     * @param followUserId  粉丝用户ID
     * @param targetUserId  关注者用户id列表
     * @return
     */
    public List<UserRelationDO> listUserRelations(Long followUserId, Collection<Long> targetUserId) {
        return lambdaQuery()
                .eq(UserRelationDO::getFollowUserId, followUserId)
                .in(UserRelationDO::getUserId, targetUserId)
                .list();
    }

    public Long queryUserFollowCount(Long userId) {
        QueryWrapper<UserRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserRelationDO::getFollowUserId, userId)
                .eq(UserRelationDO::getFollowState, FollowStateEnum.FOLLOW.getCode());
        return baseMapper.selectCount(queryWrapper);
    }

    public Long queryUserFansCount(Long userId) {
        // 这里使用Wrapper.lambdaQuery() ，原作者使用new QueryWrapper 然后.lambda。 目前认为结果是一样的
        LambdaQueryWrapper<UserRelationDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper
                .eq(UserRelationDO::getUserId, userId)
                .eq(UserRelationDO::getFollowState, FollowStateEnum.FOLLOW.getCode());
        return baseMapper.selectCount(queryWrapper);
    }

    /**
     * 获取关注信息， 查看某个用户是否为登录用户的粉丝
     *
     * @param userId        登录用户， 被关注者
     * @param followUserId  关注的用户
     * @return
     */
    public UserRelationDO getUserRelationByUserId(Long userId, Long followUserId) {
        LambdaQueryWrapper<UserRelationDO> query = Wrappers.lambdaQuery();
        query.eq(UserRelationDO::getUserId, userId)
                .eq(UserRelationDO::getFollowUserId, followUserId)
                .eq(UserRelationDO::getFollowState, FollowStateEnum.FOLLOW.getCode());
        return baseMapper.selectOne(query);
    }

    /**
     * 获取关注信息，与上面的不同是查询范围包括曾经关注过的记录
     *
     * @param userId
     * @param followUserId
     * @return
     */
    public UserRelationDO getUserRelationRecord(Long userId, Long followUserId) {
        LambdaQueryWrapper<UserRelationDO> query = Wrappers.lambdaQuery();
        query.eq(UserRelationDO::getUserId, userId)
                .eq(UserRelationDO::getFollowUserId, followUserId);
        return baseMapper.selectOne(query);
    }
}
