package com.qyd.api.model.vo.recommend;

import lombok.Data;

/**
 * 资源访问， 下载， 评分 信息
 *
 * @author 邱运铎
 * @date 2024-04-22 20:56
 */
@Data
public class RateVisitDTO {

    /**
     * 查看次数
     */
    private Integer visit;

    /**
     * 下载次数
     */
    private Integer download;

    /**
     * 评分，浮点数，String方式返回，避免精度问题
     */
    private String rate;

    public RateVisitDTO() {
        visit = 0;
        download = 0;
        rate = "8";
    }

    public void incrVisit() {
        visit += 1;
    }

    public void incrDownload() {
        download += 1;
    }
}
