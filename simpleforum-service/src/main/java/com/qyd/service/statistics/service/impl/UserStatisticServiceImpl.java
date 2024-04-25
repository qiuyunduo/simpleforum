package com.qyd.service.statistics.service.impl;

import com.qyd.service.statistics.service.UserStatisticService;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户统计服务
 *
 * @author 邱运铎
 * @date 2024-04-25 14:01
 */
@Service
public class UserStatisticServiceImpl implements UserStatisticService {

    /**
     * 对于单机的场景，可以直接使用本地局部变量来实现计数
     * 对于集群场景，可以考虑借助redis的zSet 来实现集群的在线用户人数统计
     */
    private AtomicInteger onlineUserCnt = new AtomicInteger(0);

    @Override
    public int incrOnlineUserCnt(int add) {
        return onlineUserCnt.addAndGet(add);
    }

    @Override
    public int getOnlineUserCnt() {
        return onlineUserCnt.get();
    }
}
