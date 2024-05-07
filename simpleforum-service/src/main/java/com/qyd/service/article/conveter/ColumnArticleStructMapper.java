package com.qyd.service.article.conveter;

import com.qyd.api.model.vo.article.ColumnArticleReq;
import com.qyd.api.model.vo.article.SearchColumnArticleReq;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.repository.params.ColumnArticleParams;
import com.qyd.service.article.repository.params.SearchColumnArticleParams;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author 邱运铎
 * @date 2024-05-05 19:24
 */
@Mapper
public interface ColumnArticleStructMapper {
    ColumnArticleStructMapper INSTANCE = Mappers.getMapper(ColumnArticleStructMapper.class);

    SearchColumnArticleParams toSearchParams(SearchColumnArticleReq req);

    ColumnArticleParams toParams(ColumnArticleReq req);

    ColumnArticleDO repToDO(ColumnArticleReq req);
}
