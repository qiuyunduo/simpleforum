package com.qyd.service.article.service;

import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.SearchTagReq;
import com.qyd.api.model.vo.article.TagReq;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.service.article.repository.entity.TagDO;

/**
 * 比钱后台接口
 *
 * @author 邱运铎
 * @date 2024-05-19 19:38
 */
public interface TagSettingService {
    void saveTag(TagReq tagReq);

    void deleteTag(Integer tagId);

    void operateTag(Integer tagId, Integer pushStatus);

    /**
     * 获取 tag 列表
     *
     * @param req
     * @return
     */
    PageVo<TagDTO> getTagList(SearchTagReq req);

    TagDTO getTagById(Long tagId);
}
