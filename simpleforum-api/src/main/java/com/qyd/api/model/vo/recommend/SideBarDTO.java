package com.qyd.api.model.vo.recommend;

import com.qyd.api.model.enums.SidebarStyleEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 侧边栏推广信息
 *
 * @author 邱运铎
 * @date 2024-04-22 20:42
 */
@Data
@Accessors(chain = true)
public class SideBarDTO {

    private String title;

    private String subTitle;

    private String icon;

    private String img;

    private String url;

    private String content;

    private List<SideBarItemDTO> items;

    /**
     * 侧边栏样式
     *
     * @see SidebarStyleEnum#getStyle(
     */
    private Integer style;
}
