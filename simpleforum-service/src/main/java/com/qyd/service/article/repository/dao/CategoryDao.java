package com.qyd.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyd.api.model.enums.PushStatusEnum;
import com.qyd.api.model.enums.YesOrNoEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.service.article.conveter.CategoryStructMapper;
import com.qyd.service.article.repository.entity.CategoryDO;
import com.qyd.service.article.repository.mapper.CategoryMapper;
import com.qyd.service.article.repository.params.SearchCategoryParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类目 Service
 *
 * @author 邱运铎
 * @date 2024-04-09 15:13
 */
@Repository
public class CategoryDao extends ServiceImpl<CategoryMapper, CategoryDO> {
    public List<CategoryDO> listAllCategoriesFromDb() {
        return lambdaQuery()
                .eq(CategoryDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(CategoryDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .list();
    }

    /**
     * 抽取一个私有方法，构造查询条件
     * @param params
     * @return
     */
    private LambdaQueryChainWrapper<CategoryDO> createCategoryQuery(SearchCategoryParams params) {
        return lambdaQuery()
                .eq(CategoryDO::getDeleted, YesOrNoEnum.NO.getCode())
                .like(StringUtils.isNoneBlank(params.getCategory()), CategoryDO::getCategoryName, params.getCategory());
    }

    /**
     * 获取所有 Category 列表 （分页）
     *
     * @param params
     * @return
     */
    public List<CategoryDTO> listCategory(SearchCategoryParams params) {
        List<CategoryDO> list = createCategoryQuery(params)
                .orderByDesc(CategoryDO::getUpdateTime)
                .orderByDesc(CategoryDO::getRank)
                .last(PageParam.getLimitSql(
                        PageParam.newPageInstance(params.getPageNum(), params.getPageSize())
                ))
                .list();
        return CategoryStructMapper.INSTANCE.toDTOs(list);
    }

    /**
     * 获取所有 category 的总数 （分页）
     *
     * @param params
     * @return
     */
    public Long countCategory(SearchCategoryParams params) {
        return createCategoryQuery(params)
                .count();
    }
}
