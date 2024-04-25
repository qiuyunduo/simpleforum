package com.qyd.api.model.vo.article.dto;

import lombok.Data;

/**
 * 翻页信息，但从实际展示来看前端并没有实现翻页相关功能
 *
 * @author 邱运铎
 * @date 2024-04-25 11:12
 */
@Data
public class ColumnArticleFlipDTO {
    String prevHref;
    Boolean prevShow;
    String nextHref;
    Boolean nextShow;
}
