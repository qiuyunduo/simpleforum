package com.qyd.service.article.service.impl;

import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.service.article.repository.dao.TagDao;
import com.qyd.service.article.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 注解@RequiredArgsConstructor将类中final或者@NonNull修饰的属性加入到构造函数中
 * 带有spring的controller,Component, service, configure 等注解类中构造对象中的入参对象会友
 * spring ioc机制自动注入，不需要@Autowrited注解技能实现构造函数紫自动注入
 *
 * @author 邱运铎
 * @date 2024-05-04 19:51
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    @Override
    public PageVo<TagDTO> queryTags(String key, PageParam pageParam) {
        List<TagDTO> tagDTtoS = tagDao.listOnlineTag(key, pageParam);
        Integer totalCount = tagDao.countOnlineTag(key);
        return PageVo.build(tagDTtoS, pageParam.getPageSize(), pageParam.getPageNum(), totalCount);
    }

    @Override
    public Long queryTagsId(String tag) {
        return tagDao.selectTagIdByTag(tag);
    }
}
