package com.qyd.core.async;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.qyd.core.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 异步工具类
 *
 * @author 邱运铎
 * @date 2024-04-23 21:03
 */
@Slf4j
public class AsyncUtil {
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = this.defaultFactory.newThread(r);
            if (!thread.isDaemon()) {
                thread.setDaemon(true);
            }

            thread.setName("simple-forum-" + this.threadNumber.getAndIncrement());
            return thread;
        }
    };
    private static ExecutorService executorService;
    private static SimpleTimeLimiter simpleTimeLimiter;

    static {
        initExecutorService(0, 50);
    }

    public static void initExecutorService(int core, int max) {
        // 异步工具类的默认线程池构建, 参数选择原则:
        //  1. 技术派不存在cpu密集型任务，大部分操作都设计到 redis/mysql 等io操作
        //  2. 统一的异步封装工具，这里的线程池是一个公共的执行仓库，不希望被其他的线程执行影响，因此队列长度为0, 核心线程数满就创建线程执行，超过最大线程，就直接当前线程执行
        //  3. 同样因为属于通用工具类，再加上技术派的异步使用的情况实际上并不是非常饱和的，因此空闲线程直接回收掉即可；大部分场景下，cpu * 2的线程数即可满足要求了
        executorService = new ExecutorBuilder().setCorePoolSize(core)
                .setMaxPoolSize(max)
//                .setKeepAliveTime(0)  //该设置应该是重复了
                .setKeepAliveTime(0, TimeUnit.SECONDS)
                .setWorkQueue(new SynchronousQueue<Runnable>())
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .setThreadFactory(THREAD_FACTORY)
                .buildFinalizable();
        simpleTimeLimiter = SimpleTimeLimiter.create(executorService);
    }

    /**
     * 带超时时间的方法调用执行
     * 当执行时间超过给定的时间，则返回一个超时异常
     * 内部的其他任务还是正常执行
     *
     * 若超时时间内执行完毕，则直接返回
     *
     * @param time
     * @param unit
     * @param call
     * @return
     * @param <T>
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static <T> T callWithTimeLimit(long time, TimeUnit unit, Callable<T> call) throws ExecutionException, InterruptedException, TimeoutException {
        return simpleTimeLimiter.callWithTimeout(call, time, unit);
    }

    public static void execute(Runnable call) {
        executorService.execute(call);
    }

    public static <T> Future<T> submit(Callable<T> t) {
        return executorService.submit(t);
    }

    public static boolean sleep(Number timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout.longValue());
            return true;
        } catch (InterruptedException var3) {
            return false;
        }
    }

    public static boolean sleep(Number millis) {
        return millis == null ? true : sleep(millis.longValue());
    }

    public static boolean sleep(long millis) {
        if (millis > 0L) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException var3) {
                return false;
            }
        }
        return true;
    }

    public static class CompletableFutureBridge {
        private List<CompletableFuture> list;
        private Map<String, Long> cost;
        private String taskName;

        public CompletableFutureBridge() {
            this("CompletableFutureExecute");
        }

        public CompletableFutureBridge(String task) {
            this.taskName = task;
            list = new ArrayList<>();
            cost = new ConcurrentHashMap<>();
            cost.put(task, System.currentTimeMillis());
        }

        /**
         * 异步执行，带返回结果
         *
         * @param supplier
         * @return
         */
        public CompletableFutureBridge supplyAsync(Supplier supplier) {
            return supplyAsync(supplier, executorService);
        }

        public CompletableFutureBridge supplyAsync(Supplier supplier, ExecutorService executor) {
            return supplyAsyncWithTimeRecord(supplier, supplier.toString(), executor);
        }

        public CompletableFutureBridge supplyAsyncWithTimeRecord(Supplier supplier, String name) {
            return supplyAsyncWithTimeRecord(supplier, name, executorService);
        }

        public CompletableFutureBridge supplyAsyncWithTimeRecord(Supplier supplier, String name, ExecutorService executor) {
            list.add(CompletableFuture.supplyAsync(supplyWithTime(supplier, name), executor));
            return this;
        }

        /**
         * 异步并发执行，无返回结果
         *
         * @param run
         * @return
         */
        public CompletableFutureBridge runAsync(Runnable run) {
            list.add(CompletableFuture.runAsync(runWithTime(run, run.toString())));
            return this;
        }

        public CompletableFutureBridge runAsync(Runnable run, ExecutorService executor) {
            return runAsyncWithTimeRecord(run, run.toString(), executor);
        }

        /**
         * 异步并发执行，并记录耗时
         *
         * @param run
         * @param name
         * @return
         */
        public CompletableFutureBridge runAsyncWithTimeRecord(Runnable run, String name) {
            return runAsyncWithTimeRecord(run, name, executorService);
        }

        public CompletableFutureBridge runAsyncWithTimeRecord(Runnable run, String name, ExecutorService executor) {
            list.add(CompletableFuture.runAsync(runWithTime(run, name), executor));
            return this;
        }

        private Runnable runWithTime(Runnable run, String name) {
            return () -> {
                startRecord(name);
                try {
                    run.run();
                } finally {
                    endRecord(name);
                }
            };
        }

        private Supplier supplyWithTime(Supplier call, String name) {
            return () -> {
                startRecord(name);
                try {
                    return call.get();
                } finally {
                    endRecord(name);
                }
            };
        }

        public CompletableFutureBridge allExecuted() {
            CompletableFuture.allOf(ArrayUtil.toArray(list, CompletableFuture.class)).join();
            endRecord(this.taskName);
            return this;
        }

        private void startRecord(String name) {
            cost.put(name, System.currentTimeMillis());
        }

        private void endRecord(String name) {
            long now = System.currentTimeMillis();
            cost.put(name, now - cost.getOrDefault(name, now));
        }

        /**
         * 打印异步执行的任务的耗时占比
         */
        public void prettyPrint() {
            StringBuilder sb = new StringBuilder();
            sb.append('\n');
            Long totalCost = cost.remove(taskName);
            sb.append("StopWatch '")
                    .append(taskName)
                    .append("': running time = ...")
                    .append(totalCost)
                    .append(" ms");
            sb.append('\n');
            if (cost.size() <= 1) {
                sb.append("No task info kept");
            } else {
                sb.append("------------------------------------------sss---\n");
                sb.append("ms         %     Task name\n");
                sb.append("---------------------------------------------\n");
                // 根据当前环境获取百分数实例
                NumberFormat pf = NumberFormat.getPercentInstance();
                // 设置整数最小两位
                pf.setMinimumIntegerDigits(2);
                // 设置百分位保留两位小数
                pf.setMinimumFractionDigits(2);
                // 是否分组  暂时不理解先放着
                pf.setGroupingUsed(false);
                for (Map.Entry<String, Long> entry : cost.entrySet()) {
                    sb.append(entry.getValue()).append("\t\t");
                    sb.append(pf.format(entry.getValue() / (double) totalCost)).append("\t\t");
                    sb.append(entry.getKey()).append("\n");
                }
            }
            if (!EnvUtil.isPro()) {
                log.info("\n---------------------\n{}\n--------------------\n", sb);
            }
        }
    }

    public static CompletableFutureBridge concurrentExecutor(String...names) {
        if (names.length > 0) {
            return new CompletableFutureBridge(names[0]);
        }
        return new CompletableFutureBridge();
    }
}
