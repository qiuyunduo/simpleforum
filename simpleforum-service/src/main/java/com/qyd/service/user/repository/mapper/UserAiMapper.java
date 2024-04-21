package com.qyd.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.user.dto.StarUserInfoDTO;
import com.qyd.service.user.repository.entity.UserAiDO;
import com.qyd.service.user.repository.params.SearchStarWhiteParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ai用户登录mapper接口
 *
 * @author 邱运铎
 * @date 2024-04-19 9:42
 */
public interface UserAiMapper extends BaseMapper<UserAiDO> {

    Long countStarUsersByParams(@Param("searchParams")SearchStarWhiteParams params);

    List<StarUserInfoDTO> listStarUsersByParams(@Param("searchParams") SearchStarWhiteParams params,
                                               @Param("pageParam")PageParam newPageInstance);
}
