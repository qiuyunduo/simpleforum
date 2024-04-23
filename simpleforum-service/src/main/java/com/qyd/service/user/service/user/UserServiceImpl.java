package com.qyd.service.user.service.user;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.article.dto.YearArticleDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.user.UserInfoSaveReq;
import com.qyd.api.model.vo.user.UserPwdLoginReq;
import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.qyd.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.qyd.core.util.IpUtil;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.statistics.service.CountService;
import com.qyd.service.user.converter.UserConverter;
import com.qyd.service.user.repository.dao.UserAiDao;
import com.qyd.service.user.repository.dao.UserDao;
import com.qyd.service.user.repository.dao.UserRelationDao;
import com.qyd.service.user.repository.entity.*;
import com.qyd.service.user.service.UserAiService;
import com.qyd.service.user.service.UserService;
import com.qyd.service.user.service.help.UserPwdEncoder;
import com.qyd.service.user.service.help.UserSessionHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户Service
 * @Autowired按 byType自动注入，
 * @Resource 默认按byName自动注入。
 * ps: @Qualifier 注解打标签的作用，用于解决多个同类型的bean，出现注入歧义
 *
 * @author 邱运铎
 * @date 2024-04-18 21:56
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserAiDao userAiDao;

    @Resource
    private UserRelationDao userRelationDao;

    @Autowired
    private CountService countService;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private UserSessionHelper userSessionHelper;

    @Autowired
    private UserPwdEncoder userPwdEncoder;

    @Autowired
    private UserAiService userAiService;


    @Override
    public UserDO getWxUser(String wxuuid) {
        return userDao.getByThirdAccountId(wxuuid);
    }

    @Override
    public List<SimpleUserInfoDTO> searchUser(String userName) {
        List<UserInfoDO> users = userDao.getByUserNameLike(userName);
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        return users.stream().map(s -> new SimpleUserInfoDTO()
                .setUserId(s.getUserId())
                .setName(s.getUserName())
                .setAvatar(s.getPhoto())
                .setProfile(s.getProfile())
                ).collect(Collectors.toList());
    }

    @Override
    public void saveUserInfo(UserInfoSaveReq req) {
        UserInfoDO userInfoDO = UserConverter.toDO(req);
        userDao.updateUserInfo(userInfoDO);
    }

    @Override
    public BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String session, String clientIp) {
        if (StringUtils.isBlank(session)) {
            return null;
        }

        Long userId = userSessionHelper.getUserIdBySession(session);
        if (userId == null) {
            return null;
        }

        // 查询用户信息，并更新最后一次使用IP
        UserInfoDO user = userDao.getByUserId(userId);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }

        IpInfo ip = user.getIp();
        if (clientIp != null && !Objects.equals(ip.getLatestIp(), clientIp)) {
            // ip不同，需要更新
            ip.setLatestIp(clientIp);
            ip.setLatestRegion(IpUtil.getLocationByIp(clientIp).toRegionStr());

            if (ip.getFirstIp() == null) {
                ip.setFirstIp(clientIp);
                ip.setFirstRegion(ip.getLatestRegion());
            }
            userDao.updateById(user);
        }

        // 查询 user_ai信息，标注用户是否为星球专属用户
        UserAiDO userAiDO = userAiDao.getByUserId(userId);
        return UserConverter.toDTO(user, userAiDO);
    }

    @Override
    public SimpleUserInfoDTO querySimpleUserInfo(Long userId) {
        UserInfoDO user = userDao.getByUserId(userId);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }
        return UserConverter.toSimpleInfo(user);
    }

    @Override
    public BaseUserInfoDTO queryBasicUserInfo(Long userId) {
        UserInfoDO user = userDao.getByUserId(userId);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }
        return UserConverter.toDTO(user);
    }

    @Override
    public List<SimpleUserInfoDTO> batchQuerySimpleUserInfo(Collection<Long> userIds) {
        List<UserInfoDO> users = userDao.getByUserIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userIds);
        }
        return users.stream().map(UserConverter::toSimpleInfo).collect(Collectors.toList());
    }

    @Override
    public List<BaseUserInfoDTO> batchQueryBasicUserInfo(Collection<Long> userIds) {
        List<UserInfoDO> users = userDao.getByUserIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userIds);
        }
        return users.stream().map(UserConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserStatisticInfoDTO queryUserInfoWithStatistic(Long userId) {
        // 从数据库中拿的用户基础信息
        BaseUserInfoDTO userInfoDTO = queryBasicUserInfo(userId);
        // 从redis拿的用户基础信息
        UserStatisticInfoDTO userHomeDTO = countService.queryUserStatisticInfo(userId);
        // 以数据库拿到的信息为准合并两者信息
        userHomeDTO = UserConverter.toUserHomeDTO(userHomeDTO, userInfoDTO);

        // 用户资料完整度
        int cnt = 0;
        if (StringUtils.isNotBlank(userHomeDTO.getCompany())) {
            ++cnt;
        }
        if (StringUtils.isNotBlank(userHomeDTO.getPosition())) {
            ++cnt;
        }
        if (StringUtils.isNotBlank(userHomeDTO.getProfile())) {
            ++cnt;
        }
        userHomeDTO.setInfoPercent(cnt * 100 / 3);

        // 是否关注
        Long followUserId = ReqInfoContext.getReqInfo().getUserId();
        if (followUserId != null) {
            UserRelationDO userRelationDO = userRelationDao.getUserRelationByUserId(userId, followUserId);
            userHomeDTO.setFollowed(userRelationDO == null ? Boolean.FALSE : Boolean.TRUE);
        } else {
            userHomeDTO.setFollowed(Boolean.FALSE);
        }

        // 加入天数
        int joinDayCount = (int) ((System.currentTimeMillis() - userHomeDTO.getCreateTime()
                .getTime()) / (1000 * 3600 * 24));
        userHomeDTO.setJoinDayCount(Math.max(joinDayCount, 1));

        // 创作历程
        List<YearArticleDTO> yearArticleDTOS = articleDao.listYearArticleByUserId(userId);
        userHomeDTO.setYearArticleList(yearArticleDTOS);
        return userHomeDTO;
    }

    @Override
    public Long getUserCount() {
        return this.userDao.getUserCount();
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public void bindUserInfo(UserPwdLoginReq loginReq) {
        // 0. 绑定用户名 & 密码 前置校验
        UserDO user = userDao.getUserByUserName(loginReq.getUsername());
        if (user == null) {
            // 用户名不存在， 则标识当前登录用户可以使用这个用户名
            user = new UserDO();
            user.setId(loginReq.getUserId());
        } else if (!Objects.equals(loginReq.getUserId(), user.getId())) {
            // 登录用户名已经存在
            throw ExceptionUtil.of(StatusEnum.USER_LOGIN_NAME_REPEAT,  loginReq.getUsername());
        }

        // 1. 更新用户名密码
        user.setUserName(loginReq.getUsername());
        user.setPassword(userPwdEncoder.encPwd(loginReq.getPassword()));
        userDao.saveUser(user);

        // 2. 更新ai相关信息
        userAiService.initOrUpdateAiInfo(loginReq);
    }
}
