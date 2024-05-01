package com.qyd.core.mdc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author 邱运铎
 * @date 2024-05-01 15:51
 */
@Slf4j
@Aspect
@Component
public class MdcAspect implements ApplicationContextAware {
    // Spring中SPel表达式中用于解析SPel表达式的解析器
    private ExpressionParser parser = new SpelExpressionParser();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(MdcDot) || @within(MdcDot)")
    public void getLogAnnotation() {

    }

    @Around("getLogAnnotation()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        boolean hasTag = addMdcCode(joinPoint);
        try {
            Object ans = joinPoint.proceed();
            return ans;
        } finally {
            log.info("执行耗时: {}#{} = {}ms",
                    // 获取目标方法所属类的简单类名
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    // 获取目标方法名
                    joinPoint.getSignature().getName(),
                    System.currentTimeMillis());
            if (hasTag) {
                MdcUtil.reset();
            }
        }
    }

    /**
     * 往 日志MDC（上下文诊断映射容器）中放入bizCode
     *
     * @param joinPoint
     * @return
     */
    private boolean addMdcCode(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        MdcDot dot = method.getAnnotation(MdcDot.class);
        if (dot == null) {
            // 如果目标方法上没有 MdcDot注解，查看方法所属类上是否存在MdcDot注解
            dot = (MdcDot) joinPoint.getSignature().getDeclaringType().getAnnotation(MdcDot.class);
        }

        if (dot != null) {
            MdcUtil.add("bizCode", loadBizCode(dot.bizCode(), joinPoint));
            return true;
        }
        return false;
    }

    /**
     * 获取 MdcDot注解中 bizCode SPel表达式代表的实际值
     * @param key
     * @param joinPoint
     * @return
     */
    private String loadBizCode(String key, ProceedingJoinPoint joinPoint) {
        if (StringUtils.isBlank(key)) {
            return "";
        }

        // SPel中上下文，主要存储一些内容/对象
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        // 获取@RequestParam注解中的参数name的值
        String[] params = parameterNameDiscoverer.getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        // 获取 切面方法的入参值
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            // 将@RequestParam注解中name值和方法参数一一映射放入上下文中
            context.setVariable(params[i], args[i]);
        }
        // 从SPel上下文中获取SPel表达式的值，例如 key = #articled ; return 102;
        return parser.parseExpression(key).getValue(context, String.class);
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
