package com.qyd.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * 保存 Category 请求参数
 *
 * @author 邱运铎
 * @date 2024-04-09 16:35
 */
@Data
public class CategoryReq implements Serializable {

    private Long categoryId;

    private String category;

    //排序
    private Integer rank;
}
