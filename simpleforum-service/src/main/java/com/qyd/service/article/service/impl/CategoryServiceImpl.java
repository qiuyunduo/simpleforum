package com.qyd.service.article.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.qyd.api.model.enums.YesOrNoEnum;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.service.article.conveter.ArticleConverter;
import com.qyd.service.article.repository.dao.CategoryDao;
import com.qyd.service.article.repository.entity.CategoryDO;
import com.qyd.service.article.service.CategoryService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-09 15:04
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    /**
     * 分类数一般不会特别多，如编程领域可以预期的分类将不会超过30，所以可以做一个全量的内存缓存
     * todo 后续可以改为Guava -> Redis
     */
    private LoadingCache<Long, CategoryDTO> categoryCaches;

    private CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    /**
     * 初始化缓存
     * todo 构造缓存数据中根据categoryId获得类目信息，这里面id是从什么地方来的需要确认。 --已解决
     * 当前不涉及往缓存中添加数据，仅仅是开辟一个指定大小的内存空间，
     * 缓存每查询一次，就往缓存中加一个数据。，所有不是说当前直接把数据库中的所有类目信息拿到然后缓存
     * 而是在每次查询过程中，缓存慢慢存入数据
     *
     */
    @PostConstruct
    public void init() {
        categoryCaches = CacheBuilder.newBuilder().maximumSize(300).build(new CacheLoader<Long, CategoryDTO>() {
            @Override
            public CategoryDTO load(@NotNull Long categoryId) throws Exception {
                CategoryDO category = categoryDao.getById(categoryId);
                if (category == null || category.getDeleted() == YesOrNoEnum.YES.getCode()) {
                    return CategoryDTO.EMPTY;
                }
                return new CategoryDTO(categoryId, category.getCategoryName(), category.getRank());
            }
        });
    }

    /**
     * 从缓存中根据类目 ID 查询类目名
     * @param categoryId
     * @return
     */
    @Override
    public String queryCategoryName(Long categoryId) {
        return categoryCaches.getUnchecked(categoryId).getCategory();
    }

    /**
     * 查询所有的分类
     * @return
     */

    @Override
    public List<CategoryDTO> loadAllCategories() {
        if (categoryCaches.size() <= 5) {
            refreshCache();
        }
        List<CategoryDTO> list = new ArrayList<>(categoryCaches.asMap().values());
        list.removeIf(s -> s.getCategoryId() <= 0);
        list.sort(Comparator.comparingInt(CategoryDTO::getRank));
        return list;
    }

    @Override
    public Long queryCategoryId(String category) {
        return categoryCaches.asMap().values().stream()
                .filter(s -> s.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .map(CategoryDTO::getCategoryId)
                .orElse(null);
    }

    /**
     * 刷新缓存
     */
    @Override
    public void refreshCache() {
        List<CategoryDO> list = categoryDao.listAllCategoriesFromDb();
        categoryCaches.invalidateAll();
        categoryCaches.cleanUp();
        //这里从 do to  dto 没有使用MapStruct ，而是使用自己写的转换,应该是多人协同开发导致的，todo 改成MapStruct进行映射
        list.forEach(s -> categoryCaches.put(s.getId(), ArticleConverter.toDto(s)));
    }
}
