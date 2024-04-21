package com.qyd.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * 保存Tag请求参数
 *
 * @author 邱运铎
 * @date 2024-04-11 0:38
 */
@Data
public class TagReq implements Serializable {
    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tag;

    /**
     * 类目ID
     */
    private Long categoryId;
}
