package com.qyd.service.article.repository.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.DocumentTypeEnum;
import com.qyd.api.model.enums.OfficialStatEnum;
import com.qyd.api.model.enums.PushStatusEnum;
import com.qyd.api.model.enums.YesOrNoEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ArticleAdminDTO;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.article.dto.YearArticleDTO;
import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import com.qyd.core.permission.UserRole;
import com.qyd.service.article.conveter.ArticleConverter;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.ArticleDetailDO;
import com.qyd.service.article.repository.entity.ReadCountDO;
import com.qyd.service.article.repository.mapper.ArticleDetailMapper;
import com.qyd.service.article.repository.mapper.ArticleMapper;
import com.qyd.service.article.repository.mapper.ReadCountMapper;
import com.qyd.service.article.repository.params.SearchArticleParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章相关DB操作
 * 多表结构的操作封装，只与DB操作相关
 *
 * @author 邱运铎
 * @date 2024-04-10 22:51
 */
@Repository
public class ArticleDao extends ServiceImpl<ArticleMapper, ArticleDO> {
    @Resource
    private ArticleDetailMapper articleDetailMapper;

    @Resource
    private ReadCountMapper readCountMapper;

    @Resource
    private ArticleMapper articleMapper;

    /**
     * 查询文章详情
     *
     * @param articleId
     * @return
     */
    public ArticleDTO queryArticleDetail(Long articleId) {
        //查询文章记录
        ArticleDO article = baseMapper.selectById(articleId);
        if (article == null || Objects.equals(article.getDeleted(), YesOrNoEnum.YES.getCode())) {
            return  null;
        }

        //查询文章正文
        ArticleDTO dto = ArticleConverter.toDto(article);
        if (showReviewContent(article)) {
            ArticleDetailDO detail = findLatestDetail(articleId);
            dto.setContent(detail.getContent());
        } else {
            //对于审核中的文章，只有作者本人才能看到原文
            dto.setContent("### 文章审核中，请稍后再看");
        }
        return dto;
    }

    private boolean showReviewContent(ArticleDO article) {
        if (article.getStatus() != PushStatusEnum.REVIEW.getCode()) {
            return true;
        }

        BaseUserInfoDTO user = ReqInfoContext.getReqInfo().getUser();
        if (user == null) {
            return false;
        }

        //作者本人和admin超管可以看到审核内容
        return user.getUserId().equals(article.getUserId()) ||
                (user.getRole() != null &&
                        user.getRole().equalsIgnoreCase(UserRole.ADMIN.name()));
    }

    //-------------------- article content ----------------------

    private ArticleDetailDO findLatestDetail(long articleId) {
        //查询文章内容
        LambdaQueryWrapper<ArticleDetailDO> contentQuery = Wrappers.lambdaQuery();
        contentQuery.eq(ArticleDetailDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDetailDO::getArticleId, articleId)
                .orderByDesc(ArticleDetailDO::getVersion);
        return articleDetailMapper.selectList(contentQuery).get(0);
    }

    /**
     * 保存文章正文
     *
     * @param articleId
     * @param content
     * @return
     */
    public Long saveArticleContent(Long articleId, String content) {
        ArticleDetailDO detail = new ArticleDetailDO();
        detail.setArticleId(articleId);
        detail.setContent(content);
        detail.setVersion(1L);
        articleDetailMapper.insert(detail);
        return detail.getId();
    }

    /**
     * 更正文章正文
     *
     * @param articleId
     * @param content
     * @param update  true 表示更新最后一条记录， false 表示新插入一个新的记录
     */
    public void updateArticleContent(Long articleId, String content, boolean update) {
        if (update) {
            articleDetailMapper.updateContent(articleId, content);
        } else {
            ArticleDetailDO latest = findLatestDetail(articleId);
            latest.setVersion(latest.getVersion() + 1);
            latest.setId(null);
            latest.setContent(content);
            articleDetailMapper.insert(latest);
        }
    }

    //-------------------文章列表查询-----------------------------
    public List<ArticleDO> listArticlesByUserId(Long userId, PageParam pageParam) {
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getUserId, userId)
                .last(PageParam.getLimitSql(pageParam))
                .orderByDesc(ArticleDO::getId);
        if (!Objects.equals(ReqInfoContext.getReqInfo().getUserId(), userId)) {
            //作者本人，可以查看草稿，审核，上线文章；其他用户只能查案上线的文章
            query.eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode());
        }
        return baseMapper.selectList(query);
    }

    public List<ArticleDO> listArticlesByCategoryId(Long categoryId, PageParam pageParam) {
        if (categoryId != null && categoryId <= 0) {
            //分类不存在时，表示查询所有
            categoryId = null;
        }

        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode());

        // 如果分页中置顶的四条数据，需要加上官方的查询条件
        // 说明是查询官方的文章， 非置顶的文章，只限制全部分类
        if (categoryId == null && pageParam.getPageSize() == PageParam.TOP_PAGE_SIZE) {
            query.eq(ArticleDO::getOfficialStat, OfficialStatEnum.OFFICIAL.getCode());
        }

        Optional.ofNullable(categoryId).ifPresent(cid -> query.eq(ArticleDO::getCategoryId, cid));
        query.last(PageParam.getLimitSql(pageParam))
                .orderByDesc(ArticleDO::getToppingStat, ArticleDO::getCreateTime);

        return baseMapper.selectList(query);
    }

    @Deprecated
    public Long countArticleByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(ArticleDO::getCategoryId, categoryId);
        return baseMapper.selectCount(query);
    }

    /**
     * 根据文章分类统计文章总数
     *
     * @return key: category_id, value: count
     */
    public Map<Long, Long> countArticleByCategoryId() {
        QueryWrapper<ArticleDO> query = Wrappers.query();
        query.select("category_id, count(*) as cnt")
                .eq("deleted", YesOrNoEnum.NO.getCode())
                .eq("status", PushStatusEnum.ONLINE.getCode())
                .groupBy("category_id");

        List<Map<String, Object>> mapsList = baseMapper.selectMaps(query);
        Map<Long, Long> result = Maps.newHashMapWithExpectedSize(mapsList.size());
        for (Map<String, Object> mp : mapsList) {
            Long cnt = (Long) mp.get("cnt");
            if (cnt != null && cnt > 0) {
                result.put((Long) mp.get("category_id"), cnt);
            }
        }
        return  result;
    }

    public List<ArticleDO> listArticlesByBySearchKey(String key, PageParam pageParam) {
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .and(!StringUtils.isEmpty(key),
                        v -> v.like(ArticleDO::getTitle, key)
                                .or()
                                .like(ArticleDO::getShortTitle, key)
                                .or()
                                .like(ArticleDO::getSummary, key));
        query.last(PageParam.getLimitSql(pageParam))
                .orderByDesc(ArticleDO::getId);
        return baseMapper.selectList(query);
    }

    /**
     * 通过关键词从标题中找出相似的进行推荐，返回主键 + 标题 + 短标题
     * 应用的地方： 在首页搜索文章时候，通过输入的关键词实时显示匹配数据
     * @param key
     * @return
     */
    public List<ArticleDO> listSimpleArticlesByBySearchKey(String key) {
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .and(!StringUtils.isEmpty(key),
                        v -> v.like(ArticleDO::getTitle, key)
                                .or()
                                .like(ArticleDO::getShortTitle, key)
                );
        query.select(ArticleDO::getId, ArticleDO::getTitle, ArticleDO::getShortTitle)
                .last("limit 10")
                .orderByDesc(ArticleDO::getId);
        return baseMapper.selectList(query);
    }

    /**
     * 文章阅读计数
     *
     * @param articleId
     * @return
     */
    public int incrReadCount(Long articleId) {
        LambdaQueryWrapper<ReadCountDO> query = Wrappers.lambdaQuery();
        query.eq(ReadCountDO::getDocumentId, articleId)
                .eq(ReadCountDO::getDocumentType, DocumentTypeEnum.ARTICLE.getCode());
        ReadCountDO record = readCountMapper.selectOne(query);
        if (record == null) {
            record = new ReadCountDO()
                    .setDocumentId(articleId)
                    .setDocumentType(DocumentTypeEnum.ARTICLE.getCode())
                    .setCnt(1);
            readCountMapper.insert(record);
        } else {
            // fixme: 这里存在并发覆盖问题，推荐使用 update read_count set cnt = cnt + 1 where id = xxx
            // 需要直接使用sql 语句 cnt = cnt + 1 来保证数据安全
            record.setCnt(record.getCnt() + 1);
            readCountMapper.updateById(record);
        }
        return record.getCnt();
    }

    /**
     * 统计用户的文章计数
     * @param userId
     * @return
     */
    public int countArticleByUser(Long userId) {
        return lambdaQuery().eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count()
                .intValue();
    }

    /**
     * 热门文章推荐， 适用于首页的侧边栏
     *
     * @param pageParam
     * @return
     */
    public List<SimpleArticleDTO> listHotArticles(PageParam pageParam) {
        return baseMapper.listArticlesByReadCounts(pageParam);
    }

    /**
     * 作者的热门文章推荐， 适用于作者的详情页侧边栏
     *
     * @param userId
     * @param pageParam
     * @return
     */
    public List<SimpleArticleDTO> listAuthorHotArticles(long userId, PageParam pageParam) {
        return baseMapper.listArticlesByUserIdOrderByReadCounts(userId, pageParam);
    }

    /**
     * 根据相同的类目 + 标签 进行推荐
     * 我的理解：应用场景，打开一个文章后侧边的推荐文章就是通过这个方法来的
     *
     * @param categoryId
     * @param tagIds
     * @param pageParam
     * @return
     */
    public List<ArticleDO> listRelatedArticlesOrderByReadCount(Long categoryId, List<Long> tagIds, PageParam pageParam) {
        List<ReadCountDO> list = baseMapper.listArticleByCategoryAndTags(categoryId, tagIds, pageParam);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<Long> ids = list.stream()
                .map(ReadCountDO::getDocumentId)
                .collect(Collectors.toList());

        List<ArticleDO> result = baseMapper.selectBatchIds(ids);
        result.sort((o1, o2) -> {
            int i1 = ids.indexOf(o1.getId());
            int i2 = ids.indexOf(o2.getId());
            return Integer.compare(i1, i2);
        });

        return result;
    }

    /**
     * 根据用户ID获取创作历程 年份 + 该年创作文章数量
     *
     * @param userId
     * @return
     */
    public List<YearArticleDTO> listYearArticleByUserId(Long userId) {
        return baseMapper.listYearArticleByUserId(userId);
    }

    /**
     * 抽取样板代码
     * 目前没有看到有用到该方法的地方，
     * 但看代码是对searchArticleParams查询文章参数的处理形成一个处理链交给调用者
     * 让调用者在此基础上可以再加上一些自己的查询条件
     */
    private LambdaQueryChainWrapper<ArticleDO> buildQuery(SearchArticleParams searchArticleParams) {
        return lambdaQuery()
                .like(StringUtils.isNotBlank(searchArticleParams.getTitle()), ArticleDO::getTitle, searchArticleParams.getTitle())
                //ID不为空
                .eq(Objects.nonNull(searchArticleParams.getArticleId()), ArticleDO::getId, searchArticleParams.getArticleId())
                .eq(Objects.nonNull(searchArticleParams.getUserId()), ArticleDO::getUserId, searchArticleParams.getUserId())
                .eq(Objects.nonNull(searchArticleParams.getStatus()) && searchArticleParams.getStatus() != -1, ArticleDO::getStatus, searchArticleParams.getStatus())
                .eq(Objects.nonNull(searchArticleParams.getOfficialStat()) && searchArticleParams.getOfficialStat() != -1, ArticleDO::getOfficialStat, searchArticleParams.getOfficialStat())
                .eq(Objects.nonNull(searchArticleParams.getToppingStat()) && searchArticleParams.getToppingStat() != -1, ArticleDO::getToppingStat, searchArticleParams.getToppingStat())
                .eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode());
    }

    /**
     * 文章列表 （用于后台管理） 支持分页
     *
     * @param params
     * @return
     */
    public List<ArticleAdminDTO> listArticlesByParams(SearchArticleParams params) {
        // todo 这里用articleMapper 还是用 baseMapper好一些，有点疑惑
        return articleMapper.listArticlesByParams(params,
                PageParam.newPageInstance(params.getPageNum(), params.getPageSize()));
    }

    /**
     * 根据查询条件文章总数 （用于后台）
     *
     * @param searchArticleParams
     * @return
     */
    public Long countArticleByParams(SearchArticleParams searchArticleParams) {
        return articleMapper.countArticlesByParams(searchArticleParams);
    }

    /**
     * 平台所有发布的文章总数 (用于后台)
     *
     * @return
     */
    public Long countArticle() {
        return lambdaQuery()
                .eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count();
    }

    public List<ArticleDO> selectByIds(List<Integer> ids) {
        List<ArticleDO> articleDOS = baseMapper.selectBatchIds(ids);
        return articleDOS;
    }
}
