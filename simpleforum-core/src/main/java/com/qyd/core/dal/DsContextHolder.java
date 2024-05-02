package com.qyd.core.dal;

/**
 * 上下文中保存选择的数据源，存储当前选中的是哪个数据源
 *
 * @author 邱运铎
 * @date 2024-05-02 16:23
 */
public class DsContextHolder {

    /**
     * 正常情况下，子线程无法复用父线程的ThreadLocal中内容，
     * 但通过InheritableThreadLocal可以实现子线程复用父线程的线程本地变量
     * ----------------
     * 使用继承的线程上下文，支持异步时选择传递
     * 使用DsNode, 支持链式的数据源切换，如最外层使用master数据源，内部某个方法使用slave数据源，
     *  但需要注意在事务场景中不能交叉
     *
     */
    private static final ThreadLocal<DsNode> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    private DsContextHolder() {

    }

    public static void set (String dbType) {
        DsNode current = CONTEXT_HOLDER.get();
        CONTEXT_HOLDER.set(new DsNode(current, dbType));
    }

    public static String get() {
        DsNode ds = CONTEXT_HOLDER.get();
        return ds == null ? null : ds.ds;
    }

    /**
     * 移除上下文
     */
    public static void reset() {
        DsNode ds = CONTEXT_HOLDER.get();
        if (ds == null) {
            return;
        }
        if (ds.pre != null) {
            // 退出当前的数据源选择，切回去走上一次的数据源配置
            CONTEXT_HOLDER.set(ds.pre);
        } else {
            CONTEXT_HOLDER.remove();
        }
    }

    /**
     * 使用主数据源库数据源
     */
    public static void master() {
        set(MasterSlaveDsEnum.MASTER.name());
    }

    /**
     * 使用从数据库数据源
     */
    public static void slave() {
        set(MasterSlaveDsEnum.SLAVE.name());
    }

    public static void set(DS ds) {
        set(ds.name().toUpperCase());
    }

    public static class DsNode {
        DsNode pre;
        String ds;

        public DsNode(DsNode parent, String ds) {
            pre = parent;
            this.ds = ds;
        }
    }
}
