package com.qyd.service.user.service;

import com.qyd.api.model.enums.ai.AISourceEnum;
import com.qyd.api.model.vo.chat.ChatItemVo;
import com.qyd.api.model.vo.user.UserPwdLoginReq;

/**
 * @author 邱运铎
 * @date 2024-04-21 16:41
 */
public interface UserAiService {

    /**
     * 保存聊天历史记录
     *
     * @param source
     * @param user
     * @param item
     */
    void pushChatItem(AISourceEnum source, Long user, ChatItemVo item);

    /**
     * 获取用户的最大聊天次数
     *
     * @param userId
     * @return
     */
    int getMaxChatCnt(Long userId);

    /**
     * 重建用户绑定的星球编号
     *
     * @param loginReq
     */
    void initOrUpdateAiInfo(UserPwdLoginReq loginReq);
}
