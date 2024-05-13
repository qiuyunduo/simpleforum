package com.qyd.api.model.vo.rank.dto;

import com.qyd.api.model.enums.rank.ActivityRankTimeEnum;
import java.util.List;
import lombok.Data;

/**
 * 排行榜信息
 *
 * @author 邱运铎
 * @date 2024-05-09 17:17
 */
@Data
public class RankInfoDTO {
    private ActivityRankTimeEnum time;
    private List<RankItemDTO> items;
}
