package com.qyd.api.model.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 邱运铎
 * @date 2024-04-19 10:42
 */
@Getter
@AllArgsConstructor
public enum StarSourceEnum {

    /**
     * java进阶之路星球
     */
    JAVA_GUIDE(1),

    /**
     * 技术派星球
     */
    TECH_API(2),
    ;

    private int source;
}
