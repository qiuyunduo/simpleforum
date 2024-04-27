package com.qyd.api.model.vo.statistics.dto;

import lombok.Data;

/**
 * 每天的统计计数
 *
 * @author 邱运铎
 * @date 2024-04-27 21:14
 */
@Data
public class StatisticsDayDTO {

    /**
     * 日期
     */
    private String date;

    /**
     * PV 数量
     */
    private Long pvCount;

    /**
     * UV 数量
     */
    private Long uvCount;
}
