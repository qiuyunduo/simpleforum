package com.qyd.service.user.converter;

import com.qyd.api.model.vo.user.SearchStarUserReq;
import com.qyd.service.user.repository.params.SearchStarWhiteParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author 邱运铎
 * @date 2024-05-13 16:02
 */
@Mapper
public interface UserStructMapper {
    UserStructMapper INSTANCE = Mappers.getMapper(UserStructMapper.class);

    @Mapping(source = "pageNumber", target = "pageNum")
    @Mapping(source = "state", target = "status")
    SearchStarWhiteParams toSearchParams(SearchStarUserReq req);
}
