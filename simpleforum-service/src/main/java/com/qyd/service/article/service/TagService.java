package com.qyd.service.article.service;

import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.dto.TagDTO;

/**
 * 标签Service
 *
 * @author 邱运铎
 * @date 2024-05-04 19:49
 */
public interface TagService {

    PageVo<TagDTO> queryTags(String key, PageParam pageParam);

    Long queryTagsId(String tag);
}
