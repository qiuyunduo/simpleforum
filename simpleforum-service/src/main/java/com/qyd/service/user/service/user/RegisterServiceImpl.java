package com.qyd.service.user.service.user;

import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.enums.user.LoginTypeEnum;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.notify.NotifyMsgEvent;
import com.qyd.api.model.vo.user.UserPwdLoginReq;
import com.qyd.core.util.SpringUtil;
import com.qyd.core.util.TransactionUtil;
import com.qyd.service.user.converter.UserAiConverter;
import com.qyd.service.user.repository.dao.UserAiDao;
import com.qyd.service.user.repository.dao.UserDao;
import com.qyd.service.user.repository.entity.UserAiDO;
import com.qyd.service.user.repository.entity.UserDO;
import com.qyd.service.user.repository.entity.UserInfoDO;
import com.qyd.service.user.service.RegisterService;
import com.qyd.service.user.service.help.UserPwdEncoder;
import com.qyd.service.user.service.help.UserRandomGenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户注册服务
 *
 * @author 邱运铎
 * @date 2024-04-26 19:25
 */
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserPwdEncoder userPwdEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAiDao userAiDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerByUserNameAndPassword(UserPwdLoginReq loginReq) {
        // 1. 判断用户名是否准确
        UserDO user = userDao.getUserByUserName(loginReq.getUsername());
        if (user != null) {
            throw ExceptionUtil.of(StatusEnum.USER_LOGIN_NAME_REPEAT, loginReq.getUsername());
        }

        // 2. 保存用户登录信息
        user = new UserDO();
        user.setUserName(loginReq.getUsername());
        user.setPassword(userPwdEncoder.encPwd(loginReq.getPassword()));
        user.setThirdAccountId("");
        // 用户名密码注册
        user.setLoginType(LoginTypeEnum.USER_PWD.getType());
        // 保存信息用户基本信息到user表
        userDao.saveUser(user);

        // 3. 保存用户信息
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setUserId(user.getId());
        userInfo.setUserName(user.getUserName());
        userInfo.setPhoto(UserRandomGenHelper.genAvatar());
        // 保存用户的详情信息到 user_info 表
        userDao.save(userInfo);

        // 4. 保存ai交互信息
        UserAiDO userAiDO = UserAiConverter.initAi(user.getId(), loginReq.getStarNumber());
        userAiDao.saveOrUpdateAiBindInfo(userAiDO, loginReq.getInvitationCode());
        processAfterUserRegister(user.getId());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerByWechat(String thirdAccount) {
        // 用户不存在，则需要注册
        // 1. 保存用户登录信息
        UserDO user = new UserDO();
        user.setThirdAccountId(thirdAccount);
        user.setLoginType(LoginTypeEnum.WECHAT.getType());
        userDao.saveUser(user);

        // 2. 初始化用户信息，随机生成用户昵称 + 头像
        UserInfoDO userinfo = new UserInfoDO();
        userinfo.setUserId(user.getId());
        userinfo.setUserName(UserRandomGenHelper.genNickName());
        userinfo.setPhoto(UserRandomGenHelper.genAvatar());
        userDao.save(userinfo);

        // 3. 保存ai交互信息
        UserAiDO userAiDO = UserAiConverter.initAi(user.getId());
        userAiDao.saveOrUpdateAiBindInfo(userAiDO, null);
        processAfterUserRegister(user.getId());
        return user.getId();
    }

    /**
     * 用户注册完毕之后触发的动作
     *
     * @param userId
     */
    private void processAfterUserRegister(Long userId) {
        TransactionUtil.registerAfterCommitImmediateRun(new Runnable() {
            @Override
            public void run() {
                // 用户注册事件
                SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.REGISTER, userId));
            }
        });
    }
}
