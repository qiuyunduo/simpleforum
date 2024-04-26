package com.qyd.service.user.service.help;

import com.qyd.service.user.service.conf.AiConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 星球编号检查帮助类
 *
 * @author 邱运铎
 * @date 2024-04-26 19:07
 */
@Component
public class StarNumberHelper {

    @Resource
    private AiConfig aiConfig;

    public Boolean checkStarNumber(String starNumber) {
        // 判断编号是否在 0 - maxStarNumber 之间，其中maxStarNumber是配置在application-ai.yml中的
        return Integer.parseInt(starNumber) >= 0 && Integer.parseInt(starNumber) <= aiConfig.getMaxNum().getStarNumber();
    }
}
