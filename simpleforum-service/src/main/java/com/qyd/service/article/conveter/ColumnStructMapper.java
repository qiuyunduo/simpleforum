package com.qyd.service.article.conveter;

import com.qyd.api.model.vo.article.ColumnReq;
import com.qyd.api.model.vo.article.SearchColumnReq;
import com.qyd.api.model.vo.article.dto.ColumnDTO;
import com.qyd.api.model.vo.article.dto.SimpleColumnDTO;
import com.qyd.service.article.repository.entity.ColumnInfoDO;
import com.qyd.service.article.repository.params.SearchColumnParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 注解Mapper中componentModel = "spring" 代表使用spring的Component注解生成可注入的对象
 * 这样就可以使用@AutoWrited注解注入该mapper
 *
 * @author 邱运铎
 * @date 2024-05-05 19:02
 */
@Mapper(componentModel = "spring")
public interface ColumnStructMapper {
    // 注解中使用了 componentModel - "spring" 可以代替下面这种生成静态实例的方法
    // ColumnStructMapper INSTANCE = Mappers.getMapper(ColumnStructMapper.class);

    SearchColumnParams reqToSearchParams(SearchColumnReq req);

    @Mapping(source = "id", target = "columnId")
    @Mapping(source = "columnName", target = "column")
    @Mapping(source = "userId", target = "author")
    // Date 转 Long
    @Mapping(target = "publishTime", expression = "java(columnInfoDO.getPublishTime().getTime())")
    @Mapping(target = "freeStartTime", expression = "java(columnInfoDO.getFreeStartTime().getTime())")
    @Mapping(target = "freeEndTime", expression = "java(columnInfoDO.getFreeEndTime().getTime())")
    ColumnDTO infoToDto(ColumnInfoDO columnInfoDO);

    List<ColumnDTO> infoToDTOS(List<ColumnInfoDO> columnInfoDOS);

    // 专栏 ID 、专栏名、封面
    @Mapping(source = "id", target = "columnId")
    @Mapping(source = "columnName", target = "column")
    SimpleColumnDTO infoToSimpleDto(ColumnInfoDO columnInfoDO);

    List<SimpleColumnDTO> infoToSimpleDTOS(List<ColumnInfoDO> columnInfoDOS);

    @Mapping(source = "column", target = "columnName")
    @Mapping(source = "author", target = "userId")
    // Long 转 Date
    @Mapping(target = "freeStartTime", expression = "java(new java.util.Date(req.getFreeStartTime()))")
    @Mapping(target = "freeEndTime", expression = "java(new java.util.Date(req.getFreeEndTime()))")
    ColumnInfoDO toDo(ColumnReq req);
}
