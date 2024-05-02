package com.qyd.core.dal;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 在配置了多数据源时，该配置启用
 * 注解@ConditionalOnProperty 表示存在参数中指定的配置才会生效
 * 当注解参数中的配置不存在的时候，Spring上下文将不会对该bean进行加载
 *
 * @author 邱运铎
 * @date 2024-05-02 17:08
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.dynamic", name = "primary")
@EnableConfigurationProperties(DsProperties.class)
public class DataSourceConfig {

    private Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
        log.info("动态数据源初始化!");
    }

    @Bean
    public DsAspect dsAspect() {
        return new DsAspect();
    }

    @Bean
    public SqlStateInterceptor sqlStateInterceptor() {
        return new SqlStateInterceptor();
    }

    /**
     * 配置动态数据源
     * 整合主从数据库
     *
     * @param dsProperties
     * @return
     */
    @Bean
    @Primary
    public DataSource dataSource(DsProperties dsProperties) {
        Map<Object, Object> targetDataSources = Maps.newHashMapWithExpectedSize(dsProperties.getDatasource().size());
        dsProperties.getDatasource().forEach((k, v) -> targetDataSources.put(k.toUpperCase(), initDataSource(k, v)));

        if (CollectionUtils.isEmpty(targetDataSources)) {
            throw new IllegalStateException("多数据源配置，请以 spring.dynamic 开头");
        }

        MyRoutingDataSource myRoutingDataSource = new MyRoutingDataSource();
        Object key = dsProperties.getPrimary().toUpperCase();
        if (!targetDataSources.containsKey(key)) {
            if (targetDataSources.containsKey(MasterSlaveDsEnum.MASTER.name())) {
                // 如果没有配置primary对应的数据源时，但存在MASTER数据源， 则将Master作为默认的数据源
                key = MasterSlaveDsEnum.MASTER.name();
            } else {
                key = targetDataSources.keySet().iterator().next();
            }
        }

        log.info("动态数据源，默认启用为：" + key);
        myRoutingDataSource.setDefaultTargetDataSource(targetDataSources.get(key));
        myRoutingDataSource.setTargetDataSources(targetDataSources);
        return myRoutingDataSource;
    }

    public DataSource initDataSource(String prefix, DataSourceProperties properties) {
        if (!DruidCheckUtil.hasDruidPkg()) {
            log.info("实例化HikariDataSource: {}", prefix);
            return properties.initializeDataSourceBuilder().build();
        }

        if (properties.getType() == null || !properties.getType().isAssignableFrom(DruidDataSource.class)) {
            log.info("实例化HikariDataSource: {}", prefix);
            return properties.initializeDataSourceBuilder().build();
        }

        log.info("实例化DruidDataSource: {}", prefix);
        // fixme 知识点： 手动配置赋值到实例中的方式
        return Binder.get(environment).bindOrCreate(DsProperties.DS_PREFIX + ".datasource." + prefix, DruidDataSource.class);
    }

    /**
     * 下面ConditionOnExpression表示在项目中引入了Druid相关包才创建该bean
     * 对于使用DruidDataSource的情况，配置一个相应的资源监控配置
     *
     * 注解ConditionOnExpression中T(),T 是type的缩写，在SPel表达式中
     * 表示获取对应路径类的class对象
     *
     * <?> 一个占位符，可以是任何类型 是“不确定的类型” 只能接收不能修改
     * <T>  泛型表示符， 是一个“确定的”类型 可以进行操作
     *
     *
     * @return
     */
    @Bean
    @ConditionalOnExpression(value = "T(com.qyd.core.dal.DruidCheckUtil).hasDruidPkg()")
    public ServletRegistrationBean<?> druidStatViewServlet() {
        // 先配置管理后台的Servlet, 访问入口为/druid/
        ServletRegistrationBean<?> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        // IP白名单（没有配置或者为空，则允许所有访问）
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        // IP黑名单（存在共同的部分，deny（黑名单）优先于allow(白名单)）
        servletRegistrationBean.addInitParameter("deny", "");
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        log.info("开启druid数据源监控面板");
        return servletRegistrationBean;

    }

}
