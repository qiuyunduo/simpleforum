package com.qyd.service.user.service.whitelist;

import com.qyd.api.model.enums.UserAIStatEnum;
import com.qyd.api.model.enums.user.LoginTypeEnum;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.user.SearchStarUserReq;
import com.qyd.api.model.vo.user.StarUserPostReq;
import com.qyd.api.model.vo.user.dto.StarUserInfoDTO;
import com.qyd.service.user.converter.UserAiConverter;
import com.qyd.service.user.converter.UserStructMapper;
import com.qyd.service.user.repository.dao.UserAiDao;
import com.qyd.service.user.repository.dao.UserDao;
import com.qyd.service.user.repository.entity.UserAiDO;
import com.qyd.service.user.repository.entity.UserDO;
import com.qyd.service.user.repository.entity.UserInfoDO;
import com.qyd.service.user.repository.params.SearchStarWhiteParams;
import com.qyd.service.user.service.StarWhiteListService;
import com.qyd.service.user.service.help.UserPwdEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-13 16:00
 */
public class StarWhiteListServiceImpl implements StarWhiteListService {
    @Autowired
    private UserAiDao userAiDao;

    @Autowired
    private UserDao userDao;

    @Resource
    private UserPwdEncoder userPwdEncoder;

    @Override
    public PageVo<StarUserInfoDTO> getList(SearchStarUserReq req) {
        SearchStarWhiteParams params = UserStructMapper.INSTANCE.toSearchParams(req);
        // 查询知识星球用户
        List<StarUserInfoDTO> starUserInfoDTOS = userAiDao.listStarUsersByParams(params);
        Long totalCount = userAiDao.countStarUserByParams(params);
        return PageVo.build(starUserInfoDTOS, req.getPageSize(), req.getPageNumber(), totalCount);
    }

    @Override
    public void operate(Long id, UserAIStatEnum operate) {
        // 根据 id 获取用户信息
        UserAiDO userAiDo = userAiDao.getById(id);
        // 为空则抛出异常
        if (userAiDo == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, id, "用户不存在");
        }

        // 更新用户状态
        userAiDo.setState(operate.getCode());

        // 审核通过的时候调整用户的策略
        userAiDao.updateById(userAiDo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StarUserPostReq req) {
        // 根据 Id 获取用户信息
        UserAiDO userAiDO = userAiDao.getById(req.getId());
        // 为空则抛出异常
        if (userAiDO == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, req.getId(), "用户不存在");
        }

        // 星球编号不能重复
        UserAiDO userAiDOByStarNumber = userAiDao.getByStarNumber(req.getStarNumber());
        if (userAiDOByStarNumber != null && !userAiDOByStarNumber.getId().equals(req.getId())) {
            throw ExceptionUtil.of(StatusEnum.USER_STAR_REPEAT, req.getStarNumber(), "星球编号已存在");
        }

        // 用户登录名不能重复
        UserDO userDO = userDao.getUserByUserName(req.getUserCode());
        if (userDO != null && !userDO.getId().equals(userAiDO.getUserId())) {
            throw ExceptionUtil.of(StatusEnum.USER_LOGIN_NAME_REPEAT, req.getUserCode(), "用户登录名已存在2");
        }

        // 更新用户登录名
        userDO = new UserDO();
        userDO.setId(userAiDO.getUserId());
        userDO.setUserName(req.getUserCode());
        userDao.updateUser(userDO);

        // 更新用户昵称
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setId(userAiDO.getUserId());
        userInfoDO.setUserName(req.getName());
        userDao.updateById(userInfoDO);

        // 更新星球编号
        userAiDO.setStarNumber(req.getStarNumber());
        // 更新AI策略
        userAiDO.setStrategy(req.getStrategy());

        userAiDao.updateById(userAiDO);
    }

    @Override
    public void batchOperate(List<Long> ids, UserAIStatEnum operate) {
        // 批量更新用户状态
        userAiDao.batchUpdateState(ids, operate.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reset(Integer authorId) {
        // 根据id获取用户信息
        UserAiDO userAiDo = userAiDao.getById(authorId);
        // 为空则抛出异常
        if (userAiDo == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, authorId, "该星球用户不存在");
        }

        // 获取用户，看是微信还是用户名密码注册用户
        UserDO userDO = userDao.getUserByUserId(userAiDo.getUserId());
        if (userDO == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, userAiDo.getUserId(), "该用户不存在！");
        }

        // 不能直接删除，要初始化用户的 AI 信息
        UserAiDO initUserAiDO = UserAiConverter.initAi(userAiDo.getUserId());
        initUserAiDO.setId(userAiDo.getId());
        userAiDao.updateById(initUserAiDO);

        UserDO user = new UserDO();
        user.setId(userAiDo.getUserId());
        // 如果是微信注册用户
        if (LoginTypeEnum.WECHAT.getType() == userDO.getLoginType()) {
            // 用户登录名也重置
            user.setUserName("");
        }

        // 密码重置为
        user.setPassword(userPwdEncoder.encPwd("simpleforum"));
        userDao.saveUser(user);
    }
}
