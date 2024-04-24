package com.qyd.service.notify.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import com.qyd.api.model.enums.NotifyTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 邱运铎
 * @date 2024-04-24 18:24
 */
@Data
@Accessors(chain = true)
@TableName("notify_msg")
public class NotifyMsgDO extends BaseDO {
    private static final long serialVersionUID = -3503865925269010288L;

    /**
     * 消息关联的主体
     * - 如文章收藏、评论、回复评论、点赞消息，这里存文章ID
     * - 如系统通知消息时，这里就是存的系统通知消息正文主键，也可以是0
     * - 如关注： 这里就是0
     *
     */
    private Long relatedId;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 消息通知的用户ID
     */
    private Long notifyUserId;

    /**
     * 触发这个消息的用户id
     */
    private Long operateUserId;

    /**
     * 消息类型
     *
     * @see NotifyTypeEnum#getType()
     */
    private Integer type;

    /**
     * 0-未查看，1-已查看
     */
    private Integer state;
}
