package com.qyd.core.dal;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author 邱运铎
 * @date 2024-05-02 16:43
 */
@Aspect
public class DsAspect {

    /**
     * 切入点，拦截器，方法上有注解的地方，用于切换数据源
     */
    @Pointcut("@annotation(com.qyd.core.dal.DsAno) || @within(com.qyd.core.dal.DsAno)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws  Throwable {
        DsAno ds = getDsAno(proceedingJoinPoint);
        try {
            if (ds != null && (StringUtils.isNotBlank(ds.ds()) || ds.value() != null)) {
                // 当上下文中没有时，则写入线程上下文，判断应该用那个DB
                DsContextHolder.set(StringUtils.isNotBlank(ds.ds()) ? ds.ds() : ds.value().name());
            }
            return proceedingJoinPoint.proceed();
        } finally {
            // 清空上下文信息
            if (ds != null) {
                DsContextHolder.reset();
            }
        }
    }

    private DsAno getDsAno(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        DsAno ds = method.getAnnotation(DsAno.class);
        if (ds == null) {
            ds = (DsAno) proceedingJoinPoint.getSignature().getDeclaringType().getAnnotation(DsAno.class);
        }
        return ds;
    }
}
