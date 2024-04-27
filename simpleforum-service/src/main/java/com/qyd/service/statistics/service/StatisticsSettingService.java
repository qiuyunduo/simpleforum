package com.qyd.service.statistics.service;

import com.qyd.api.model.vo.statistics.dto.StatisticsCountDTO;
import com.qyd.api.model.vo.statistics.dto.StatisticsDayDTO;

import java.util.List;

/**
 * 数据统计后台接口
 *
 * @author 邱运铎
 * @date 2024-04-27 20:08
 */
public interface StatisticsSettingService {

    /**
     * 保存计数
     *
     * @param host
     */
    void saveRequestCount(String host);

    /**
     * 获取总数
     *
     * @return
     */
    StatisticsCountDTO getStatisticCount();

    /**
     * 获取每天的PV UV 统计数据
     *
     * @param day
     * @return
     */
    List<StatisticsDayDTO> getPvUvDayList(Integer day);
}
