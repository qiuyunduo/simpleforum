package com.qyd.web.front.rank;

import com.qyd.api.model.enums.rank.ActivityRankTimeEnum;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.rank.dto.RankInfoDTO;
import com.qyd.api.model.vo.rank.dto.RankItemDTO;
import com.qyd.service.rank.service.UserActivityRankService;
import com.qyd.web.global.BaseViewController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 排行榜
 *
 * @author 邱运铎
 * @date 2024-05-09 17:13
 */
@Controller
public class RankController extends BaseViewController {
    @Autowired
    private UserActivityRankService userActivityRankService;

    /**
     * 活跃用户排行榜
     *
     * @param time
     * @param model
     * @return
     */
    @RequestMapping(path = "/rank/{time}")
    public String rank(@PathVariable(value = "time") String time,
                       Model model) {
        ActivityRankTimeEnum rankTime = ActivityRankTimeEnum.nameOf(time);
        if (rankTime == null) {
            rankTime = ActivityRankTimeEnum.MONTH;
        }
        List<RankItemDTO> list = userActivityRankService.queryRankList(rankTime, 30);
        RankInfoDTO info = new RankInfoDTO();
        info.setTime(rankTime);
        info.setItems(list);
        ResVo<RankInfoDTO> vo = ResVo.ok(info);
        model.addAttribute("vo", vo);
        return "views/rank/index";
    }
}
