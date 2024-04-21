package com.qyd.api.model.vo.user.dto;

import com.qyd.api.model.entity.BaseDTO;
import com.qyd.api.model.enums.UserAIStatEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author 邱运铎
 * @date 2024-04-11 0:55
 */
@Data
@ApiModel("用户基础实体对象")
@Accessors(chain = true)
public class BaseUserInfoDTO extends BaseDTO {

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id", required = true)
    private Long userId;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名",required = true)
    private String userName;

    /**
     * 用户角色 admin，normal
     */
    @ApiModelProperty(value = "用户角色", example = "ADMIN | NORMAL")
    private String role;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String photo;

    /**
     * 个人简介
     */
    @ApiModelProperty(value = "用户简介")
    private String profile;

    /**
     * 职位
     */
    @ApiModelProperty(value = "个人职位")
    private String position;

    /**
     * 公司
     */
    @ApiModelProperty(value = "公司")
    private String company;

    /**
     * 扩展字段
     */
    @ApiModelProperty(hidden = true)
    private String extend;

    /**
     * 是否删除
     */
    @ApiModelProperty(hidden = true, value = "用户是否被删除")
    private Integer deleted;

    /**
     * 用户最后登录区域
     */
    @ApiModelProperty(value = "用户最后登录的地理位置", example = "湖北·武汉")
    private String region;

    /**
     * 用户星球状态
     */
    private UserAIStatEnum starStatus;
}
