package com.qyd.service.statistics.service;

/**
 * 用户统计服务
 *
 * @author 邱运铎
 * @date 2024-04-25 13:59
 */
public interface UserStatisticService {

    /**
     * 添加在线人数
     *
     * @param add 正数，表示添加在线人数，负数，表示减少在线人数
     * @return
     */
    int incrOnlineUserCnt(int add);

    /**
     * 查询当前在线用户人数
     *
     * @return
     */
    int getOnlineUserCnt();
}
