package com.qyd.service.user.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.qyd.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户个人信息表
 *
 * autoResultMap 必须存在，否则ip对象无法正确获取
 * 原因网上查了一下。大概是下面这个样子
 * 对于直接指定typeHandler,mybatis只支持你写在2个地方:
 * 1. 定义在resultMap里,只作用于select查询的返回结果封装
 * 2. 定义在insert和updatesql的#{property}里的property后面
 * 这里是属于第一种情况
 * @author 邱运铎
 * @date 2024-04-18 21:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_info", autoResultMap = true)
public class UserInfoDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户图像
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

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * 0： 普通用户
     * 1： 超级管理员
     */
    private Integer userRole;

    /**
     * ip信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private IpInfo ip;

    public IpInfo getIp() {
        if (ip == null) {
            ip = new IpInfo();
        }

        return ip;
    }

}
