package com.qyd.service.config.service;

import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.banner.ConfigReq;
import com.qyd.api.model.vo.banner.SearchConfigReq;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;

/**
 * 后台配置管理器
 *
 * @author 邱运铎
 * @date 2024-05-19 18:21
 */
public interface ConfigSettingService {

    /**
     * 保存
     *
     * @param configReq
     */
    void saveConfig(ConfigReq configReq);

    /**
     * 删除
     *
     * @param configId
     */
    void deleteConfig(Integer configId);

    /**
     * 操作（上线/下线）
     *
     * @param configId
     * @param pushStatus
     */
    void operateConfig(Integer configId, Integer pushStatus);

    /**
     * 获取 配置项 列表
     *
     * @param params
     * @return
     */
    PageVo<ConfigDTO> getConfigList(SearchConfigReq params);

    /**
     * 获取公告列表
     *
     * @param pageParam
     * @return
     */
    PageVo<ConfigDTO> getNoticeList(PageParam pageParam);
}
