package com.qyd.service.statistics.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyd.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.qyd.service.statistics.repository.entity.RequestCountDO;
import com.qyd.service.statistics.repository.mapper.RequestCountMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-27 21:44
 */
@Repository
public class RequestCountDao extends ServiceImpl<RequestCountMapper, RequestCountDO> {

    public Long getPvTotalCount() {
        return baseMapper.getPvTotalCount();
    }

    /**
     * 获取保存的请求计数
     *
     * @param host
     * @param date
     * @return
     */
    public RequestCountDO getRequestCount(String host, Date date) {
        return lambdaQuery()
                .eq(RequestCountDO::getHost, host)
                .eq(RequestCountDO::getDate, date)
                .one();
    }

    /**
     * 获取 PV UV 数据列表
     *
     * @param day
     * @return
     */
    public List<StatisticsDayDTO> getPvUvDayList(Integer day) {
        return baseMapper.getPvUvDayList(day);
    }

    public void incrementCount(Long id) {
        baseMapper.incrementCount(id);
    }
}
