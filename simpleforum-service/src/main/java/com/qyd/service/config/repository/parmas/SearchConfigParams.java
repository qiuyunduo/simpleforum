package com.qyd.service.config.repository.parmas;

import com.qyd.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 邱运铎
 * @date 2024-04-22 17:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchConfigParams extends PageParam {

    /**
     * 配置资源类型
     */
    private Integer type;

    /**
     * 配置资源名称
     */
    private String name;
}
