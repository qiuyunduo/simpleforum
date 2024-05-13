package com.qyd.core.senstive;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 敏感词相关配置，db配置表中的由埃及更高，支持动态刷新
 *
 * @author 邱运铎
 * @date 2024-05-09 21:14
 */
@Data
@ConfigurationProperties(prefix = SensitiveProperty.SENSITIVE_KEY_PREFIX)
public class SensitiveProperty {
    public static final String SENSITIVE_KEY_PREFIX = "forum.sensitive";

    /**
     * true 表示开启敏感词校验
     */
    private Boolean enable;

    /**
     * 自定义的敏感词
     */
    private List<String> deny;

    /**
     * 自定义的非敏感词
     */
    private List<String> allow;
}
