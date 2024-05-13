package com.qyd.service.chatai.constants;

import com.qyd.api.model.enums.ai.AISourceEnum;

import java.time.LocalDate;

/**
 * @author 邱运铎
 * @date 2024-05-09 21:43
 */
public class ChatConstants {

    /**
     * 记录每个用户针对不同的ai的使用次数
     */
    public static String getAiRateKey(AISourceEnum ai) {
        return "chat.rates." + ai.name().toLowerCase();
    }

    public static String getAiRateKeyPerDay(AISourceEnum ai) {
        return "chat.rate." + ai.name().toLowerCase() + "-" + LocalDate.now();
    }

    /**
     * 聊天历史记录
     *
     * @param ai
     * @param user
     * @return
     */
    public static String getAiHistoryRecordsKey(AISourceEnum ai, Long user) {
        return "chat.history." + ai.name().toLowerCase() +"." + user;
    }

    /**
     * 每个用户的最大使用次数
     */
    public static final int MAX_CHAT_GPT_QAS_CNT = 10;

    /**
     * 最多保存的历史聊天记录
     */
    public static final int MAX_HISTORY_RECORD_ITEMS = 500;

    /**
     * 两次提问的间隔时间，要求20s
     */
    public static final long QAS_TIME_INTERVAL = 20_000;

    public static final String CHAT_REPLY_RECOMMEND = "请注册SIMPLE_FORUM之后再来体验吧！官网： \n https://simpleforum.com";
    public static final String CHAT_REPLY_BEGIN = "让我们开始体验ChatGpt的魅力吧~";
    public static final String CHAT_REPLY_OVER = "体验结束，让我们下次再见吧！";
    public static final String CHAT_REPLY_CNT_OVER = "次数使用完了哦，勾搭一下群主，多申请点使用次数把~\n 微信：weixin_qiuyunduo";


    public static final String CHAT_REPLY_TIME_WAITING = "chatGpt还在努力回答中，请等待几秒之后再问一次吧...";
    public static final String CHAT_REPLY_QAS_TOO_FAST = "提问太频繁了，喝一杯咖啡，暂缓一下";

    public static final String TOKEN_OVER = "您的免费次数已经使用完毕了！";

    /**
     * 异步聊天时返回的提示文案
     */
    public static final String ASYNC_CHAT_TIP = "小派正在努力回答中，耐心等待一下";

    public static final String SENSITIVE_QUESTION = "提问中包含敏感词:%s, 请微信联系作者「qiuyunduo」加入白名单";
}
