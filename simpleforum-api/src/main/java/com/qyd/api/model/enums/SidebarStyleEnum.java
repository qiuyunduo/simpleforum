package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 邱运铎
 * @date 2024-04-22 21:00
 */
@AllArgsConstructor
public enum SidebarStyleEnum {
    NOTICE(1),
    ARTICLES(2),
    RECOMMEND(3),
    ABOUT(4),
    COLUMN(5),
    PDF(6),
    SUBSCRIBE(7),
    /**
     * 活跃度排行榜
     */
    ACTIVE_RANK(8),
    ;

    @Getter
    private int style;
}
