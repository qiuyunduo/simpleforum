package com.qyd.api.model.vo.notify.dto;

import com.qyd.api.model.enums.NotifyTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author 邱运铎
 * @date 2024-04-24 18:07
 */
@Data
public class NotifyMsgDTO implements Serializable {
    private static final long serialVersionUID = 3833777672628522348L;

    private Long msgId;

    /**
     * 消息关联的主体，如文章、评论等id
     */
    private String relateId;

    /**
     * 关联信息
     */
    private String relatedInfo;

    /**
     * 发起消息的用户ID
     */
    private Long operateUserId;

    /**
     * 发起消息的用户名
     */
    private String operateUserName;

    /**
     * 发起消息的用户头像
     */
    private String operateUserPhoto;

    /**
     * 消息类型
     *
     * @see NotifyTypeEnum#getType()
     */
    private Integer type;

    /**
     * 消息正文
     */
    private String msg;

    /**
     * 1 已读，0 未读
     */
    private Integer state;

    /**
     * 消息产生时间
     */
    private Timestamp createTime;
}
