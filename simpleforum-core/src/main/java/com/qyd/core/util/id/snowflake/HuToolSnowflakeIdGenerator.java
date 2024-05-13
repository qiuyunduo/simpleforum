package com.qyd.core.util.id.snowflake;

import cn.hutool.core.lang.Snowflake;

import java.util.Date;

/**
 * @author 邱运铎
 * @date 2024-05-13 20:34
 */
public class HuToolSnowflakeIdGenerator implements IdGenerator {
    private static final Date EPOC = new Date(2023, 1, 1);
    private Snowflake snowflake;

    public HuToolSnowflakeIdGenerator(int workId, int dataCenter) {
        snowflake = new Snowflake(EPOC, workId, dataCenter, false);
    }
    @Override
    public Long nextId() {
        return snowflake.nextId();
    }

    public static void main(String[] args) {
        HuToolSnowflakeIdGenerator generator = new HuToolSnowflakeIdGenerator(1, 1);
        System.out.println(generator.nextId());
        System.out.println(generator.nextId());
        System.out.println(generator.nextId());
        System.out.println(generator.nextId());
        System.out.println(generator.nextId());
        System.out.println(generator.nextId());
        System.out.println(generator.nextId());
    }
}
