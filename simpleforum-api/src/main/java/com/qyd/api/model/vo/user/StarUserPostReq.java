package com.qyd.api.model.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-05-13 15:58
 */
@Data
public class StarUserPostReq implements Serializable {

    private static final long serialVersionUID = -7006241910451938447L;

    private Long id;

    /**
     * 用户名
     */
    private String userCode;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 星球编号
     */
    private String starNumber;

    /**
     * Ai策略
     */
    private Integer strategy;
}
