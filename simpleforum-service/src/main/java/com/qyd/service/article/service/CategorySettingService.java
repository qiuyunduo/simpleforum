package com.qyd.service.article.service;

import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.CategoryReq;
import com.qyd.api.model.vo.article.SearchCategoryReq;
import com.qyd.api.model.vo.article.dto.CategoryDTO;

/**
 * 分类后台接口
 *
 * @author 邱运铎
 * @date 2024-05-19 16:51
 */
public interface CategorySettingService {
    void saveCategory(CategoryReq req);

    void deleteCategory(Integer categoryId);

    void operateCategory(Integer categoryId, Integer pushStatus);

    /**
     * 获取 category 列表
     *
     * @param req
     * @return
     */
    PageVo<CategoryDTO> getCategoryList(SearchCategoryReq req);
}
