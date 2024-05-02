package com.qyd.web.error;

import com.qyd.web.global.BaseViewController;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 这里Controller 注解中参数本来应该是对应的实例名称，但作者这里给了个/error确实让我很迷惑
 * 看大概代码逻辑是一个跳转错误页面的控制器
 * 但是我在全局异常处理拦截器中已经对错误页面返回进行了处理，
 * 而且这里返回的页面 /error 也没有对应的具体页面
 * 总的来说对这个控制器的功能不理解。
 *
 * 目前大概猜测是相对应用的所有错误页面做一个统一入口处理
 * 并且可以通过配置指定错误页面的入口
 *
 * 整个控制器应该是围绕javaweb中servlet来开发的
 *
 * @author 邱运铎
 * @date 2024-04-27 17:49
 */
@Controller("/error")
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomizeErrorController extends BaseViewController implements ErrorController {

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, Model model) {
        HttpStatus status = getStatus(request);

        if (status.is4xxClientError()) {
            model.addAttribute("message", "你这个请求错了吧，要不换个姿势！！！");
        }
        if (status.is5xxServerError()) {
            model.addAttribute("message", "服务器冒烟了，要不然你稍后试试！！！");
        }

        return new ModelAndView("error/index");
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        /**
         * javax.servlet.error.status_code是一个在Java Servlet API中定义的属性，
         * 它表示发生错误时的HTTP状态码。当一个Servlet处理请求时遇到错误，
         * 容器会转发请求到配置的错误页面（error page），同时设置这个属性来表示错误的状态码。
         *
         */
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode == null) {
            // INTERNAL_SERVER_ERROR 英文意思是服务内部问题
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
