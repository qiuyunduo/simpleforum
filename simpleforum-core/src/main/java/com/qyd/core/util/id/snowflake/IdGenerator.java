package com.qyd.core.util.id.snowflake;

/**
 * @author 邱运铎
 * @date 2024-05-13 19:10
 */
public interface IdGenerator {

    /**
     * 生成分布式id
     *
     * @return
     */
    Long nextId();
}
