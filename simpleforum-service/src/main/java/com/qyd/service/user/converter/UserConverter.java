package com.qyd.service.user.converter;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.FollowStateEnum;
import com.qyd.api.model.enums.RoleEnum;
import com.qyd.api.model.enums.UserAIStatEnum;
import com.qyd.api.model.vo.user.UserInfoSaveReq;
import com.qyd.api.model.vo.user.UserRelationReq;
import com.qyd.api.model.vo.user.UserSaveReq;
import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.qyd.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.qyd.service.user.repository.entity.UserAiDO;
import com.qyd.service.user.repository.entity.UserDO;
import com.qyd.service.user.repository.entity.UserInfoDO;
import com.qyd.service.user.repository.entity.UserRelationDO;
import org.springframework.beans.BeanUtils;

/**
 * 用户转换
 *
 * @author 邱运铎
 * @date 2024-04-21 18:20
 */
public class UserConverter {

    public static UserDO toDO(UserSaveReq req) {
        if (req == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        userDO.setId(req.getUserId());
        userDO.setThirdAccountId(req.getThirdAccountId());
        userDO.setLoginType(req.getLoginType());
        return userDO;
    }

    public static UserInfoDO toDO(UserInfoSaveReq req) {
        if (req == null) {
            return null;
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUserId(req.getUserId());
        userInfoDO.setUserName(req.getUserName());
        userInfoDO.setPhoto(req.getPhoto());
        userInfoDO.setPosition(req.getPosition());
        userInfoDO.setCompany(req.getCompany());
        userInfoDO.setProfile(req.getProfile());
        return userInfoDO;
    }

    public static BaseUserInfoDTO toDTO(UserInfoDO info, UserAiDO userAiDO) {
        BaseUserInfoDTO user = toDTO(info);
        if(userAiDO != null) {
            user.setStarStatus(UserAIStatEnum.fromCode(userAiDO.getState()));
        }
        return user;
    }

    public static BaseUserInfoDTO toDTO(UserInfoDO info) {
        if (info == null) {
            return null;
        }
        BaseUserInfoDTO user = new BaseUserInfoDTO();
        // todo 知识点， bean属性拷贝的几种方式 1. get/set 2. 使用BeanUtil工具类（spring, cglib, apache, objectMapper） 3. 序列化等
        BeanUtils.copyProperties(info, user);
        // 设置用户最新登录地理位置
        user.setRegion(info.getIp().getLatestRegion());
        // 设置用户角色
        user.setRole(RoleEnum.role(info.getUserRole()));
        return user;
    }

    public static SimpleUserInfoDTO toSimpleInfo(UserInfoDO info) {
        return new SimpleUserInfoDTO().setUserId(info.getUserId())
                .setName(info.getUserName())
                .setAvatar(info.getPhoto())
                .setProfile(info.getProfile());
    }

    public static UserRelationDO toDO(UserRelationReq req) {
        if (req == null) {
            return null;
        }
        UserRelationDO relationDO = new UserRelationDO();
        relationDO.setUserId(req.getUserId());
        relationDO.setFollowUserId((ReqInfoContext.getReqInfo().getUserId()));
        relationDO.setFollowState(req.getFollowed() ? FollowStateEnum.FOLLOW.getCode() : FollowStateEnum.CANCEL_FOLLOW.getCode());
        return relationDO;
    }

    public static UserStatisticInfoDTO toUserHomeDTO(UserStatisticInfoDTO userHomeDTO, BaseUserInfoDTO baseUserInfoDTO) {
        if (baseUserInfoDTO == null) {
            return null;
        }
        BeanUtils.copyProperties(baseUserInfoDTO, userHomeDTO);
        return userHomeDTO;
    }
}
