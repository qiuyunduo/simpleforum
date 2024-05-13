package com.qyd.web.front.chat.rest;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.ai.AISourceEnum;
import com.qyd.web.front.chat.helper.WsAnswerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * STOMP协议的chatGpt聊天通讯实现方式
 *
 * @author 邱运铎
 * @date 2024-05-09 18:06
 */
@Slf4j
@RestController
public class ChatRestController {
    @Resource
    private WsAnswerHelper answerHelper;

    /**
     * 接受用户发送的消息
     *
     * 注解@MessageMapping("/chat/{session}") 表示接受 "/app/chat/xxx路径发送来的消息
     *  如果有@SendTo, 则表示将返回结果，转发其对应的路径上 （这个sendTo的路径，就是前端订阅的路径）
     * 注解@DestinationVariable: 实现路径上的参数解析 类似与SpringMvc中@PathVariable注解
     * 注解@Header 实现请求头格式的参数解析，@Header（“headName"） 表示获取某个请求头的内容
     * @param msg
     * @param session
     * @param attrs
     */
    @MessageMapping("/chat/{session}")
    public void chat(String msg, @DestinationVariable("session") String session,
                     @Header("simpSessionAttributes")Map<String, Object> attrs) {
        String aiType = (String) attrs.get(WsAnswerHelper.AI_SOURCE_PARAM);
        answerHelper.execute(attrs, () -> {
            log.info("{} 用户开始了对话: {} - {}", ReqInfoContext.getReqInfo().getUser(), aiType, msg);
            AISourceEnum source = aiType == null ? null : AISourceEnum.valueOf(aiType);
            answerHelper.sendMsgToUser(source, session, msg);
        });
    }
}
