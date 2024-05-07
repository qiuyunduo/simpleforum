package com.qyd.service.article.repository.params;

import com.qyd.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 邱运铎
 * @date 2024-05-04 20:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchTagParams extends PageParam {
    // 表签名称
    private String tag;
}
