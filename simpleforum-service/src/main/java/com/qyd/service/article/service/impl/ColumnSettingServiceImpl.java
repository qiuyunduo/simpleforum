package com.qyd.service.article.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.*;
import com.qyd.api.model.vo.article.dto.ColumnArticleDTO;
import com.qyd.api.model.vo.article.dto.ColumnDTO;
import com.qyd.api.model.vo.article.dto.SimpleColumnDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import com.qyd.core.util.NumUtil;
import com.qyd.service.article.conveter.ColumnArticleStructMapper;
import com.qyd.service.article.conveter.ColumnStructMapper;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.repository.dao.ColumnArticleDao;
import com.qyd.service.article.repository.dao.ColumnDao;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.repository.entity.ColumnInfoDO;
import com.qyd.service.article.repository.params.SearchColumnArticleParams;
import com.qyd.service.article.repository.params.SearchColumnParams;
import com.qyd.service.article.service.ColumnSettingService;
import com.qyd.service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 专栏后台接口
 *
 * @author 邱运铎
 * @date 2024-05-05 18:52
 */
@Service
@RequiredArgsConstructor
public class ColumnSettingServiceImpl implements ColumnSettingService {
    private final UserService userService;
    private final ColumnArticleDao columnArticleDao;
    private final ColumnDao columnDao;
    private final ArticleDao articleDao;
    private final ColumnStructMapper columnStructMapper;

    /**
     * 将文章保存到对应的专栏
     *
     * @param articleId
     * @param columnId
     */
    @Override
    public void saveColumnArticle(Long articleId, Long columnId) {
        // 转换参数
        // 插入的时候，需要判断是否已经存在
        ColumnArticleDO exit = columnArticleDao.getOne(Wrappers.<ColumnArticleDO>lambdaQuery()
                .eq(ColumnArticleDO::getArticleId, articleId));
        if (exit != null) {
            if (!Objects.equals(columnId, exit.getColumnId())) {
                // 更新
                exit.setColumnId(columnId);
                columnArticleDao.updateById(exit);
            }
        } else {
            // 将文章保存到专栏中，章节序列号 +1
            ColumnArticleDO columnArticleDO = new ColumnArticleDO();
            columnArticleDO.setColumnId(columnId);
            columnArticleDO.setArticleId(articleId);
            // section 自增 +1
            int maxSection = columnArticleDao.selectMaxSection(columnId);
            columnArticleDO.setSection(maxSection + 1);
            columnArticleDao.save(columnArticleDO);
        }
    }

    @Override
    public void saveColumn(ColumnReq columnReq) {
        ColumnInfoDO columnInfoDO = columnStructMapper.toDo(columnReq);
        if (NumUtil.nullOrZero(columnReq.getColumnId())) {
            columnDao.save(columnInfoDO);
        } else {
            columnInfoDO.setId(columnReq.getColumnId());
            columnDao.updateById(columnInfoDO);
        }
    }

    /**
     *
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveColumnArticle(ColumnArticleReq req) {
        // 转换参数
        ColumnArticleDO columnArticleDO = ColumnArticleStructMapper.INSTANCE.repToDO(req);
        if (NumUtil.nullOrZero(columnArticleDO.getId())) {
            // 插入的时候，需要判断是否已经存在
            ColumnArticleDO exist = columnArticleDao.getOne(Wrappers.<ColumnArticleDO>lambdaQuery()
                    .eq(ColumnArticleDO::getColumnId, columnArticleDO.getColumnId())
                    .eq(ColumnArticleDO::getArticleId, columnArticleDO.getArticleId()));
            if (exist != null) {
                throw ExceptionUtil.of(StatusEnum.COLUMN_ARTICLE_EXISTS, "请勿重复添加");
            }

            // section 自增 +1
            int maxSection = columnArticleDao.selectMaxSection(columnArticleDO.getColumnId());
            columnArticleDO.setSection(maxSection + 1);
            columnArticleDao.save(columnArticleDO);
        } else {
            columnArticleDao.updateById(columnArticleDO);
        }

        // 同时更新article的shortTitle短标题
        if (req.getShortTitle() != null) {
            ArticleDO articleDO = new ArticleDO();
            articleDO.setShortTitle(req.getShortTitle());
            articleDO.setId(req.getArticleId());
            articleDao.updateById(articleDO);
        }
    }

    @Override
    public void deleteColumn(Long columnId) {
        columnDao.deleteColumn(columnId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteColumnArticle(Long id) {
        ColumnArticleDO columnArticleDO = columnArticleDao.getById(id);
        if (columnArticleDO != null) {
            columnArticleDao.removeById(id);
            // 删除的时候，批量更新section 比如原来是1,2,3,4,5,6,7 删除 5 ， 那么 5 后面的都要 - 1
            columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                    .setSql("section = section - 1")
                    .eq(ColumnArticleDO::getColumnId, columnArticleDO.getColumnId())
                    // section 大于 1
                    .gt(ColumnArticleDO::getSection, 1)
                    .gt(ColumnArticleDO::getSection, columnArticleDO.getSection()));
        }
    }

    @Override
    public List<SimpleColumnDTO> listSimpleColumnBySearchKey(String key) {
        LambdaQueryWrapper<ColumnInfoDO> query = Wrappers.lambdaQuery();
        query.select(ColumnInfoDO::getId, ColumnInfoDO::getColumnName, ColumnInfoDO::getCover)
                .and(!StringUtils.isEmpty(key),
                        v -> v.like(ColumnInfoDO::getColumnName, key))
                .orderByDesc(ColumnInfoDO::getId);
        List<ColumnInfoDO> columns = columnDao.list(query);
        return columnStructMapper.infoToSimpleDTOS(columns);
    }

    @Override
    public PageVo<ColumnDTO> getColumnList(SearchColumnReq req) {
        SearchColumnParams params = columnStructMapper.reqToSearchParams(req);
        List<ColumnInfoDO> columnInfoList = columnDao.listColumnsByParams(params, PageParam.newPageInstance(req.getPageNumber(), req.getPageSize()));
        List<ColumnDTO> columnDTOList = columnStructMapper.infoToDTOS(columnInfoList);
        if (CollUtil.isNotEmpty(columnDTOList)) {
            List<Long> userIds = columnDTOList.stream()
                    .map(ColumnDTO::getAuthor)
                    .collect(Collectors.toList());

            List<BaseUserInfoDTO> users = userService.batchQueryBasicUserInfo(userIds);

            // Function.identity() 等价于lambda表达式  t -> t，下面的就是将userid作为map的key,user对象自身作为map的value
            Map<Long, BaseUserInfoDTO> userMap = users.stream().collect(Collectors.toMap(BaseUserInfoDTO::getId, Function.identity()));

            columnDTOList.forEach(columnDTO -> {
                BaseUserInfoDTO user = userMap.get(columnDTO.getAuthor());
                columnDTO.setAuthorName(user.getUserName());
                columnDTO.setAuthorAvatar(user.getPhoto());
                columnDTO.setAuthorProfile(user.getProfile());
            });
        }
        Integer totalCount = columnDao.countColumnByParams(params);
        return PageVo.build(columnDTOList, req.getPageSize(), req.getPageNumber(), totalCount);
    }

    @Override
    public PageVo<ColumnArticleDTO> getColumnArticleList(SearchColumnArticleReq req) {
        SearchColumnArticleParams params = ColumnArticleStructMapper.INSTANCE.toSearchParams(req);
        List<ColumnArticleDTO> simpleArticleDTOS = columnDao.listColumnArticlesDetail(params, PageParam.newPageInstance(req.getPageNumber(), req.getPageSize()));
        Integer totalCount = columnDao.countColumnArticles(params);
        return PageVo.build(simpleArticleDTOS, req.getPageSize(), req.getPageNumber(), totalCount);
    }

    /**
     * 在专栏文章列表中拖动文章顺序改变文章的排序
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortColumnArticleApi(SortColumnArticleReq req) {
        // 根据 req 的两个 id 调换两篇文章的顺序
        ColumnArticleDO activeDO = columnArticleDao.getById(req.getActiveId());
        ColumnArticleDO overDO = columnArticleDao.getById(req.getOverId());
        if (activeDO != null && overDO != null && !activeDO.getId().equals(overDO.getId())) {
            Integer activeSection = activeDO.getSection();
            Integer overSection = overDO.getSection();
            if (activeSection > overSection) {
                // 1. 如果activeSection > overSection 那么 overSection的section 到 activeSection - 1的文章section 都要 + 1
                // 向上拖动，将active文章的排序提高
                columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                        .setSql("section = section + 1")
                        .eq(ColumnArticleDO::getColumnId, overDO.getColumnId())
                        .ge(ColumnArticleDO::getSection, overSection)
                        .lt(ColumnArticleDO::getSection, activeSection));

                // 将 activeDO 的 section 设置为 overSection
                activeDO.setSection(overSection);
                columnArticleDao.updateById(activeDO);
            } else {
                // 1. 如果activeSection <  overSection 那么 activeSection + 1 到 overSection的section 的文章section 都要 - 1
                // 向下拖动，将active文章排序降低
                columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                        .setSql("section = section - 1") // 将符合条件的记录的 section 字段的值减少 1
                        .eq(ColumnArticleDO::getColumnId, overDO.getColumnId()) // 指定要更新记录的 columnId 条件
                        .gt(ColumnArticleDO::getSection, activeSection) // 指定 section 字段的下限（不包含此值）
                        .le(ColumnArticleDO::getSection, overSection)); // 指定 section 字段的上限（包含此值）

                // 将 activeDO 的 section 设置为 overSection -1
                activeDO.setSection(overSection);
                columnArticleDao.updateById(activeDO);
            }
        }
    }

    /**
     * 将专栏下的指定文章调整顺序（章节）为指定的顺序
     *
     * @param req
     */
    @Override
    @Transactional
    public void sortColumnArticleByIDApi(SortColumnArticleByIDReq req) {
        // 获取要重新排序的专栏文章
        ColumnArticleDO columnArticleDO = columnArticleDao.getById(req.getId());
        if (columnArticleDO == null) {
            throw ExceptionUtil.of(StatusEnum.COLUMN_ARTICLE_EXISTS, "教程不存在");
        }
        // 如果顺序没变
        if (req.getSort().equals(columnArticleDO.getSection())) {
            return;
        }
        // 获取教程可以调整的最大顺序
        int maxSection = columnArticleDao.selectMaxSection(columnArticleDO.getColumnId());
        if (req.getSort() > maxSection) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "顺序超出范围");
        }
        // 查看输入的顺序是否存在
        ColumnArticleDO changeColumnArticleDO = columnArticleDao.selectBySection(columnArticleDO.getColumnId(), req.getSort());
        if (changeColumnArticleDO != null) {
            columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                    .set(ColumnArticleDO::getSection, columnArticleDO.getSection())
                    .eq(ColumnArticleDO::getId, changeColumnArticleDO.getId()));
            columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                    .set(ColumnArticleDO::getSection, changeColumnArticleDO.getSection())
                    .eq(ColumnArticleDO::getId, columnArticleDO.getId()));
        } else {
            // 如果不存在，直接修改顺序
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "输入的顺序不存在，无法完成交换");
        }
    }
}
