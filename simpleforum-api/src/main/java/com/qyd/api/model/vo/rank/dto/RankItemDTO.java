package com.qyd.api.model.vo.rank.dto;

import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 排行榜信息
 *
 * @author 邱运铎
 * @date 2024-04-22 22:14
 */
@Data
@Accessors(chain = true)
public class RankItemDTO {

    private Integer rank;

    private Integer score;

    private SimpleUserInfoDTO user;
}
