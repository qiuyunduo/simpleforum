package com.qyd.web.admin.rest;

import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.statistics.dto.StatisticsCountDTO;
import com.qyd.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.statistics.service.StatisticsSettingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据统计后台
 *
 * @author 邱运铎
 * @date 2024-05-19 19:15
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "全站统计分析控制器", tags = "统计分析")
@RequestMapping(path = {"api/admin/statistics/", "admin/statistics/"})
public class StatisticsSettingRestController {
    @Autowired
    private StatisticsSettingService statisticsSettingService;

    static final Integer DEFAULT_DAY = 7;

    @GetMapping(path = "queryTotal")
    public ResVo<StatisticsCountDTO> queryTotal() {
        StatisticsCountDTO statisticCount = statisticsSettingService.getStatisticCount();
        return ResVo.ok(statisticCount);
    }

    @GetMapping(path = "pvUvDayList")
    public ResVo<List<StatisticsDayDTO>> pvUvDayList(@RequestParam(name = "day", required = false) Integer day) {
        day = (day == null || day == 0) ? DEFAULT_DAY : day;
        List<StatisticsDayDTO> pvUvDayList = statisticsSettingService.getPvUvDayList(day);
        return ResVo.ok(pvUvDayList);
    }
}
