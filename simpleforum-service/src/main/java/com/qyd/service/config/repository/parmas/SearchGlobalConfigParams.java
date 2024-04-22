package com.qyd.service.config.repository.parmas;

import com.qyd.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 邱运铎
 * @date 2024-04-22 19:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchGlobalConfigParams extends PageParam {
    // 全局配置项键
    private String key;

    // 全局配置项值
    private String value;

    // 配置项备注说明
    private String comment;
}
