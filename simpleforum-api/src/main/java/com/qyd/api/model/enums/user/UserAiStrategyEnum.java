package com.qyd.api.model.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ai可用次数的条件策略
 * 不同的策略，在进入网站后可使用ai的次数不一样
 * 绑定微信公众号的 使用一次，
 * 绑定邀请用户的 使用二次，
 * 绑定 java进阶之路的使用4次
 * 绑定技术派星球的使用8次
 * ---新的理解
 * 这里的策略是针对用户绑定了多少个第三方，
 * 只绑定微信公众号的最少只有一次
 * 绑定了微信公众号，邀请用户，java进阶之路，技术派有15次
 *
 * @author 邱运铎
 * @date 2024-04-19 11:34
 */
@Getter
@AllArgsConstructor
public enum UserAiStrategyEnum {
    WECHAT(1),
    INVITE_USER(2),
    STAR_JAVA_GUIDE(4),
    STAR_TECH_PAI(8),
    ;

    /**
     * 二进制使用姿势
     * 第0位： = 1 表示已绑定微信公众号
     * 第1位： = 1 表示绑定了邀请用户
     * 第2位： = 1 表示绑定了java星球
     * 第3位： = 1 表示绑定了技术派星球
     */
    private Integer condition;

    /**
     * 更新策略，用户可能进行了多个绑定，需要更新用户的策略
     *
     * @param input
     * @return
     */
    public Integer updateCondition(Integer input) {
        if (input == null) {
            input = 0;
        }

        /**
         * 使用 运算与操作不是很理解，
         * todo 不清楚是因为什么需要需要进行这个操作而不是直接更新condition
         *  或者直接在condition的基础上加一定数值
         * 上面todo已解决, 理解错误， 这里并非仅仅是更新使用次数，
         * 更新次数只是附带的一个使用
         * 这里主要是更新策略， 例如开始用户只绑定了公众号，那么策略 0001 1
         * 然后用户又绑定了邀请用户， 那么策略0011 3
         * 这样 0011 就代表用户即绑定了公众号，有绑定了邀请用户
         * --- 新的补充 转到UserAiDO类中的strategy字段注释
         */
        return input | condition;
    }

    public boolean match(Integer strategy) {
        // todo (strategy & condition) == condition.intValue() 这个判断的意义何在？
        // 上面todo 已解决 这里是判断用户绑定了几个第三方
        // 0001 代表仅绑定了公众号  1001 代表用户绑定了公众号和技术派星球， 1111 代表用户四个第三方均已绑定
        //--- 新的补充 转到UserAiDO类中的strategy字段注释
        return strategy != null && (strategy & condition) == condition.intValue();
    }
}
