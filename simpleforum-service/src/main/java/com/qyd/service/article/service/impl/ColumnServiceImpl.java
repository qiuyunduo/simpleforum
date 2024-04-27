package com.qyd.service.article.service.impl;

import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ColumnDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import com.qyd.api.model.vo.user.dto.ColumnFootCountDTO;
import com.qyd.service.article.conveter.ColumnConvert;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.repository.dao.ColumnArticleDao;
import com.qyd.service.article.repository.dao.ColumnDao;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.repository.entity.ColumnInfoDO;
import com.qyd.service.article.service.ColumnService;
import com.qyd.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 邱运铎
 * @date 2024-04-27 22:30
 */
@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnDao columnDao;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ColumnArticleDao columnArticleDao;

    @Autowired
    private UserService userService;

    @Override
    public ColumnArticleDO getColumnArticleRelation(Long articleId) {
        return columnArticleDao.selectColumnArticleByArticleId(articleId);
    }

    /**
     * 专栏列表
     *
     * @param pageParam
     * @return
     */
    @Override
    public PageListVo<ColumnDTO> listColumn(PageParam pageParam) {
        List<ColumnInfoDO> columnList = columnDao.listOnlineColumns(pageParam);
        List<ColumnDTO> result = columnList.stream()
                .map(this::buildColumnInfo)
                .collect(Collectors.toList());
        return PageListVo.newVo(result, pageParam.getPageSize());
    }

    public ColumnDTO buildColumnInfo(ColumnInfoDO info) {
        return buildColumnInfo(ColumnConvert.toDTO(info));
    }

    /**
     * 构建专栏详情信息
     *
     * @param dto
     * @return
     */
    public ColumnDTO buildColumnInfo(ColumnDTO dto) {
        // 补齐专栏对应的用户信息
        BaseUserInfoDTO user = userService.queryBasicUserInfo(dto.getAuthor());
        dto.setAuthorName(user.getUserName());
        dto.setAuthorAvatar(user.getPhoto());
        dto.setAuthorProfile(user.getProfile());

        // 统计计数
        ColumnFootCountDTO countDTO = new ColumnFootCountDTO();
        // 更新文章数
        countDTO.setArticleCount(columnDao.countColumnArticles(dto.getColumnId()));
        // 专栏阅读人数
        countDTO.setReadCount(columnDao.countColumnReadPeople(dto.getColumnId()));
        // 总的章节数
        countDTO.setTotalNums(dto.getNums());
        dto.setCount(countDTO);
        return dto;
    }

    @Override
    public ColumnArticleDO queryColumnArticle(long columnId, Integer section) {
        ColumnArticleDO article = columnDao.getColumnArticleId(columnId, section);
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, section);
        }
        return article;
    }

    @Override
    public ColumnDTO queryBasicColumnInfo(Long columnId) {
        // 查找专栏信息
        ColumnInfoDO column = columnDao.getById(columnId);
        if (column == null) {
            throw ExceptionUtil.of(StatusEnum.COLUMN_NOT_EXISTS, columnId);
        }
        return ColumnConvert.toDTO(column);
    }

    @Override
    public ColumnDTO queryColumInfo(Long columnId) {
        return buildColumnInfo(queryBasicColumnInfo(columnId));
    }

    @Override
    public List<SimpleArticleDTO> queryColumnArticles(Long columnId) {
        return columnDao.listColumnArticles(columnId);
    }

    @Override
    public Long getTutorialCount() {
        return columnDao.countColumnArticles();
    }
}
