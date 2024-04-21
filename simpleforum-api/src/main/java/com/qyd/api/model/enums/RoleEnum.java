package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author 邱运铎
 * @date 2024-04-21 18:30
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {
    NORMAL(0, "普通用户"),
    ADMIN(1, "超级用户")
    ;

    private int role;
    private String desc;

    public static String role(Integer roleId) {
        if (Objects.equals(roleId, 1)) {
            return ADMIN.name();
        } else {
            return NORMAL.name();
        }
    }
}
