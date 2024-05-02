package com.qyd.web.front.login.pwd;

import com.qyd.service.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
