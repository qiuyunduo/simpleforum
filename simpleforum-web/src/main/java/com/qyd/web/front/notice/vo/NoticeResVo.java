package com.qyd.web.front.notice.vo;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.notify.dto.NotifyMsgDTO;
import lombok.Data;

import java.util.Map;

/**
 * @author 邱运铎
 * @date 2024-05-09 16:29
 */
@Data
public class NoticeResVo {

    /**
     * 消息通知列表
     */
    private PageListVo<NotifyMsgDTO> list;

    /**
     * 每个分类（评论，回复，点赞，收藏，关注消息，系统消息）的未读数量
     */
    private Map<String, Integer> unreadCountMap;


    /**
     * 当前选中的消息类型
     */
    private String selectType;
}
