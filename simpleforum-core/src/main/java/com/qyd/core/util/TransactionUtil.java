package com.qyd.core.util;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 事务辅助工具类
 *
 * @author 邱运铎
 * @date 2024-04-26 19:44
 */
public class TransactionUtil {

    /**
     * 注册事务回调-事务提交前执行，如果没在事务中就立即执行
     *
     * @param runnable
     */
    public static void registryBeforeCommitOrImmediateRun(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        // 处于事务中
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // 等事务提交时在提交前执行
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    runnable.run();
                }
            });
        } else {
            // 马上执行
            runnable.run();
        }
    }

    /**
     * 事务执行完/回滚完之后执行
     *
     * @param runnable
     */
    public static void registerAfterCompletionOrImmediateRun(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        // 处于事务中
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // 等事务执行完成（包括异常回滚后）之后执行
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    runnable.run();
                }
            });
        } else {
            // 不在事务中，立马执行
            runnable.run();
        }
    }

    /**
     * 事务正常提交之后执行
     *
     * @param runnable
     */
    public static void registerAfterCommitImmediateRun(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        // 处于事务中
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // 等事务提交之后执行
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }
            });
        } else {
            // 立马执行
            runnable.run();
        }
    }
}
