package com.qyd.service.article.service.impl;

import cn.hutool.db.Page;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.CategoryReq;
import com.qyd.api.model.vo.article.SearchCategoryReq;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.core.util.NumUtil;
import com.qyd.service.article.conveter.CategoryStructMapper;
import com.qyd.service.article.repository.dao.CategoryDao;
import com.qyd.service.article.repository.entity.CategoryDO;
import com.qyd.service.article.repository.params.SearchCategoryParams;
import com.qyd.service.article.service.CategoryService;
import com.qyd.service.article.service.CategorySettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类后台接口
 *
 * @author 邱运铎
 * @date 2024-05-19 16:53
 */
@Service
public class CategorySettingServiceImpl implements CategorySettingService {
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void saveCategory(CategoryReq req) {
        CategoryDO categoryDO = CategoryStructMapper.INSTANCE.toDO(req);
        if (NumUtil.nullOrZero(req.getCategoryId())) {
            categoryDao.save(categoryDO);
        } else {
            categoryDO.setId(req.getCategoryId());
            categoryDao.updateById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        CategoryDO categoryDO = categoryDao.getById(categoryId);
        if (categoryDO != null) {
            categoryDao.removeById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public void operateCategory(Integer categoryId, Integer pushStatus) {
        CategoryDO categoryDO = categoryDao.getById(categoryId);
        if (categoryDO != null) {
            categoryDO.setStatus(pushStatus);
            categoryDao.updateById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public PageVo<CategoryDTO> getCategoryList(SearchCategoryReq req) {
        // 转换
        SearchCategoryParams params = CategoryStructMapper.INSTANCE.toSearchParams(req);
        // 查询
        List<CategoryDTO> categoryDTOS = categoryDao.listCategory(params);
        Long totalCount = categoryDao.countCategory(params);
        return PageVo.build(categoryDTOS, params.getPageSize(), params.getPageNum(), totalCount);
    }
}
