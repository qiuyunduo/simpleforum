package com.qyd.api.model.vo.recommend;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 侧边推广信息
 *
 * @author 邱运铎
 * @date 2024-04-22 20:54
 */
@Data
@Accessors(chain = true)
public class SideBarItemDTO {

    private String title;

    private String name;

    private String url;

    private String img;

    private Long time;

    /**
     * tag 列表
     */
    private List<Integer> tags;

    /**
     * 评分信息
     */
    private RateVisitDTO visit;
}
