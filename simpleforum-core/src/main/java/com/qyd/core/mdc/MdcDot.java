package com.qyd.core.mdc;

import java.lang.annotation.*;

/**
 * ElementType.METHOD 注解可放在方法上
 * ElementType.TYPE 注解可放在类，接口，枚举，注解上
 *
 * @author 邱运铎
 * @date 2024-05-01 15:45
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MdcDot {
    /**
     * 如果日志需要携带一些重要的标识，用bizCode来存储
     * 后面放到日志打印中去，提供信息
     * 可以理解为业务编码 例如： #articleId, #req.articleId, #userId等
     * 还有就是 上面这些都是SPel表达式，在进行获取后会通过SPel解析器解析后获得实际值
     *
     * @return
     */
    String bizCode() default "";
}
