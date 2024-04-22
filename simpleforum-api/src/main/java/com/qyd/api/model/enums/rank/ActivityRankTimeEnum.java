package com.qyd.api.model.enums.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活跃度排行榜时间周期
 *
 * @author 邱运铎
 * @date 2024-04-22 22:17
 */
@Getter
@AllArgsConstructor
public enum ActivityRankTimeEnum {
    DAY(1, "day"),
    MONTH(2, "month"),
    ;

    private int type;
    private String desc;

    public static ActivityRankTimeEnum nameOf(String name) {
        if (DAY.desc.equalsIgnoreCase(name)) {
            return DAY;
        } else if (MONTH.desc.equalsIgnoreCase(name)) {
            return MONTH;
        }
        return null;
    }
}
