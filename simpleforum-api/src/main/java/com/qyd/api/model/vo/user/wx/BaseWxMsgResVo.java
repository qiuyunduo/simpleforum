package com.qyd.api.model.vo.user.wx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * 微信公众号响应用户发送消息时需要服务器返回的数据结构体
 *
 * @author 邱运铎
 * @link <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html"/>
 * @date 2024-05-03 14:43
 */
@Data
@JacksonXmlRootElement(localName = "xml")
public class BaseWxMsgResVo {
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;
    @JacksonXmlProperty(localName = "CreateTime")
    private Long createTime;
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;
    // 这里按照微信公众开发说明应该还有一个content字段，但作者这里没写，不知道是不是默认为空即可
    // 解决了，这个类知识一个基础类，方式的都是该类的子类，这里知识把一些公用的抽了出来
}
