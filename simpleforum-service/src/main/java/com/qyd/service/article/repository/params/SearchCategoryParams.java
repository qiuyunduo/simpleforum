package com.qyd.service.article.repository.params;

import com.qyd.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 邱运铎
 * @date 2024-04-09 15:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchCategoryParams extends PageParam {
    //类目名称
    private String category;
}
