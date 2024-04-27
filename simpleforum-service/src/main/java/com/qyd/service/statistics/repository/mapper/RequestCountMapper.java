package com.qyd.service.statistics.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.qyd.service.statistics.repository.entity.RequestCountDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 请求计数mapper接口
 *
 * @author 邱运铎
 * @date 2024-04-27 21:45
 */
public interface RequestCountMapper extends BaseMapper<RequestCountDO> {

    /**
     * 获取 PV 总数
     *
     * @return
     */
    @Select("select sum(cnt) from request_count)")
    Long getPvTotalCount();

    /**
     * 获取 PV UV 数据列表
     *
     * @param day
     * @return
     */
    List<StatisticsDayDTO> getPvUvDayList(@Param("day") Integer day);

    /**
     * 增加计数
     *
     * @param id
     */
    @Update("update request_count set cnt = cnt + 1 where id = #{id}}")
    void incrementCount(Long id);
}