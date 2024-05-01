package com.qyd.web.hook.filter;

import cn.hutool.core.date.StopWatch;
import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.core.async.AsyncUtil;
import com.qyd.core.mdc.MdcUtil;
import com.qyd.core.util.*;
import com.qyd.service.sitemap.service.impl.SitemapServiceImpl;
import com.qyd.service.statistics.service.StatisticsSettingService;
import com.qyd.service.user.service.LoginService;
import com.qyd.web.global.GlobalInitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        long start = System.currentTimeMillis();
        HttpServletRequest request = null;
        StopWatch stopWatch = new StopWatch("请求耗时");
        try {
            stopWatch.start("请求参数构建");
            request = this.initReqInfo((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
            stopWatch.stop();

            // 处理跨域
            stopWatch.start("cors");
            CrossUtil.buildCors(request, (HttpServletResponse) servletResponse);
            stopWatch.stop();

            stopWatch.start("业务执行");
            filterChain.doFilter(request, servletResponse);
            stopWatch.stop();
        } finally {
            stopWatch.start("输出请求日志");
            buildRequestLog(ReqInfoContext.getReqInfo(), request, System.currentTimeMillis() - start);
            // 一个链路请求完毕，清空MDC相关设置的变量(如GlobalTraceId, 用户信息)
            MdcUtil.clear();
            ReqInfoContext.clear();
            stopWatch.stop();

            if (! isStaticURI(request) && ! EnvUtil.isPro()) {
                log.info("{} - cost:\n{}", request.getRequestURI(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
            }
        }
    }

    @Override
    public void destroy() {
    }

    private HttpServletRequest initReqInfo(HttpServletRequest request, HttpServletResponse response) {
        if (isStaticURI(request)) {
            // 静态资源直接放行
            return request;
        }

        StopWatch stopWatch = new StopWatch("请求参数构建");
        try {
            stopWatch.start("traceId");
            // 添加全链路的traceId
            MdcUtil.addTraceId();
            stopWatch.stop();

            stopWatch.start("请求基本信息");
            // 手动写入一个session, 借助 OnlineCountListener 实现在线人数实时统计
            request.getSession().setAttribute("latestVisit", System.currentTimeMillis());

            ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
            reqInfo.setHost(request.getHeader("host"));
            reqInfo.setPath(request.getPathInfo());
            if (reqInfo.getPath() == null) {
                String uri = request.getRequestURI();
                int index = uri.indexOf("?");
                if (index > 0) {
                    uri = uri.substring(0, index);
                }
                reqInfo.setPath(uri);
            }
            reqInfo.setReferer(request.getHeader("referer"));
            reqInfo.setClientIp(IpUtil.getClientIp(request));
            reqInfo.setUserAgent(request.getHeader("User-Agent"));
            reqInfo.setDeviceId(getOrInitDeviceId(request, response));

            request = this.wrapperRequest(request, reqInfo);
            stopWatch.stop();

            stopWatch.start("登录用户信息");
            // 初始化登录信息
            globalInitService.initLoginUser(reqInfo);
            stopWatch.stop();

            ReqInfoContext.addReqInfo(reqInfo);

            stopWatch.start("pv/uv站点统计");
            // 更新pv/uv计数
            AsyncUtil.execute(() -> SpringUtil.getBean(SitemapServiceImpl.class).saveVisitInfo(reqInfo.getClientIp(), reqInfo.getPath()));
            stopWatch.stop();

            stopWatch.start("回写traceId");
            // 返回头中记录traceId
            response.setHeader(GLOBAL_TRACE_ID_HEADER, Optional.ofNullable(MdcUtil.getTraceId()).orElse(""));
            stopWatch.stop();
        } catch (Exception e) {
            log.error("init reqInfo error!", e);
        } finally {
            if (!EnvUtil.isPro()) {
                log.info("{} -> 请求构建耗时: \n{}", request.getRequestURI(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
            }
        }

        return  request;
    }

    private void buildRequestLog(ReqInfoContext.ReqInfo reqInfo, HttpServletRequest request, long costTIme) {
        if (reqInfo == null || isStaticURI(request)) {
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("method=" + request.getMethod()).append("; ");
        if (StringUtils.isNotBlank(reqInfo.getReferer())) {
            msg.append("referer=").append(URLDecoder.decode(reqInfo.getReferer())).append("; ");
        }
        msg.append("remoteIp=").append(reqInfo.getClientIp()).append("; ");
        msg.append("agent=").append(reqInfo.getUserAgent()).append("; ");

        if (reqInfo.getUserId() != null) {
            // 打印用户信息
            msg.append("user=").append(reqInfo.getUserId()).append("; ");
        }

        msg.append("uri=").append(request.getRequestURI()).append("; ");
        // getQueryString 获取get请求中的param,
        // 例如： http://127.0.0.1:8080/read?articleId=1 获取 "articleId = 1“
        if (StringUtils.isNotBlank(request.getQueryString())) {
            msg.append("?").append(URLDecoder.decode(request.getQueryString())).append("; ");
        }

        msg.append("payload=").append(reqInfo.getPayload()).append("; ");
        msg.append("cost=").append(costTIme).append("; ");
        REQ_LOG.info("{}", msg);

        // 保存请求计数
        statisticsSettingService.saveRequestCount(reqInfo.getClientIp());
    }

    private HttpServletRequest wrapperRequest(HttpServletRequest request, ReqInfoContext.ReqInfo reqInfo) {
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            return request;
        }

        BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
        // post请求的表单参数从request中获取放入到reqInfo中，保证post请求参数在整个流程总可见
        reqInfo.setPayload(requestWrapper.getBodyString());
        return requestWrapper;
    }

    /**
     * 判断请求的是否为静态资源
     *
     * @param request
     * @return
     */
    private boolean isStaticURI(HttpServletRequest request) {
        return request == null
                || request.getRequestURI().endsWith("css")
                || request.getRequestURI().endsWith("js")
                || request.getRequestURI().endsWith("png")
                || request.getRequestURI().endsWith("ico")
                || request.getRequestURI().endsWith("svg")
                || request.getRequestURI().endsWith("min.js.map")
                || request.getRequestURI().endsWith("min.js.map");
    }

    /**
     * 初始化设备id
     *
     * @param request
     * @param response
     * @return
     */
    private String getOrInitDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String deviceId = request.getParameter("deviceId");
        if (StringUtils.isNotBlank(deviceId) && !"null".equalsIgnoreCase(deviceId)) {
            return deviceId;
        }

        Cookie device = SessionUtil.findCookieByName(request, LoginService.USER_DEVICE_KEY);
        if (device == null) {
            // 设备id 通过uuid随机生成
            deviceId = UUID.randomUUID().toString();
            if (response != null) {
                response.addCookie(SessionUtil.newCookie(LoginService.USER_DEVICE_KEY, deviceId));
            }
            return deviceId;
        }
        return  device.getValue();
    }
}
