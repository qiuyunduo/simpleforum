package com.qyd.web.front.chat.view;

import com.qyd.web.global.BaseViewController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 邱运铎
 * @date 2024-05-09 17:56
 */
@Controller
@RequestMapping(path = "chat")
public class ChatViewController extends BaseViewController {

    @RequestMapping(path = {"", "/", "home"})
    public String index() {
        return "views/chat-home/index";
    }
}
