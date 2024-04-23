package com.qyd.service.article.conveter;

import com.qyd.api.model.vo.article.CategoryReq;
import com.qyd.api.model.vo.article.SearchCategoryReq;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.service.article.repository.entity.CategoryDO;
import com.qyd.service.article.repository.params.SearchCategoryParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 类目相关实体类之间由于字段名称不同单业务含义是一致的，通过该映射类转换
 * 有个坑，lombok 的maven依赖似乎需要在 mapstruct maven依赖的前面否则会报未知字段
 * 个人理解是很多类都是用lombok的@Data注解自动生成字段
 * 如果 mapstruct 加载在 lombok之前在编译阶段会因为lombok注解还未生效导致获取字段get方法出错导致报未知字段错误
 * 目前我是通过该方法解决了经常编译报未知字段错误
 *
 * @author 邱运铎
 * @date 2024-04-09 16:17
 */
@Mapper
public interface CategoryStructMapper {

    //instance
    CategoryStructMapper INSTANCE = Mappers.getMapper(CategoryStructMapper.class);

    //rep to params
    @Mapping(source = "pageNumber", target = "pageNum")
    SearchCategoryParams toSearchParams(SearchCategoryReq req);

    //do to dto
    @Mapping(source = "id", target = "categoryId")
    @Mapping(source = "categoryName", target = "category")
    CategoryDTO toDTO(CategoryDO categoryDO);

    List<CategoryDTO> toDTOs(List<CategoryDO> list);

    //req to do
    @Mapping(source = "category", target = "categoryName")
    CategoryDO toDO(CategoryReq categoryReq);
}
