package com.qyd.service.user.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户关系表
 *
 * @author 邱运铎
 * @date 2024-04-20 18:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_relation")
public class UserRelationDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * 主用户ID， 被关注者
     */
    private Long userId;

    /**
     * 粉丝用户ID, 关注者
     */
    private Long followUserId;

    /**
     * 关注状态 0-未关注 1-已关注 2-取消关注
     */
    private Integer followState;
}
