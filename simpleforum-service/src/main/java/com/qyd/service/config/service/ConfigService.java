package com.qyd.service.config.service;

import com.qyd.api.model.enums.ConfigTypeEnum;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;

import java.util.List;

/**
 * Banner 前台接口
 *
 * @author 邱运铎
 * @date 2024-04-22 16:38
 */
public interface ConfigService {

    /**
     * 获取banner列表
     * 目前认为是获取一些资源标签的小图标
     *
     * @param configTypeEnum
     * @return
     */
    List<ConfigDTO> getConfigList(ConfigTypeEnum configTypeEnum);

    /**
     * 累计访问某标签类资源的次数 每次计数+1
     * 目前主要是针对pdf电子书资源
     *
     * @param configId
     * @param extra
     */
    void updateVisited(Long configId, String extra);
}
