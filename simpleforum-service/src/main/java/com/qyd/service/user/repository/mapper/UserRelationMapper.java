package com.qyd.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.user.dto.FollowUserInfoDTO;
import com.qyd.service.user.repository.entity.UserRelationDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关系mapper接口
 *
 * @author 邱运铎
 * @date 2024-04-20 18:31
 */
public interface UserRelationMapper extends BaseMapper<UserRelationDO> {
    /**
     * 查询用户的关注
     *
     * @param followUserId
     * @param pageParam
     * @return
     */
    List<FollowUserInfoDTO> queryUserFollowList(@Param("followUserId") Long followUserId,
                                                @Param("pageParam")PageParam pageParam);

    /**
     * 查询用户的粉丝
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<FollowUserInfoDTO> queryUserFansList(@Param("userId") Long userId,
                                              @Param("pageParam") PageParam pageParam);
}
