package com.qyd.service.article.service;

import com.qyd.api.model.vo.article.dto.CategoryDTO;

import java.util.List;

/**
 * 标签 Service
 *
 * @author 邱运铎
 * @date 2024-04-09 14:30
 */
public interface CategoryService {
    /**
     * 根据类目ID,查询类目名
     *
     * @param categoryId
     * @return
     */
    String queryCategoryName(Long categoryId);

    /**
     * 查询所有的文章分类
     *
     * @return
     */
    List<CategoryDTO> loadAllCategories();

    /**
     * 根据类目名称查询类目ID
     *
     * @param category
     * @return
     */
    Long queryCategoryId(String category);

    /**
     * 刷新缓存
     */
    void refreshCache();
}
