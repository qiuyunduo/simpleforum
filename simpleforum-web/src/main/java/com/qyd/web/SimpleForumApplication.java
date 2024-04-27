package com.qyd.web;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qyd.core.util.SpringUtil;
import com.qyd.web.config.GlobalViewConfig;
import com.qyd.web.global.ForumExceptionHandler;
import com.qyd.web.hook.interceptor.GlobalViewInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 使用@ServletComponentScan注解后，Servlet（控制器）、Filter（过滤器）、Listener（监听器）
 * 可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册到Spring容器中，无需其他代码。
 *
 * @author 邱运铎
 * @date 2024-04-08 17:24
 */
@Slf4j
@EnableAsync
@EnableScheduling
@EnableCaching
@ServletComponentScan
@SpringBootApplication
public class SimpleForumApplication implements WebMvcConfigurer, ApplicationRunner {

    /**
     * 这里value注解中port:8080,应该是表示如果配置文件中没有配置server.port
     * 则使用：后面的值，避免出现配置文件中没有配置server.port导致找不到该配置报错
     * 因为server.port默认是8080，不进行配置也不会有问题
     */
    @Value("${server.port:8080}")
    private Integer webPort;

    @Autowired
    private GlobalViewInterceptor globalViewInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalViewInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, new ForumExceptionHandler());
    }

    public static void main(String[] args) {
        SpringApplication.run(SimpleForumApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 设置类型转换， 主要用于mybatis 读取varchar/json类型数据，并写入到json格式的实体Entity中
        JacksonTypeHandler.setObjectMapper(new ObjectMapper());
        // 应用启动之后执行
        GlobalViewConfig config = SpringUtil.getBean(GlobalViewConfig.class);
        if (webPort != null) {
            config.setHost("http://127.0.0.1:" + webPort);
        }
        log.info("启动成功，点击进入首页： {}", config.getHost());
    }
}
