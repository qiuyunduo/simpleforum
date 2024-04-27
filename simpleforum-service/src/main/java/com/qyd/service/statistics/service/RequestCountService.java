package com.qyd.service.statistics.service;

import com.qyd.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.qyd.service.statistics.repository.entity.RequestCountDO;

import java.util.List;

/**
 *
 * @author 邱运铎
 * @date 2024-04-27 21:37
 */
public interface RequestCountService {

    RequestCountDO getRequestCount(String host);

    void insert(String host);

    void incrementCount(Long id);

    Long getPvTotalCount();

    List<StatisticsDayDTO> getPvUvDatList(Integer day);
}
