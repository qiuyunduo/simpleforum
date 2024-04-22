package com.qyd.service.config.service.impl;

import com.qyd.api.model.enums.ConfigTypeEnum;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;
import com.qyd.service.config.repository.dao.ConfigDao;
import com.qyd.service.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Banner前台接口
 *
 * @author 邱运铎
 * @date 2024-04-22 17:16
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigDao configDao;

    @Override
    public List<ConfigDTO> getConfigList(ConfigTypeEnum configTypeEnum) {
        return configDao.listConfigByType(configTypeEnum.getCode());
    }

    /**
     * 配置发生变更之后，失效本地缓存，这里主要是配合 SidebarServiceImpl 中的缓存使用
     *
     * @param configId
     * @param extra
     */
    @Override
    public void updateVisited(Long configId, String extra) {
        configDao.updatePdfConfigVisitNum(configId, extra);
    }
}
