package com.qyd.core.util.id;

import com.qyd.core.async.AsyncUtil;
import com.qyd.core.util.CompressUtil;
import com.qyd.core.util.id.snowflake.PaiSnowflakeIdGenerator;
import com.qyd.core.util.id.snowflake.SnowflakeProducer;

/**
 * @author 邱运铎
 * @date 2024-05-13 19:04
 */
public class IdUtil {
    /**
     * 默认的id生成器
     */
    public static SnowflakeProducer DEFAULT_ID_PRODUCER = new SnowflakeProducer(new PaiSnowflakeIdGenerator());

    /**
     * 生成全局id
     *
     * @return
     */
    public static Long genId() {
        return DEFAULT_ID_PRODUCER.genId();
    }

    /**
     * 生成字符串格式全局iD
     *
     * @return
     */
    public static String genStrId() {
        return CompressUtil.int2Str(genId());
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.genStrId());
        Long id = IdUtil.genId();
        System.out.println(id + " = " + CompressUtil.int2Str(id));
        System.out.println(IdUtil.genId() + "->" + IdUtil.genStrId());
        AsyncUtil.sleep(2000);
        System.out.println(IdUtil.genId() + "->" + IdUtil.genStrId());

        System.out.println("---------");
        SnowflakeProducer producer = new SnowflakeProducer(new PaiSnowflakeIdGenerator());
        id = producer.genId();
        System.out.println("id: " + id + " -> " + CompressUtil.int2Str(id));
        id = producer.genId();
        System.out.println("id: " + id + " -> " + CompressUtil.int2Str(id));
    }
}
