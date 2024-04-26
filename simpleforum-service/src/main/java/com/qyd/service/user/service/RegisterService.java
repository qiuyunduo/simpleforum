package com.qyd.service.user.service;

import com.qyd.api.model.vo.user.UserPwdLoginReq;

/**
 * 用户注册服务
 *
 * @author 邱运铎
 * @date 2024-04-26 19:22
 */
public interface RegisterService {

    /**
     * 通过用户名/密码进行注册
     *
     * @param loginReq  携带用户名密码的请求对象
     * @return  userid
     */
    Long registerByUserNameAndPassword(UserPwdLoginReq loginReq);

    /**
     * 通过微信公众号进行注册
     *
     * @param thirdAccount  第三方账号
     * @return  userId
     */
    Long registerByWechat(String thirdAccount);
}
