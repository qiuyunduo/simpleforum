package com.qyd.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * 发布文章请求参数
 *
 * @author 邱运铎
 * @date 2024-05-08 12:46
 */
@Data
public class ContentPostReq implements Serializable {

    /**
     * 正文内容
     */
    private String content;
}
