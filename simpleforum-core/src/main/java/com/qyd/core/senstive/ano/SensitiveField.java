package com.qyd.core.senstive.ano;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * todo 类名称的翻译是 敏感字段
 *  目前猜测使用AOP进行内容敏感校验
 *
 * @author 邱运铎
 * @date 2024-04-16 0:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SensitiveField {
    /**
     * 绑定的数据库表中的指定字段
     *
     * @return
     */
    String bind() default "";
}
