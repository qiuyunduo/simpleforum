package com.qyd.api.model.vo.user.wx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.ToString;

/**
 * 简单文本消息回复
 *
 * @author 邱运铎
 * @link <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html"/>
 * @date 2024-05-03 20:17
 */
@Data
@ToString(callSuper = true)
@JacksonXmlRootElement(localName = "xml")
public class WxTxtMsgResVo extends BaseWxMsgResVo {

    @JacksonXmlProperty(localName = "Content")
    private String content;

    public WxTxtMsgResVo() {
        setMsgType("text");
    }
}
