package com.qyd.service.config.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyd.api.model.enums.ConfigTypeEnum;
import com.qyd.api.model.enums.PushStatusEnum;
import com.qyd.api.model.enums.YesOrNoEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;
import com.qyd.api.model.vo.config.SearchGlobalConfigReq;
import com.qyd.service.config.converter.ConfigConverter;
import com.qyd.service.config.converter.ConfigStructMapper;
import com.qyd.service.config.repository.entity.GlobalConfigDO;
import com.qyd.service.config.repository.mapper.ConfigMapper;
import com.qyd.service.config.repository.entity.ConfigDO;
import com.qyd.service.config.repository.mapper.GlobalConfigMapper;
import com.qyd.service.config.repository.parmas.SearchConfigParams;
import com.qyd.service.config.repository.parmas.SearchGlobalConfigParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-22 17:17
 */
@Repository
public class ConfigDao extends ServiceImpl<ConfigMapper, ConfigDO> {

    @Resource
    private GlobalConfigMapper globalConfigMapper;

    /**
     * 根据类型获取配置资源列表 无需分页
     *
     * @param type
     * @return
     */
    public List<ConfigDTO> listConfigByType(Integer type) {
        List<ConfigDO> configDOS = lambdaQuery()
                .eq(ConfigDO::getType, type)
                .eq(ConfigDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByDesc(ConfigDO::getRank)
                .list();
        return ConfigConverter.toDTOList(configDOS);
    }

    private LambdaQueryChainWrapper<ConfigDO> createConfigQuery(SearchConfigParams params) {
        return lambdaQuery()
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .like(StringUtils.isNotBlank(params.getName()), ConfigDO::getName, params.getName())
                .eq(params.getType() != null && params.getType() != -1, ConfigDO::getType, params.getType());
    }

    /**
     * 获取所有的Banner 列表 （分页）
     *
     * @param params
     * @return
     */
    public List<ConfigDTO> listBanner(SearchConfigParams params) {
        List<ConfigDO> configDOS = createConfigQuery(params)
                .orderByDesc(ConfigDO::getUpdateTime)
                .orderByAsc(ConfigDO::getRank)
                .last(PageParam.getLimitSql(PageParam.newPageInstance(params.getPageNum(), params.getPageSize())))
                .list();
        return ConfigStructMapper.INSTANCE.toDTOList(configDOS);
    }

    /**
     * 获取所有配置资源总数（分页）
     *
     * @param params
     * @return
     */
    public Long countConfig(SearchConfigParams params) {
        return createConfigQuery(params).count();
    }

    /**
     * 获取所有公告列表 （分页）
     *
     * @param pageParam
     * @return
     */
    public List<ConfigDTO> listNotice(PageParam pageParam) {
        List<ConfigDO> configDOS = lambdaQuery()
                .eq(ConfigDO::getType, ConfigTypeEnum.NOTICE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByDesc(ConfigDO::getCreateTime)
                .last(PageParam.getLimitSql(pageParam))
                .list();
        return ConfigConverter.toDTOList(configDOS);
    }

    /**
     * 获取所有公告总数（分页）
     *
     * @return
     */
    public Integer countNotice() {
        return lambdaQuery()
                .eq(ConfigDO::getType, ConfigTypeEnum.NOTICE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count()
                .intValue();
    }

    public void updatePdfConfigVisitNum(long configId, String extra) {
        lambdaUpdate().set(ConfigDO::getExtra, extra)
                .eq(ConfigDO::getId, configId)
                .update();
    }

    public List<GlobalConfigDO> listGlobalConfig(SearchGlobalConfigParams params) {
        LambdaQueryWrapper<GlobalConfigDO> query = buildQuery(params);
        query.select(GlobalConfigDO::getId,
                GlobalConfigDO::getKey,
                GlobalConfigDO::getValue,
                GlobalConfigDO::getComment);
        return globalConfigMapper.selectList(query);
    }

    public Long countGlobalConfig(SearchGlobalConfigParams params) {
        return globalConfigMapper.selectCount(buildQuery(params));
    }

    private LambdaQueryWrapper<GlobalConfigDO> buildQuery(SearchGlobalConfigParams params) {
        LambdaQueryWrapper<GlobalConfigDO> query = Wrappers.lambdaQuery();
        query.and(!StringUtils.isEmpty(params.getKey()),
                k -> k.like(GlobalConfigDO::getKey, params.getKey()))
                .and(!StringUtils.isEmpty(params.getValue()),
                        v -> v.like(GlobalConfigDO::getValue, params.getValue()))
                .and(!StringUtils.isEmpty(params.getComment()),
                        c -> c.like(GlobalConfigDO::getComment, params.getComment()))
                .eq(GlobalConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByDesc(GlobalConfigDO::getUpdateTime);
        return query;
    }

    private LambdaQueryWrapper<GlobalConfigDO> buildQuery() {
        LambdaQueryWrapper<GlobalConfigDO> query = Wrappers.lambdaQuery();
        query.select(GlobalConfigDO::getId,
                GlobalConfigDO::getKey,
                GlobalConfigDO::getValue,
                GlobalConfigDO::getComment)
                .eq(GlobalConfigDO::getDeleted, YesOrNoEnum.NO.getCode());
        return query;
    }

    public void save(GlobalConfigDO globalConfigDO) {
        globalConfigMapper.insert(globalConfigDO);
    }

    public void updateById(GlobalConfigDO globalConfigDO) {
        globalConfigDO.setUpdateTime(new Date());
        globalConfigMapper.updateById(globalConfigDO);
    }

    public void delete(GlobalConfigDO globalConfigDO) {
        globalConfigDO.setDeleted(YesOrNoEnum.YES.getCode());
        globalConfigMapper.updateById(globalConfigDO);
    }

    /**
     * 根据id 查询全局配置
     *
     * @param id
     * @return
     */
    public GlobalConfigDO getGlobalConfigById(Long id) {
        return globalConfigMapper.selectOne(buildQuery().
                eq(GlobalConfigDO::getId, id));
    }

    /**
     * 根据 key 查询全局配置
     *
     * @param key
     * @return
     */
    public GlobalConfigDO getGlobalConfigByKey(String key) {
        return globalConfigMapper.selectOne(buildQuery().
                eq(GlobalConfigDO::getKey, key));
    }
}
