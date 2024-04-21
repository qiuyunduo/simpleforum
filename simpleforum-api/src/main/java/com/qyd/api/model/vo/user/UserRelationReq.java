package com.qyd.api.model.vo.user;

import lombok.Data;

/**
 * 用户关系入参
 *
 * @author 邱运铎
 * @date 2024-04-21 19:59
 */
@Data
public class UserRelationReq {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 粉丝用户ID
     */
    private Long followUserId;

    /**
     * 是否关注当前用户
     */
    private Boolean followed;
}
