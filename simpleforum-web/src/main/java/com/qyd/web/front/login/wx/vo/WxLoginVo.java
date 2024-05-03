package com.qyd.web.front.login.wx.vo;

import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-03 17:34
 */
@Data
public class WxLoginVo {

    /**
     * 验证码
     */
    private String code;

    /**
     * 二维码
     */
    private String qr;

    /**
     * true 表示需要重新建立连接
     */
    private boolean reconnect;
}
