package com.qyd.api.model.vo.user;

import lombok.Data;

/**
 * 保存用户信息时的入参
 *
 * @author 邱运铎
 * @date 2024-04-18 21:28
 */
@Data
public class UserInfoSaveReq {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String photo;

    /**
     * 职位
     */
    private String position;

    /**
     * 公司
     */
    private String company;

    /**
     * 个人简介
     */
    private String profile;
}
