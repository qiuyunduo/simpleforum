package com.qyd.core.dal;


import com.github.hui.quick.plugin.qrcode.util.ClassUtils;

/**
 * @author 邱运铎
 * @date 2024-05-02 17:56
 */
public class DruidCheckUtil {

    /**
     * 判断是否包含druid相关的数据包
     *
     * @return
     */
    public static boolean hasDruidPkg() {
        return ClassUtils.isPresent("com.alibaba.druid.pool.DruidDataSource", DataSourceConfig.class.getClassLoader());
    }
}
