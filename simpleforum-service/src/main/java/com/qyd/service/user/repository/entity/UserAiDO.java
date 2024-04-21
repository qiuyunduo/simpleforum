package com.qyd.service.user.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * ai 用户表 可以使用网站接入的AI大模型的用户
 *
 * @author 邱运铎
 * @date 2024-04-19 9:26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("user_ai")
public class UserAiDO extends BaseDO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 知识星球编号
     */
    private String starNumber;

    /**
     * 星球来源： 1：java进阶之路， 2： 技术派
     */
    private Integer starType;

    /**
     * 当前用户绑定的邀请者
     */
    private Long inviterUserId;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 当前用户邀请的人数
     */
    private Integer inviteNum;

    /**
     * 二进制使用姿势<br/>
     * 第0位： = 1 表示已绑定微信公众号<br/>
     * 第1位： = 1 表示已绑定了邀请用户<br/>
     * 第2位： = 1 表示已绑定java进阶之路星球<br/>
     * 第3位： = 1 表示已绑定了技术派星球
     * todo 暂时理解是 1， 2， 4， 8 代表不同含义，感觉不是很合适，
     *  后面再看这样做能获得系统哪方面的提升，感觉不如 0， 1， 2， 3来的好
     * 上面todo已解决： 因为这里的绑定并非互斥的，所有一个用户可以绑定多个第三方
     * 1 代表只绑定了公众号
     * 2 代表志只绑定了邀请用户
     * 3 代表绑定了公众号 和 邀请用户
     * 依此类推
     * 15 代表绑定了公众号， 邀请用户， java进阶之路， 技术派
     * ----注意
     * 根据后面的业务处理发现，
     * 这里用户绑定微信公众号和星球只能选择绑定一个其中一个
     * 然后 绑定星球，也只能在 java进阶之路和技术派之中选择一个
     * 所以用户的绑定策略只有 以下几种
     * 0001 绑定了微信公众号
     * 0010 绑定了邀请用户
     * 0100 仅绑定了java进阶之路
     * 1000 仅绑定了技术派
     * 0011 绑定了邀请用户和公众号，
     * 0110 绑定邀请用户 和 java进阶之路
     * 1010，绑定了邀请用户 和 技术派
     */
    private Integer strategy;

    /**
     * 0 审核中 1 试用中 2 审核通过 3 审核拒绝
     */
    private Integer state;

    /**
     * 删除标记 0-未删除，1-删除
     */
    private Integer deleted;
}
