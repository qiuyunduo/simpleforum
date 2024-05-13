package com.qyd.service.chatai.service;

/**
 * @author 邱运铎
 * @date 2024-05-10 22:21
 */
public interface ChatGptService {

    /**
     * 判断是否在会话中
     *
     * @param wxUuid
     * @param content
     * @return
     */
    boolean inChat(String wxUuid, String content);

    /**
     * 开始进入聊天
     *
     * @param wxUuid
     * @param content   输入的内容
     * @return  chatGpt返回的结果
     */
    String chat(String wxUuid, String content);
}
