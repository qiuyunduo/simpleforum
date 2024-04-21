package com.qyd.service.user.service.conf;

import com.qyd.api.model.enums.ai.AISourceEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * todo 这里的配置类字段和application-ai.yml配置文件中中的字段非强一致
 *  例如： 这里是maxNum 配置文件中是 max-num, 如果后面出错还是改回来吧
 *
 * @author 邱运铎
 * @date 2024-04-21 17:15
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfig {
    @Data
    public static class AiMaxChatNumStrategyConf {
        /**
         * 默认的策略
         */
        private Integer basic;
        /**
         * 公众号用户 AI交互次数
         */
        private Integer wechat;
        /**
         * 星球用户 AI交互次数
         */
        private Integer star;

        // 星球最大编号
        private Integer starNumber;
        /**
         * 星球试用用户 AI交互次数
         */
        private Integer starTry;
        /**
         * 绑定了邀请者，再当前次数基础上新增的策略, 默认增加 10%
         */
        private Float invited;

        /**
         * 根据邀请的人数，增加的聊天次数策略，默认增加 20%
         */
        private Float inviteNum;
    }

    /**
     * 用户的最大使用次数配置项
     */
    private AiMaxChatNumStrategyConf maxNum;

    /**
     * 当前支持的AI模型
     * todo: 这里直接通过配置文件中 字符串来拿到对应的枚举值？不会出问题吗？
     */
    private List<AISourceEnum> source;
}
