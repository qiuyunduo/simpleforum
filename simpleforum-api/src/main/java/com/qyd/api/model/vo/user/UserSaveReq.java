package com.qyd.api.model.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户信息登录入参
 *
 * @author 邱运铎
 * @date 2024-04-21 18:22
 */
@Data
@Accessors(chain = true)
public class UserSaveReq {
    /**
     * 主键ID
     */
    private Long userId;

    /**
     * 第三方用户ID
     */
    private String thirdAccountId;

    /**
     * 登录方式： 0-微信登录， 1-账号密码登录
     */
    private Integer loginType;
}
