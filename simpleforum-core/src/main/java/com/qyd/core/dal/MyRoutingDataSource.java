package com.qyd.core.dal;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.Nullable;

/**
 * 注解@Nullable 标识方法返回值可以为null,
 * 或者可以认为是对开发者说改方法返回值存在null的情况请注意处理
 *
 * AbstractRoutingDataSource Spring中提供的类，
 * 根据给定的数据源获取对应的connection
 *
 * @author 邱运铎
 * @date 2024-05-02 16:37
 */
public class MyRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    @Nullable
    protected Object determineCurrentLookupKey() {
        return DsContextHolder.get();
    }
}
