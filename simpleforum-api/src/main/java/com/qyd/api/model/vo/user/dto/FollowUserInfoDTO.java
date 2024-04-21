package com.qyd.api.model.vo.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 关注者用户信息
 *
 * @author 邱运铎
 * @date 2024-04-20 18:34
 */
@Data
public class FollowUserInfoDTO implements Serializable {

    /**
     * 登录用户和这个用户之间的关联关系id
     */
    private Long relationId;

    /**
     * true 表示当前登录用户关注了这个用户
     * false 表示当前登录用户没有关注这个用户
     */
    private Boolean followed;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String avatar;
}
