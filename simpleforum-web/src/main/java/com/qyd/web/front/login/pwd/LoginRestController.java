package com.qyd.web.front.login.pwd;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.user.UserPwdLoginReq;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.core.util.SessionUtil;
import com.qyd.service.user.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 用户名 密码方式的的登录/登出的入口
 *
 * @author 邱运铎
 * @date 2024-05-02 0:40
 */
@RestController
@RequestMapping
public class LoginRestController {
    @Autowired
    private LoginService loginService;

    /**
     * 用户名和密码登录
     *
     * @return
     */
    @PostMapping("/login/username")
    public ResVo<Boolean> login(@RequestParam(name = "username") String username,
                                @RequestParam(name = "password") String password,
                                HttpServletResponse response) {
        String session = loginService.loginByUserPwd(username, password);
        if (StringUtils.isNotBlank(session)) {
            // 登陆成功，cookie中写入用户的登录信息用于身份识别
            response.addCookie(SessionUtil.newCookie(LoginService.SESSION_KEY, session));
            return ResVo.ok(true);
        } else {
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "用户名和密码登录异常, 请稍后重试");
        }
    }

    /**
     * 绑定星球账号并进行注册并登录
     */
    @PostMapping("/login/register")
    public ResVo<Boolean> register(UserPwdLoginReq loginReq,
                                   HttpServletResponse response) {
        String session = loginService.registerByUserPwd(loginReq);
        if (StringUtils.isNotBlank(session)) {
            // cookie中写入用户登录信息，用于身份识别
            response.addCookie(SessionUtil.newCookie(LoginService.SESSION_KEY, session));
            return ResVo.ok(true);
        } else {
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "用户名和密码登录异常，请稍后重试");
        }
    }

    @Permission(role = UserRole.LOGIN)
    @RequestMapping("/logout")
    public ResVo<Boolean> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 释放会话
        request.getSession().invalidate();
        Optional.ofNullable(ReqInfoContext.getReqInfo()).ifPresent(reqInfo -> loginService.logout(reqInfo.getSession()));
        // 移除Cookie, 这里移除看代码逻辑是指将对应的cookie的到期时间设置为0
        response.addCookie(SessionUtil.delCookie(LoginService.SESSION_KEY));
        // 重定向到当前页面
        String referer = request.getHeader("Referer");
        if (StringUtils.isBlank(referer)) {
            referer = "/";
        }
        response.sendRedirect(referer);
        return ResVo.ok(true);
    }
}
