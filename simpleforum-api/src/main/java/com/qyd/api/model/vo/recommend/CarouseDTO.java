package com.qyd.api.model.vo.recommend;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-04-23 15:35
 */
@Data
@Accessors(chain = true)
public class CarouseDTO implements Serializable {

    private static final long serialVersionUID = 1048555496974144842L;

    /**
     * 说明
     */
    private String name;

    /**
     * 图片地址
     */
    private String imgUrl;

    /**
     * 跳转地址
     */
    private String actionUrl;
}
