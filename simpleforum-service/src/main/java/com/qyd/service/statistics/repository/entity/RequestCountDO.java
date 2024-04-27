package com.qyd.service.statistics.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 请求计数表
 *
 * @author 邱运铎
 * @date 2024-04-27 21:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("request_count")
public class RequestCountDO extends BaseDO {
    private static final long serialVersionUID = -4300285038228299664L;

    /**
     * 机器IP
     */
    private String host;

    /**
     * 访问计数
     */
    private Integer cnt;

    /**
     * 当前日期
     */
    private Date date;
}
