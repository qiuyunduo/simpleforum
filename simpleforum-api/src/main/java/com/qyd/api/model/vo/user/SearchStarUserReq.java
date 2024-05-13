package com.qyd.api.model.vo.user;

import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-13 15:55
 */
@Data
public class SearchStarUserReq {
    /**
     * 用户昵称
     */
    private String name;
    /**
     * 星球编号
     */
    private String starNumber;
    /**
     * 用户登录名
     */
    private String userCode;

    private Integer state;

    /**
     * 分页
     */
    private Long pageNumber;
    private Long pageSize;
}
