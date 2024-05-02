package com.qyd.core.dal;

import java.util.function.Supplier;

/**
 * 区别于DsAno注解实现指定数据源的一些缺陷
 * 这里给出一个手动指定数据源的用法
 *
 * @author 邱运铎
 * @date 2024-05-02 18:35
 */
public class DsSelectExecutor {

    /**
     * 又返回结果
     *
     * @param ds
     * @param supplier
     * @return
     * @param <T>
     */
    public static <T> T submit(DS ds, Supplier<T> supplier) {
        // 将指定的数据源放入当前上下文数据源链表的栈顶，保证后续操作获取的数据源连接都是该指定的数据源
        DsContextHolder.set(ds);
        try {
            return supplier.get();
        } finally {
            // 执行方法完成后，弹出栈顶数据源，即清除之前指定的数据源环境，切换到前一个数据源环境
            DsContextHolder.reset();
        }
    }

    /**
     * 无返回结果
     *
     * @param ds
     * @param run
     */
    public static void execute(DS ds, Runnable run) {
        DsContextHolder.set(ds);
        try {
            run.run();
        } finally {
            DsContextHolder.reset();
        }
    }
}
