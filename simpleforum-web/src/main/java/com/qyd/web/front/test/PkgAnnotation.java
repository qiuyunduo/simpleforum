package com.qyd.web.front.test;

import java.lang.annotation.*;

/**
 * 包级注解的文章说明
 *
 * @author 邱运铎
 * @date 2024-05-09 11:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface PkgAnnotation {
    String value() default "";
}
