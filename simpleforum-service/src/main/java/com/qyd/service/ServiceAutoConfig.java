package com.qyd.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 邱运铎
 * @date 2024-04-08 18:58
 */
@Configuration
@ComponentScan("com.qyd.service")
@MapperScan(basePackages = {
        "com.qyd.service.article.repository.mapper",
        "com.qyd.service.comment.repository.mapper",
        "com.qyd.service.config.repository.mapper",
        "com.qyd.service.user.repository.mapper",
        "com.qyd.service.statistic.repository.mapper"})
public class ServiceAutoConfig {
}
