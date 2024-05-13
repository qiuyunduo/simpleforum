package com.qyd.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.qyd.core.cache.RedisClient;
import com.qyd.core.config.ImageProperties;
import com.qyd.core.config.ProxyProperties;
import com.qyd.core.net.ProxyCenter;
import com.qyd.core.senstive.SensitiveProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author 邱运铎
 * @date 2024-04-22 21:19
 */
@Configuration
@ComponentScan(basePackages = "com.qyd.core")
@EnableConfigurationProperties({ImageProperties.class,
        ProxyProperties.class,
        SensitiveProperty.class})
public class ForumCoreAutoConfig {
    @Autowired
    private ProxyProperties proxyProperties;

    public ForumCoreAutoConfig(RedisTemplate<String, String> redisTemplate) {
        RedisClient.register(redisTemplate);
    }

    /**
     * 定义缓存管理器，配合Spring的@Cache 来使用
     *
     * @return
     */
    @Bean("caffeineCacheManager")
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .initialCapacity(100)
                .maximumSize(200)
        );
        return cacheManager;
    }

    @PostConstruct
    public void init() {
        // 这里借助手动解析配置信息，病实例化为java pojo对象，来实现代理池的初始化
        ProxyCenter.initProxyPool(proxyProperties.getProxy());
    }
}
