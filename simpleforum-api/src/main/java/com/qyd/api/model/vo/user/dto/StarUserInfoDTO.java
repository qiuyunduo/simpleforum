package com.qyd.api.model.vo.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 星球用户基本信息
 *
 * @author 邱运铎
 * @date 2024-04-19 9:50
 */
@Data
@Accessors(chain = true)
public class StarUserInfoDTO implements Serializable {
    private static final long serialVersionUID = 4802653694786272120L;

    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    // 这个是 userinfo 表中的 username
    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("用户头像")
    private String avatar;

    // 这个是 user 表中的 username
    @ApiModelProperty("用户编号")
    private String userCode;

    @ApiModelProperty("星球编号")
    private String starNumber;

    @ApiModelProperty("邀请码")
    private String inviteCode;

    @ApiModelProperty("邀请人数")
    private Integer inviteNum;

    @ApiModelProperty("装填")
    private Integer state;

    @ApiModelProperty("登录类型")
    private Integer loginType;

    @ApiModelProperty("AI策略")
    private Integer strategy;
}
