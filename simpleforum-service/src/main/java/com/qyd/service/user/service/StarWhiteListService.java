package com.qyd.service.user.service;

import com.qyd.api.model.enums.UserAIStatEnum;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.user.SearchStarUserReq;
import com.qyd.api.model.vo.user.StarUserPostReq;
import com.qyd.api.model.vo.user.dto.StarUserInfoDTO;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-13 15:54
 */
public interface StarWhiteListService {

    PageVo<StarUserInfoDTO> getList(SearchStarUserReq req);

    void operate(Long id, UserAIStatEnum operate);

    void update(StarUserPostReq req);

    void batchOperate(List<Long> ids, UserAIStatEnum operate);

    void reset(Integer authorId);
}
