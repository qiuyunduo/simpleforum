package com.qyd.api.model.vo.user;

import lombok.Data;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-19 21:31
 */
@Data
public class StarUserBatchOperateReq {
    // ids
    private List<Long> ids;

    // 状态
    private Integer status;
}
