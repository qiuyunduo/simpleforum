package com.qyd.web.front.login.wx.helper;

import com.qyd.api.model.vo.user.wx.*;
import com.qyd.core.util.CodeGenerateUtil;
import com.qyd.service.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-03 14:30
 */
@Slf4j
@Component
public class WxAckHelper {
    @Autowired
    private LoginService sessionService;

    @Autowired
    private WxLoginHelper qrLoginHelper;

    public BaseWxMsgResVo buildResponseBody(String eventType, String content, String fromUser) {
        // 返回的文本消息
        String textRes = null;
        // 返回的是图文消息
        List<WxImgTxtItemVo> imgTxtList = null;

        if ("subscribe".equals(eventType)) {
            // 订阅事件产生
            textRes = "优秀的你一关注，我那英俊的脸上就泛起了笑容。\n"
                    + "\n"
                    + "感谢你的关注！让我们一起撸起袖子加油干！！！";
        }
        else if ("加群".equalsIgnoreCase(content)) {
            WxImgTxtItemVo imgTxt = new WxImgTxtItemVo();
            imgTxt.setTitle("扫码加群");
            imgTxt.setDescription("加入技术派的技术交流群，卷起来！");
            imgTxt.setPicUrl("https://mmbiz.qpic.cn/mmbiz_jpg/sXFqMxQoVLGOyAuBLN76icGMb2LD1a7hBCoialjicOMsicvdsCovZq2ib1utmffHLjVlcyAX2UTmHoslvicK4Mg71Kyw/0?wx_fmt=jpeg");
            imgTxt.setUrl("https://mp.weixin.qq.com/s/aY5lkyKjLHWSUuEf1UT2yQ");
            imgTxtList = Arrays.asList(imgTxt);
        }
        // 微信公众号登录
        else if (CodeGenerateUtil.isVerifyCode(content)) {
            sessionService.autoRegisterWxUserInfo(fromUser);
            if (qrLoginHelper.login(content)) {
                textRes = "登录成功，开始愉快的玩耍Simple Forum";
            } else {
                textRes = "验证码过期了，刷新页面重试一下吧";
            }
        } else {
            textRes = "/:？ 还在找其他资料吗？ 请加我的微信 weixin_qiuyunduo";
        }

        if (textRes != null) {
            WxTxtMsgResVo vo = new WxTxtMsgResVo();
            vo.setContent(textRes);
            return vo;
        } else {
            WxImgTxtMsgResVo vo = new WxImgTxtMsgResVo();
            vo.setArticles(imgTxtList);
            vo.setArticleCount(imgTxtList.size());
            return vo;
        }
    }


}
