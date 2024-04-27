package com.qyd.web.hook.filter;

import com.qyd.service.statistics.service.StatisticsSettingService;
import com.qyd.web.global.GlobalInitService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 1. 请求参数日志输出过滤器
 * 2. 判断用户是否登录
 *
 * @author 邱运铎
 * @date 2024-04-27 20:04
 */
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "reqRecordFilter", asyncSupported = true)
public class ReqRecordFilter implements Filter {
    private static Logger REQ_LOG = LoggerFactory.getLogger("req");

    /**
     * 返给前端的traceID, 用户日志追踪
     */
    private static final String GLOBAL_TRACE_ID_HEADER = "g-trace_id";

    @Autowired
    private GlobalInitService globalInitService;

    @Autowired
    private StatisticsSettingService statisticsSettingService;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {
    }
}
