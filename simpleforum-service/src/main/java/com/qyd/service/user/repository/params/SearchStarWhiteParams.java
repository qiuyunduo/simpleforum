package com.qyd.service.user.repository.params;

import com.qyd.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 邱运铎
 * @date 2024-04-19 9:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchStarWhiteParams extends PageParam {

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 星球编号
     */
    private String starNumber;

    /**
     * 登录用户名
     */
    private String name;

    /**
     * 用户编号
     */
    private String userCode;
}
