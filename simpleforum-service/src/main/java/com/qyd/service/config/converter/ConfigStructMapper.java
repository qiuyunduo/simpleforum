package com.qyd.service.config.converter;

import com.qyd.api.model.vo.banner.ConfigReq;
import com.qyd.api.model.vo.banner.SearchConfigReq;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;
import com.qyd.api.model.vo.config.GlobalConfigReq;
import com.qyd.api.model.vo.config.SearchGlobalConfigReq;
import com.qyd.api.model.vo.config.dto.GlobalConfigDTO;
import com.qyd.service.config.repository.entity.ConfigDO;
import com.qyd.service.config.repository.entity.GlobalConfigDO;
import com.qyd.service.config.repository.parmas.SearchConfigParams;
import com.qyd.service.config.repository.parmas.SearchGlobalConfigParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-22 19:14
 */
@Mapper
public interface ConfigStructMapper {
    // instance
    ConfigStructMapper INSTANCE = Mappers.getMapper(ConfigStructMapper.class);

    // req to params
    @Mapping(source = "pageNumber", target = "pageNum")
    SearchConfigParams toSearchParams(SearchConfigReq req);

    // req to params
    @Mapping(source = "pageNumber", target = "pageNum")
    // key to keywords
    @Mapping(source = "keywords", target = "key")
    SearchGlobalConfigParams toSearchGlobalParams(SearchGlobalConfigReq req);

    // do to dto
    ConfigDTO toDTO(ConfigDO configDO);

    List<ConfigDTO> toDTOList(List<ConfigDO> configDOList);

    ConfigDO toDO(ConfigReq req);

    // do to dto
    // key to keywords
    @Mapping(source = "key", target = "keywords")
    GlobalConfigDTO toGlobalDTO(GlobalConfigDO configDO);

    List<GlobalConfigDTO> toGlobalDTOList(List<GlobalConfigDO> configDOList);

    @Mapping(source = "keywords", target = "key")
    GlobalConfigDO toGlobalDO(GlobalConfigReq req);
}
