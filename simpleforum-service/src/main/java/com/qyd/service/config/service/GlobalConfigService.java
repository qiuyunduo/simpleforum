package com.qyd.service.config.service;

import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.config.GlobalConfigReq;
import com.qyd.api.model.vo.config.SearchGlobalConfigReq;
import com.qyd.api.model.vo.config.dto.GlobalConfigDTO;

/**
 * @author 邱运铎
 * @date 2024-05-19 18:50
 */
public interface GlobalConfigService {

    PageVo<GlobalConfigDTO> getList(SearchGlobalConfigReq req);

    void save(GlobalConfigReq req);

    void delete(Long id);

    /**
     * 添加敏感白名单
     *
     * @param word
     */
    void addSensitiveWhiteWord(String word);
}
