package com.qyd.service.article.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyd.api.model.enums.column.ColumnStatusEnum;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ColumnArticleDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.repository.entity.ColumnInfoDO;
import com.qyd.service.article.repository.mapper.ColumnArticleMapper;
import com.qyd.service.article.repository.mapper.ColumnInfoMapper;
import com.qyd.service.article.repository.params.SearchColumnArticleParams;
import com.qyd.service.article.repository.params.SearchColumnParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-27 22:31
 */
@Repository
public class ColumnDao extends ServiceImpl<ColumnInfoMapper, ColumnInfoDO> {

    @Autowired
    private ColumnArticleMapper columnArticleMapper;

    /**
     * 分页查询专栏列表
     *
     * @param pageParam
     * @return
     */
    public List<ColumnInfoDO> listOnlineColumns(PageParam pageParam) {
        LambdaQueryWrapper<ColumnInfoDO> query = Wrappers.lambdaQuery();
        query.gt(ColumnInfoDO::getState, ColumnStatusEnum.OFFLINE.getCode())
                .last(PageParam.getLimitSql(pageParam))
                .orderByDesc(ColumnInfoDO::getSection);
        return baseMapper.selectList(query);
    }

    /**
     * 统计指定专栏的文章数
     *
     * @param columnId
     * @return
     */
    public int countColumnArticles(Long columnId) {
        LambdaQueryWrapper<ColumnArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ColumnArticleDO::getColumnId, columnId);
        return columnArticleMapper.selectCount(query).intValue();
    }

    /**
     * 统计所有专栏文章数
     *
     * @return
     */
    public Long countColumnArticles() {
        return columnArticleMapper.selectCount(Wrappers.emptyWrapper());
    }

    /**
     * 统计专栏的阅读人数
     *
     * @return
     */
    public int countColumnReadPeople(Long columnId) {
        return columnArticleMapper.countColumnReadUserNums(columnId).intValue();
    }

    /**
     * 根据教程id查询文章信息列表
     *
     * @param params
     * @param pageParam
     * @return
     */
    public List<ColumnArticleDTO> listColumnArticlesDetail(SearchColumnArticleParams params,
                                                           PageParam pageParam) {
        return columnArticleMapper.listColumnArticlesByColumnIdArticleName(params.getColumnId(),
                params.getArticleTitle(),
                pageParam);
    }

    public Integer countColumnArticles(SearchColumnArticleParams params) {
        return columnArticleMapper.countColumnArticlesByColumnIdArticleName(params.getColumnId(),
                params.getArticleTitle()).intValue();
    }

    /**
     * 根据教程ID,查询文章id列表
     *
     * @param columnId
     * @return
     */
    public List<SimpleArticleDTO> listColumnArticles(Long columnId) {
        return  columnArticleMapper.listColumnArticles(columnId);
    }

    public ColumnArticleDO getColumnArticleId(Long columnId, Integer section) {
        return columnArticleMapper.getColumnArticle(columnId, section);
    }

    /**
     * 删除专栏
     *
     * fixme 改为逻辑删除
     *
     * @param columnId
     */
    public void deleteColumn(Long columnId) {
        ColumnInfoDO columnInfoDO = baseMapper.selectById(columnId);
        if (columnInfoDO != null) {
            // 如果专栏对应的文章不为空，则不允许删除
            // 统计专栏文章数
            int count = countColumnArticles(columnId);
            if (count > 0) {
                throw ExceptionUtil.of(StatusEnum.COLUMN_ARTICLE_EXISTS, "请先删除教程");
            }

            // 删除专栏
            baseMapper.deleteById(columnId);
        }
    }

    /**
     * 带条件查询专栏
     *
     * @param params
     * @param pageParam
     * @return
     */
    public List<ColumnInfoDO> listColumnsByParams(SearchColumnParams params, PageParam pageParam) {
        LambdaQueryWrapper<ColumnInfoDO> query = Wrappers.lambdaQuery();
        // 加上判空条件
        query.like(StringUtils.isNotBlank(params.getColumn()), ColumnInfoDO::getColumnName, params.getColumn());
        query.last(PageParam.getLimitSql(pageParam))
                .orderByAsc(ColumnInfoDO::getSection)
                .orderByDesc(ColumnInfoDO::getUpdateTime);
        return baseMapper.selectList(query);
    }

    /**
     * 查询专栏总数
     *
     * @param params
     * @return
     */
    public Integer countColumnByParams(SearchColumnParams params) {
        LambdaQueryWrapper<ColumnInfoDO> query = Wrappers.lambdaQuery();
        query.like(StringUtils.isNotBlank(params.getColumn()), ColumnInfoDO::getColumnName, params.getColumn());
        return baseMapper.selectCount(query).intValue();
    }
}
