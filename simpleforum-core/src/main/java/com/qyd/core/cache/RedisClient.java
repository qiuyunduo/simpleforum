package com.qyd.core.cache;


import com.google.common.collect.Maps;
import com.qyd.core.util.JsonUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * redis封装客户端
 *
 *  execute() 方法是 RedisTemplate 类提供的一个通用方法，用于执行 Redis 命令
 *  opsForValue() 方法是 RedisTemplate 类提供的一种便捷方法，用于执行与字符串类型相关的 Redis 命令
 *  execute 方法提供了更广泛的灵活性，可以执行任何类型的 Redis 命令，
 *  而不仅仅局限于字符串操作。这意味着开发者可以根据实际需求执行各种复杂的 Redis 命令，
 *  而不受 opsForValue 提供的方法的限制。
 *
 * @author 邱运铎
 * @date 2024-04-20 20:37
 */
public class RedisClient {
    private static final Charset CODE = StandardCharsets.UTF_8;
    private static final String KEY_PREFIX = "pai_";
    private static RedisTemplate<String, String> template;

    public static void register(RedisTemplate<String, String> template) {
        RedisClient.template = template;
    }

    public static void nullCheck(Object...args) {
        for (Object obj : args) {
            if (obj == null) {
                throw new IllegalArgumentException("redis argument can not null");
            }
        }
    }

    /**
     * 网站的缓存值序列化处理
     *
     * @param val
     * @return
     * @param <T>
     */
    public static <T> byte[] valBytes(T val) {
        if (val instanceof String) {
            return ((String) val).getBytes(CODE);
        } else {
            return JsonUtil.toStr(val).getBytes(CODE);
        }
    }

    /**
     * 生成网站的缓存key
     *
     * @param key
     * @return
     */
    public static byte[] keyBytes(String key) {
        nullCheck(key);
        key = KEY_PREFIX + key;
        return key.getBytes(CODE);
    }

    public static byte[][] keyBytes(List<String> keys) {
        byte[][] bytes = new byte[keys.size()][];
        int index = 0;
        for (String key :keys) {
            bytes[index++] = keyBytes(key);
        }
        return bytes;
    }

    /**
     * 查询缓存
     *
     * @param key
     * @return
     */
    public static String getStr(String key) {
        return template.execute((RedisCallback<String>) con -> {
            byte[] val = con.get(keyBytes(key));
            return val == null ? null : new String(val);
        });
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    public static void setStr(String key, String value) {
        template.execute((RedisCallback<Void>) con -> {
           con.set(keyBytes(key), valBytes(value));
           return null;
        });
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public static void del(String key) {
        template.execute((RedisCallback<Long>) con -> con.del(keyBytes(key)));
    }

    /**
     * 设置缓存有效期
     *
     * @param key
     * @param expire 有效期 单位为 s秒
     */
    public static void expire(String key, Long expire) {
        template.execute((RedisCallback<Void>) con -> {
            con.expire(keyBytes(key), expire);
            return null;
        });
    }

    /**
     * 带过期时间的缓存写入
     *
     * @param key
     * @param value
     * @param expire    单位为 s 秒
     * @return
     */
    public static Boolean setStrWithExpire(String key, String value, Long expire) {
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.setEx(keyBytes(key), expire, valBytes(value));
            }
        });
    }

    public static <T> Map<String, T> hGetAll(String key, Class<T> clz) {
        Map<byte[], byte[]> records = template.execute((RedisCallback<Map<byte[], byte[]>>) con -> con.hGetAll(keyBytes(key)));
        if (records == null) {
            return Collections.emptyMap();
        }

        Map<String, T> result = Maps.newHashMapWithExpectedSize(records.size());
        for (Map.Entry<byte[], byte[]> entry : records.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }

            result.put(new String(entry.getKey()), toObj(entry.getValue(), clz));
        }
        return result;
    }

    public static <T> T hGet(String key, String field, Class<T> clz) {
        return template.execute((RedisCallback<T>) con -> {
            byte[] records = con.hGet(keyBytes(key), valBytes(field));
            if (records == null) {
                return null;
            }

            return toObj(records, clz);
        });
    }

    /**
     * 指定步长自增
     *
     * @param key
     * @param field
     * @param cnt
     * @return
     */
    public static Long hIncr(String key, String field, Integer cnt) {
        return template.execute((RedisCallback<Long>) con -> con.hIncrBy(keyBytes(key), valBytes(field), cnt));
    }

    public static <T> Boolean hDel(String key, String filed) {
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.hDel(keyBytes(key), valBytes(filed)) > 0;
            }
        });
    }

    public static <T> Boolean hSet(String key, String field, T ans) {
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.hSet(keyBytes(key), valBytes(field), valBytes(ans));
            }
        });
    }

    public static <T> void hMSet(String key, Map<String, T> fields) {
        Map<byte[], byte[]> val = Maps.newHashMapWithExpectedSize(fields.size());
        for (Map.Entry<String, T> entry : fields.entrySet()) {
            val.put(keyBytes(entry.getKey()), valBytes(entry.getValue()));
        }
        template.execute((RedisCallback<Object>) con -> {
            con.hMSet(keyBytes(key), val);
            return null;
        });
    }

    public static <T> Map<String, T> hMGet(String key, final List<String> fields, Class<T> clz) {
        return template.execute(new RedisCallback<Map<String, T>>() {
            @Override
            public Map<String, T> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[][] f = new byte[fields.size()][];
                IntStream.range(0, fields.size()).forEach(i -> f[i] = valBytes(fields.get(i)));
                List<byte[]> ans = redisConnection.hMGet(keyBytes(key), f);
                Map<String, T> result = Maps.newHashMapWithExpectedSize(fields.size());
                IntStream.range(0, fields.size()).forEach(i -> {
                    result.put(fields.get(i), toObj(ans.get(i), clz));
                });
                return result;
            }
        });
    }

    /**
     * 判断value是否在set中
     *
     * @param key
     * @param value
     * @return
     */
    public static <T> Boolean sIsMember(String key, T value) {
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.sIsMember(keyBytes(key), valBytes(value));
            }
        });
    }

    /**
     * 获取set中的所有内容
     *
     * @param key
     * @param clz
     * @return
     */
    public static <T> Set<T> sGetAll(String key, Class<T> clz) {
        return template.execute((RedisCallback<Set<T>>) con -> {
            Set<byte[]> set = con.sMembers(keyBytes(key));
            if (CollectionUtils.isEmpty(set)) {
                return Collections.emptySet();
            }
            return set.stream().map(s -> toObj(s, clz)).collect(Collectors.toSet());
        });
    }

    /**
     * 往set中添加内容
     *
     * @param key
     * @param val
     * @return
     * @param <T>
     */
    public static <T> Boolean sPut(String key, T val) {
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Long res = redisConnection.sAdd(keyBytes(key), valBytes(val));
                return res != null && res > 0;
            }
        });
    }

    /**
     * 移除set中的内容
     *
     * @param key
     * @param val
     */
    public static <T> void sDel(String key, T val) {
        template.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.sRem(keyBytes(key), valBytes(val));
                return null;
            }
        });
    }

    /**
     * 分数更新
     *
     * @param key
     * @param value
     * @param score
     */
    public static Double zIncrBy(String key, String value, Integer score) {
        return template.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.zIncrBy(keyBytes(key), score, valBytes(value));
            }
        });
    }

    public static ImmutablePair<Integer, Double> zRankInfo(String key, String value) {
        double score = zScore(key, value);
        int rank = zRank(key, value);
        return ImmutablePair.of(rank, score);
    }

    /**
     * 获取分数
     *
     * @param key
     * @param value
     * @return
     */
    public static Double zScore(String key, String value) {
        return template.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.zScore(keyBytes(key), valBytes(value));
            }
        });
    }

    public static Integer zRank(String key, String value) {
        return template.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.zRank(keyBytes(key), valBytes(value)).intValue();
            }
        });
    }

    /**
     * 找出排名靠前的n个
     *
     * @param key
     * @param n
     * @return
     */
    public static List<ImmutablePair<String, Double>> zTopNScore(String key, int n) {
        return template.execute(new RedisCallback<List<ImmutablePair<String, Double>>>() {
            @Override
            public List<ImmutablePair<String, Double>> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Set<RedisZSetCommands.Tuple> set = redisConnection.zRangeWithScores(keyBytes(key), -n, -1);
                if (set == null) {
                    return Collections.emptyList();
                }
                return set.stream()
                        .map(tuple -> ImmutablePair.of(toObj(tuple.getValue(), String.class), tuple.getScore()))
                        .sorted((o1, o2) -> Double.compare(o2.getRight(), o2.getRight()))
                        .collect(Collectors.toList());
            }
        });
    }

    /**
     * 往list最左边添加内容
     *
     * @param key
     * @param val
     * @return
     */
    public static <T> Long lPush(String key, T val) {
        return template.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.lPush(keyBytes(key), valBytes(val));
            }
        });
    }

    /**
     * 往list最右边添加内容
     *
     * @param key
     * @param val
     * @return
     */
    public static <T> Long rPush(String key, T val) {
        return template.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.rPush(keyBytes(key), valBytes(val));
            }
        });
    }

    public static <T> List<T> lRange(String key, int start, int size, Class<T> clz) {
        return template.execute(new RedisCallback<List<T>>() {
            @Override
            public List<T> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                List<byte[]> list = redisConnection.lRange(keyBytes(key), start, size);
                if (CollectionUtils.isEmpty(list)) {
                    return Collections.emptyList();
                }
                return list.stream().map(k -> toObj(k, clz)).collect(Collectors.toList());
            }
        });
    }

    /**
     * 对list修剪，只保留从start开始后size的数据
     *
     * @param key
     * @param start
     * @param size
     */
    public static void lTrim(String key, int start, int size) {
        template.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.lTrim(keyBytes(key), start, size);
                return null;
            }
        });
    }

    private static <T> T toObj(byte[] ans, Class<T> clz) {
        if (ans == null) {
            return null;
        }

        if (clz == String.class) {
            return (T) new String(ans, CODE);
        }

        return JsonUtil.toObj(new String(ans, CODE), clz);
    }

    public static PipelineAction pipelineAction() {
        return new PipelineAction();
    }

    /**
     * redis 管道执行的封装链路
     */
    public static class PipelineAction {
        private List<Runnable> run = new ArrayList<>();

        private RedisConnection connection;

        public PipelineAction add(String key, BiConsumer<RedisConnection, byte[]> conn) {
            run.add(() -> conn.accept(connection, RedisClient.keyBytes(key)));
            return this;
        }

        public PipelineAction add(String key, String filed, ThreeConsumer<RedisConnection, byte[], byte[]> conn) {
            run.add(() -> conn.accept(connection, RedisClient.keyBytes(key), valBytes(filed)));
            return this;
        }

        public void execute() {
            template.executePipelined((RedisCallback<Object>) connection -> {
                PipelineAction.this.connection = connection;
                run.forEach(Runnable::run);
                return null;
            });
        }
    }

    @FunctionalInterface
    public interface ThreeConsumer<T, U, P> {
        void accept(T t, U u, P p);
    }
}
