package com.qyd.service.config.service.impl;

import cn.hutool.db.Page;
import com.qyd.api.model.event.ConfigRefreshEvent;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.config.GlobalConfigReq;
import com.qyd.api.model.vo.config.SearchGlobalConfigReq;
import com.qyd.api.model.vo.config.dto.GlobalConfigDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.core.senstive.SensitiveProperty;
import com.qyd.core.senstive.SensitiveService;
import com.qyd.core.util.NumUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.service.config.converter.ConfigStructMapper;
import com.qyd.service.config.repository.dao.ConfigDao;
import com.qyd.service.config.repository.entity.GlobalConfigDO;
import com.qyd.service.config.repository.parmas.SearchGlobalConfigParams;
import com.qyd.service.config.service.GlobalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-19 18:53
 */
@Service
public class GlobalConfigServiceImpl implements GlobalConfigService {

    @Autowired
    private ConfigDao configDao;

    @Override
    public PageVo<GlobalConfigDTO> getList(SearchGlobalConfigReq req) {
        SearchGlobalConfigParams params = ConfigStructMapper.INSTANCE.toSearchGlobalParams(req);
        // 查询
        List<GlobalConfigDO> list = configDao.listGlobalConfig(params);
        // 总数
        Long total = configDao.countGlobalConfig(params);
        return PageVo.build(ConfigStructMapper.INSTANCE.toGlobalDTOList(list), req.getPageSize(), req.getPageNumber(), total);
    }

    @Override
    public void save(GlobalConfigReq req) {
        GlobalConfigDO globalConfigDO = ConfigStructMapper.INSTANCE.toGlobalDO(req);
        // id 不为空
        if(NumUtil.nullOrZero(globalConfigDO.getId())) {
            configDao.save(globalConfigDO);
        } else {
            configDao.updateById(globalConfigDO);
        }

        // 配置更新之后， 主动触动配置的动态加载
        SpringUtil.publishEvent(new ConfigRefreshEvent(this, req.getKeywords(), req.getValue()));
    }

    @Override
    public void delete(Long id) {
        GlobalConfigDO globalConfigDO = configDao.getGlobalConfigById(id);
        if (globalConfigDO != null) {
            configDao.delete(globalConfigDO);
        } else {
            throw ExceptionUtil.of(StatusEnum.RECORDS_NOT_EXISTS, "记录不存在");
        }
    }

    /**
     * 添加敏感词白名单
     *
     * @param word
     */
    @Override
    public void addSensitiveWhiteWord(String word) {
        String key = SensitiveProperty.SENSITIVE_KEY_PREFIX + ".allow";
        GlobalConfigReq req = new GlobalConfigReq();
        req.setKeywords(key);

        GlobalConfigDO config = configDao.getGlobalConfigByKey(key);
        if (config == null) {
            req.setValue(word);
            req.setComment("敏感词白名单");
        } else {
            req.setValue(config.getValue() + "," + word);
            req.setComment(config.getComment());
            req.setId(config.getId());
        }
        // 更新敏感词白名单
        save(req);

        // 移除敏感词记录
        SpringUtil.getBean(SensitiveService.class).removeSensitiveWord(word);
    }
}
