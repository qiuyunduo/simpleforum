package com.qyd.api.model.vo.article;

import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-19 19:40
 */
@Data
public class SearchTagReq {
    /**
     * 标签名称
     */
    private String tag;

    /**
     * 分页
     */
    private Long pageNumber;
    private Long pageSize;
}
