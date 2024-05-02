package com.qyd.web.test;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.core.dal.DsAno;
import com.qyd.core.dal.DsSelectExecutor;
import com.qyd.core.dal.MasterSlaveDsEnum;
import com.qyd.service.statistics.service.StatisticsSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 邱运铎
 * @date 2024-05-02 21:49
 */
@DsAno(MasterSlaveDsEnum.SLAVE)
@RestController
@RequestMapping(path = "test")
@Slf4j
public class TestController {

    @Autowired
    private StatisticsSettingService statisticsSettingService;


    @GetMapping(path = "ds/read")
    public String readOnly() {
        // 保存请求计数
        statisticsSettingService.saveRequestCount(ReqInfoContext.getReqInfo().getClientIp());
        return "使用从库： 更新成功";
    }

    @GetMapping(path = "ds/write")
    public String write2() {
        log.info("---------业务逻辑进入---------------");
        Long old = statisticsSettingService.getStatisticCount().getPvCount();
        DsSelectExecutor.execute(MasterSlaveDsEnum.MASTER, () -> statisticsSettingService.saveRequestCount(ReqInfoContext.getReqInfo().getClientIp()));
        // 保存请求计数
        Long n = statisticsSettingService.getStatisticCount().getPvCount();
        log.info("-----------业务逻辑结束------");
        return "编程式切换主库： 更新成功！ old=" + old + " new=" + n;
    }
}
