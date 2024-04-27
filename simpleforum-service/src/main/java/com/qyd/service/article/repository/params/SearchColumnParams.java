package com.qyd.service.article.repository.params;

import com.qyd.api.model.vo.PageParam;
import lombok.Data;

/**
 * 专栏查询
 *
 * @author 邱运铎
 * @date 2024-04-27 23:39
 */
@Data
public class SearchColumnParams extends PageParam {

    /**
     * 专栏名称
     */
    private String column;
}
