package com.qyd.web.front.login.wx.controller;

import com.qyd.api.model.vo.ResVo;
import com.qyd.core.mdc.MdcDot;
import com.qyd.web.front.login.wx.helper.WxLoginHelper;
import com.qyd.web.front.login.wx.vo.WxLoginVo;
import com.qyd.web.global.BaseViewController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 公众号登录的长连接控制器
 *
 * @author 邱运铎
 * @date 2024-05-02 0:42
 */
@Controller
@Slf4j
public class WxLoginController extends BaseViewController {
    @Autowired
    private WxLoginHelper qrLoginHelper;

    /**
     * 客户端与后端建立扫描二维码的长连接
     * 这个在点开登录窗口展示出二维码时就会调用
     * 建立起浏览器和本服务器之间的SseEmitter 长连接
     *
     * @return
     */
    @MdcDot
    @ResponseBody
    @GetMapping(path = "subscribe", produces = {org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter subscribe(String deviceId) throws IOException {
        return qrLoginHelper.subscribe();
    }

    @GetMapping(path = "/login/fetch")
    @ResponseBody
    public String resendCode(String deviceId) throws IOException {
        return qrLoginHelper.resend();
    }

    @MdcDot
    @GetMapping(path = "/login/refresh")
    @ResponseBody
    public ResVo<WxLoginVo> refresh(String deviceId) throws IOException {
        WxLoginVo vo = new WxLoginVo();
        String code = qrLoginHelper.refreshCode();
        if (StringUtils.isBlank(code)) {
            // 刷新失败， 之前的连接已经失效，重新建立连接
            vo.setCode(code);
            vo.setReconnect(true);
        } else {
            vo.setCode(code);
            vo.setReconnect(false);
        }
        return ResVo.ok(vo);
    }

}
