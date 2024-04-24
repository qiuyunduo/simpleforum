package com.qyd.web.global;

import com.qyd.service.user.service.UserService;
import com.qyd.web.config.GlobalViewConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 邱运铎
 * @date 2024-04-24 17:57
 */
@Slf4j
@Service
public class GlobalInitService {
    @Value("${env.name}")
    private String env;

    @Autowired
    private UserService userService;

    @Resource
    private GlobalViewConfig globalViewConfig;






}
