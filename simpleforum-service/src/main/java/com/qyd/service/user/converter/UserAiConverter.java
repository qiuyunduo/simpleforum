package com.qyd.service.user.converter;

import com.qyd.api.model.enums.UserAIStatEnum;
import com.qyd.api.model.enums.user.StarSourceEnum;
import com.qyd.service.user.repository.entity.UserAiDO;
import com.qyd.service.user.service.help.UserRandomGenHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 邱运铎
 * @date 2024-04-19 10:20
 */
public class UserAiConverter {

    public static UserAiDO initAi(Long userId) {
        return initAi(userId, null);
    }

    public static UserAiDO initAi(Long userId, String startNumber) {
        UserAiDO userAiDO = new UserAiDO();
        userAiDO.setUserId(userId);
        userAiDO.setStarType(0);
        userAiDO.setInviterUserId(0L);
        userAiDO.setStrategy(0);
        userAiDO.setInviteNum(0);
        userAiDO.setDeleted(0);
        userAiDO.setInviteCode(UserRandomGenHelper.genInviteCode(userId));

        if (StringUtils.isBlank(startNumber)) {
            userAiDO.setStarNumber("");
            userAiDO.setState(UserAIStatEnum.IGNORE.getCode());
        } else {
            userAiDO.setStarNumber(startNumber);
            userAiDO.setState(UserAIStatEnum.TRYING.getCode());
            // 先只支持java进阶之路的星球绑定
            userAiDO.setStrategy(StarSourceEnum.JAVA_GUIDE.getSource());
        }
        return userAiDO;
    }
}
