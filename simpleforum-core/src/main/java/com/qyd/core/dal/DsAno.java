package com.qyd.core.dal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源标识注解，用于指定使用该注解的方法在执行sql时选择的数据源
 *
 * @author 邱运铎
 * @date 2024-05-02 16:16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DsAno {

    /**
     * 使用的数据源，默认数主数据库数据源
     *
     * @return
     */
    MasterSlaveDsEnum value() default MasterSlaveDsEnum.MASTER;

    /**
     * 使用的数据源，如果窜在，则优先使用其替换默认的value
     *
     * @return
     */
    String ds() default "";

}
