package com.qyd.api.model.vo.article;

import lombok.Data;

/**
 * 该实体暂时想不明白，此时认为可以使用SearchCategoryParams类替代
 * 先写上后面再看 todo
 *
 * @author 邱运铎
 * @date 2024-04-09 16:19
 */
@Data
public class SearchCategoryReq {
    //类目名称
    private String category;
    //分页
    private Long pageNumber;
    private Long pageSize;
}
