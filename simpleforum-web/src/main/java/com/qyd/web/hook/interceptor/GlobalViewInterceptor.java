package com.qyd.web.hook.interceptor;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.core.util.JsonUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.rank.service.UserActivityRankService;
import com.qyd.service.rank.service.model.ActivityScoreBo;
import com.qyd.web.global.GlobalInitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 注入全局配置信息：
 * - thymeleaf 站点信息， 基本信息，在这里注入
 *
 * @author 邱运铎
 * @date 2024-04-27 12:14
 */
@Slf4j
@Service
public class GlobalViewInterceptor implements AsyncHandlerInterceptor {
    @Autowired
    private GlobalInitService globalInitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 判断目标方法上是否存在Permission注解
            Permission permission = handlerMethod.getMethod().getAnnotation(Permission.class);
            if (permission == null) {
                // 判断目标类上是否存在Permission注解，因为有的类下所有方法都需要进行权限认证，
                // 所以直接标注在类上，需要通过getBeanType来二次确认当前方法是否需要权限认证
                permission = handlerMethod.getBeanType().getAnnotation(Permission.class);
            }

            if (permission == null || permission.role() == UserRole.ALL) {
                if (ReqInfoContext.getReqInfo() != null) {
                    // 用户活跃度更新
                    SpringUtil.getBean(UserActivityRankService.class).addActivityScore(ReqInfoContext.getReqInfo().getUserId(),
                            new ActivityScoreBo().setPath(ReqInfoContext.getReqInfo().getPath()));
                }
                return true;
            }

            if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getUserId() == null) {
                // 这里handlerMethod.getMethod().getDeclaringClass() 和 上面handleMethod.getBeanType都是获取目标类对象
                if (handlerMethod.getMethod().getDeclaringClass().getAnnotation(ResponseBody.class) != null
                        || handlerMethod.getMethod().getDeclaringClass().getAnnotation(RestController.class) != null) {
                    // 访问需要登录的rest接口
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.getWriter().println(JsonUtil.toStr(ResVo.fail(StatusEnum.FORBID_NOT_LOGIN)));
                    response.getWriter().flush();
                    return false;
                } else if (request.getRequestURI().startsWith("/api/admin/")) {
                    response.sendRedirect("/admin");
                } else {
                    // 访问需要登录的页面直接跳转到登录界面
                    response.sendRedirect("/");
                }
                return false;
            }
            if (permission.role() == UserRole.ADMIN
                    && !UserRole.ADMIN.name().equalsIgnoreCase(ReqInfoContext.getReqInfo().getUser().getRole())) {
                String str = UserRole.ADMIN.name();
                ReqInfoContext.ReqInfo reqInfo = ReqInfoContext.getReqInfo();
                String str2 = reqInfo.getUser().getRole();
                // 设置无权限
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!ObjectUtils.isEmpty(modelAndView)) {
            if (response.getStatus() != HttpStatus.OK.value()) {
                try {
                    ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
                    // fixme 此fixme为原作者所写-对于异常重定向到 /error 时。会导致登录信息丢失，待解决
                    // 作者的这个fixme 表示下面的代码是为了修复这个问题，并且已经通过下面代码修复了，而不是仍存在问题
                    // 所以作者在这里为了修复异常重定向后用户登录信息丢失问题，在拦截器这里对于那些非Http.Ok的请求
                    // 先初始化下登录用户信息，主要是初始化ReqInfo这个携带用户登录信息的ThreadLocal
                    // 之后再通过globalInitService从ReqInfoContext中取出放入global中
                    // 完成这个动作后，可以将ReqInfoContext中的ReqInfo清除，因为用户登录信息已经存到global中了，避免OOM.
                    globalInitService.initLoginUser(reqInfo);
                    // 将登陆用户基本信息塞入到ReqInfoContext中的ThreadLocal中，没有登录就是null
                    ReqInfoContext.addReqInfo(reqInfo);
                    modelAndView.getModel().put("global", globalInitService.globalAttr());
                } finally {
                    // todo 这里清除用户的登录信息，和try中初始化用户登录信息不是矛盾了吗
                    // 目前有点理解了， 作者将ReqInfo中的信息转入到global,该ThreadLocal其实就没用了
                    // 作者只是将ReqInfo作为获取用户信息到global中的一个中转站
                    ReqInfoContext.clear();
                }
            } else {
                modelAndView.getModel().put("global", globalInitService.globalAttr());
            }
        }
    }
}
