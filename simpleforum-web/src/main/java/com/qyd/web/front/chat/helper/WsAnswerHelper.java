package com.qyd.web.front.chat.helper;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.ai.AISourceEnum;
import com.qyd.api.model.vo.chat.ChatRecordsVo;
import com.qyd.core.mdc.MdcUtil;
import com.qyd.service.chatai.ChatFacade;
import com.qyd.service.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-05-09 18:09
 */
@Slf4j
@Component
public class WsAnswerHelper {
    public static final String AI_SOURCE_PARAM = "AI";

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private ChatFacade chatFacade;

    public void sendMsgToUser(String session, String question) {
        ChatRecordsVo res = chatFacade.autoChat(question, vo -> response(session, vo));
        log.info("Ai直接返回: {}", res);
    }

    public void sendMsgToUser(AISourceEnum ai, String session, String question) {
        if (ai == null) {
            // 自动选择AI类型
            sendMsgToUser(session, question);
        } else {
            ChatRecordsVo res = chatFacade.autoChat(ai, question, vo -> response(session, vo));
            log.info("Ai直接返回: {}", res);
        }
    }

    public void sendMsgHistoryToUser(String session, AISourceEnum ai) {
        ChatRecordsVo vo = chatFacade.history(ai);
        response(session, vo);
    }

    /**
     * 将返回结果推送给用户
     *
     * @param session
     * @param response
     */
    public void response(String session, ChatRecordsVo response) {
        // convertAndSendToUser 方法可以发送信息给指定用户
        // 底层会自动将第二个参数目的地址，/chat/rsp 拼接为 /user/username/chat/rsp
        // 其中第二参数 username 即为这里的第一个参数 session
        // username 也是AuthHandshakeHandler 中配置的 Principal 用户识别标志
        simpMessagingTemplate.convertAndSendToUser(session, "/chat/rsp", response);
    }

    public void execute(Map<String, Object> attributes, Runnable func) {
        try {
            ReqInfoContext.ReqInfo reqInfo = (ReqInfoContext.ReqInfo) attributes.get(LoginService.SESSION_KEY);
            ReqInfoContext.addReqInfo(reqInfo);
            String traceId = (String) attributes.get(MdcUtil.TRACE_ID_KEY);
            MdcUtil.add(MdcUtil.TRACE_ID_KEY, traceId);

            // 执行具体的业务逻辑
            func.run();
        } finally {
            ReqInfoContext.clear();
            MdcUtil.clear();
        }
    }
}
