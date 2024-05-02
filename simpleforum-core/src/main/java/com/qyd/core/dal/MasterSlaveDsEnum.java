package com.qyd.core.dal;

/**
 * 主从数据源的枚举
 * 用于表示当前数据源是主数据库还是从数据库
 *
 * @author 邱运铎
 * @date 2024-05-02 16:18
 */
public enum MasterSlaveDsEnum implements DS {

    /**
     * master 主数据库的数据源
     */
    MASTER,

    /**
     * slave 从数据库的数据源
     */
    SLAVE,
    ;
}
