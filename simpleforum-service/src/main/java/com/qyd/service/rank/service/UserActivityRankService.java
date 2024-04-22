package com.qyd.service.rank.service;


import com.qyd.api.model.enums.rank.ActivityRankTimeEnum;
import com.qyd.api.model.vo.rank.dto.RankItemDTO;
import com.qyd.service.rank.service.model.ActivityScoreBo;

import java.util.List;

/**
 * 用户活跃度排行榜
 *
 * @author 邱运铎
 * @date 2024-04-22 22:07
 */
public interface UserActivityRankService {

    /**
     * 添加活跃度积分
     *
     * @param userId
     * @param activityScore
     */
    void addActivityScore(Long userId, ActivityScoreBo activityScore);

    /**
     * 查询用户的活跃度信息
     *
     * @param userId
     * @param time
     * @return
     */
    RankItemDTO queryRankInfo(Long userId, ActivityRankTimeEnum time);

    /**
     * 查询活跃度排行榜
     *
     * @param time
     * @param size
     * @return
     */
    List<RankItemDTO> queryRankList(ActivityRankTimeEnum time, int size);
}
