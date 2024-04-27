package com.qyd.core.permission;

import java.lang.annotation.*;

/**
 * @author 邱运铎
 * @date 2024-04-27 13:03
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {
    /**
     * 限定权限
     *
     * @return
     */
    UserRole role() default UserRole.ALL;
}
