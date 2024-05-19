package com.qyd.service.article.conveter;

import com.qyd.api.model.vo.article.SearchTagReq;
import com.qyd.api.model.vo.article.TagReq;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.service.article.repository.dao.TagDao;
import com.qyd.service.article.repository.entity.TagDO;
import com.qyd.service.article.repository.params.SearchTagParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-19 19:45
 */
@Mapper
public interface TagStructMapper {
    // instance
    TagStructMapper INSTANCE = Mappers.getMapper(TagStructMapper.class);

    // req to params
    @Mapping(source = "pageNumber", target = "pageNum")
    SearchTagParams toSearchParams(SearchTagReq req);

    // do to dto
    @Mapping(source = "id", target = "tagId")
    @Mapping(source = "tagName", target = "tag")
    TagDTO toDTO(TagDO tagDO);

    List<TagDTO> toDTOs(List<TagDO> list);

    @Mapping(source = "tag", target = "tagName")
    TagDO toDO(TagReq tagReq);
}
