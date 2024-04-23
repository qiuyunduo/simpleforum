package com.qyd.core.util;

import cn.hutool.core.lang.Assert;

/**
 * 单例形式存储当前环境
 * 主要是判断当前是否为生产环境
 *
 * @author 邱运铎
 * @date 2024-04-23 22:12
 */
public class EnvUtil {
    private static volatile EnvEnum env;

    /**
     * 当前环境状态枚举类
     */
    public enum EnvEnum {
        DEV("dev", false),
        TEST("test", false),
        PRE("pre", false),
        PROD("prod", true);

        private String env;
        private boolean prod;

        EnvEnum(String env, boolean prod) {
            this.env = env;
            this.prod = prod;
        }

        public static EnvEnum nameOf(String name) {
            for (EnvEnum env : values()) {
                if (env.env.equalsIgnoreCase(name)) {
                    return env;
                }
            }
            return null;
        }
    }

    /**
     * 当前环境是否为生产环境
     *
     * @return
     */
    public static boolean isPro() {
        return getEnv().prod;
    }

    /**
     * 双检锁单例， 获取当前环境
     *
     * @return
     */
    public static EnvEnum getEnv() {
        if (env == null) {
            synchronized (EnvUtil.class) {
                if (env == null) {
                    env = EnvEnum.nameOf(SpringUtil.getConfig("env.name"));
                }
            }
        }
        Assert.isTrue(env != null, "env.name环境配置必须存在！");
        return env;
    }
}
