package com.qyd.core.dal;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 多数据源的配置加载
 *
 * @author 邱运铎
 * @date 2024-05-02 17:05
 */
@Data
@ConfigurationProperties(prefix = DsProperties.DS_PREFIX)
public class DsProperties {
    public static final String DS_PREFIX = "spring.dynamic";

    private String primary;

    private Map<String, DataSourceProperties> datasource;
}
