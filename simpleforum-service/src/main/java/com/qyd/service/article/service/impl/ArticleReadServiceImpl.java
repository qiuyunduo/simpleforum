package com.qyd.service.article.service.impl;

import com.qyd.api.model.enums.*;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import com.qyd.core.util.ArticleUtil;
import com.qyd.service.article.conveter.ArticleConverter;
import com.qyd.service.article.repository.dao.ArticleDao;
import com.qyd.service.article.repository.dao.ArticleTagDao;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.CategoryService;
import com.qyd.service.constant.EsFieldConstant;
import com.qyd.service.constant.EsIndexConstant;
import com.qyd.service.statistics.service.CountService;
import com.qyd.service.user.repository.entity.UserFootDO;
import com.qyd.service.user.service.UserFootService;
import com.qyd.service.user.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章查询相关服务类
 *
 * @author 邱运铎
 * @date 2024-04-10 22:50
 */
@Service
public class ArticleReadServiceImpl implements ArticleReadService {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleTagDao articleTagDao;

    @Autowired
    private CategoryService categoryService;

    /**
     * 在一个项目中， UserFootService 就是内部服务调用
     * 拆微服务时，这个会作为远程服务访问
     */
    @Autowired
    private UserFootService userFootService;

    @Autowired
    private CountService countService;

    @Autowired
    private UserService userService;

    @Value("${elasticsearch.open}")
    private Boolean openES;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public ArticleDO queryBasicArticle(Long articleId) {
        return articleDao.getById(articleId);
    }

    @Override
    public String generateSummary(String content) {
        return ArticleUtil.pickSummary(content);
    }

    @Override
    public PageVo<TagDTO> queryTagsByArticleId(Long articleId) {
        List<TagDTO> tagDtoS = articleTagDao.queryArticleTagDetails(articleId);
        return PageVo.build(tagDtoS, 1, 10, tagDtoS.size());
    }

    @Override
    public ArticleDTO queryDetailArticleInfo(Long articleId) {
        ArticleDTO article = articleDao.queryArticleDetail(articleId);
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
        }

        // 填充分类相关信息, 分类id -> 分类名 ， 这里映射关系是从本地缓存中获取。不经过数据库
        CategoryDTO category = article.getCategory();
        category.setCategory(categoryService.queryCategoryName(category.getCategoryId()));

        article.setTags(articleTagDao.queryArticleTagDetails(articleId));
        return article;
    }

    /**
     * 查询文章所有的关联信息，正文，分类，标签，阅读计数，当前登录用户是否点赞，评论过
     *
     * @param articleId 文章id
     * @param currentUser 当前查看的用户id
     * @return
     */
    @Override
    public ArticleDTO queryFullArticleInfo(Long articleId, Long currentUser) {
        ArticleDTO article = queryDetailArticleInfo(articleId);

        // 文章阅读计数 + 1
        countService.incrArticleReadCount(article.getAuthor(), articleId);

        // 文章的操作标记
        if (currentUser != null) {
            UserFootDO foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE,
                    articleId,
                    article.getAuthor(),
                    currentUser,
                    OperateTypeEnum.READ);
            article.setPraised(Objects.equals(foot.getPraiseStat(), PraiseStatEnum.PRAISE.getCode()));
            article.setCommented(Objects.equals(foot.getCommentStat(), CommentStatEnum.COMMENT.getCode()));
            article.setCollected(Objects.equals(foot.getCollectionStat(), CollectionStatEnum.COLLECTION.getCode()));
        } else {
            // 未登录， 全部设置为未处理
            article.setPraised(Boolean.FALSE);
            article.setCommented(Boolean.FALSE);
            article.setCollected(Boolean.FALSE);
        }

        // 更新文章统计计数
        article.setCount(countService.queryArticleStatisticInfo(articleId));

        // 设置文章的点赞列表
        article.setPraisedUsers(userFootService.queryArticlePraisedUsers(articleId));
        return article;
    }

    /**
     * 查询文章列表
     *
     * @param categoryId
     * @param page
     * @return
     */
    @Override
    public PageListVo<ArticleDTO> queryArticlesByCategory(Long categoryId, PageParam page) {
        List<ArticleDO> records = articleDao.listArticlesByCategoryId(categoryId, page);
        return buildArticleListVo(records, page.getPageSize());
    }

    /**
     * 查询分类下的置顶文章列表, 分类id为null,查询所有
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<ArticleDTO> queryTopArticlesByCategory(Long categoryId) {
        PageParam pageParam = PageParam.newPageInstance(PageParam.DEFAULT_PAGE_NUM, PageParam.TOP_PAGE_SIZE);
        List<ArticleDO> articleDOS = articleDao.listArticlesByCategoryId(categoryId, pageParam);
        return articleDOS.stream().map(this::fillArticleRelationInfo).collect(Collectors.toList());
    }

    @Override
    @Deprecated
    public Long queryArticleCountByCategory(Long categoryId) {
        return articleDao.countArticleByCategoryId(categoryId);
    }

    @Override
    public Map<Long, Long> queryArticleCountsByCategory() {
        return articleDao.countArticleByCategoryId();
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesByTag(Long tagId, PageParam param) {
        List<ArticleDO> records = articleDao.listRelatedArticlesOrderByReadCount(null, Arrays.asList(tagId), param);
        return buildArticleListVo(records, param.getPageSize());
    }

    @Override
    public List<SimpleArticleDTO> querySimpleArticleBySearchKey(String key) {
        // todo 当key为空时，返回热门推荐
        if (StringUtils.isBlank(key)) {
            return Collections.emptyList();
        }

        key = key.trim();
        if (!openES) {
            List<ArticleDO> records = articleDao.listSimpleArticlesByBySearchKey(key);
            return records.stream().map(s -> new SimpleArticleDTO().setId(s.getId()).setTitle(s.getTitle()))
                    .collect(Collectors.toList());
        }

        // todo es整合
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(key,
                EsFieldConstant.ES_FIELD_TITLE,
                EsFieldConstant.ES_FIELD_SHORT_TITLE);
        searchSourceBuilder.query(multiMatchQueryBuilder);

        SearchRequest searchRequest = new SearchRequest(new String[]{EsIndexConstant.EX_INDEX_ARTICLE},
                searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsList = hits.getHits();
        List<Integer> ids = Arrays.asList(hitsList).stream()
                .map(s -> Integer.parseInt(s.getId()))
                .collect(Collectors.toList());
        if (ObjectUtils.isEmpty(ids)) {
            return null;
        }
        List<ArticleDO> records = articleDao.selectByIds(ids);
        return records.stream()
                .map(s -> new SimpleArticleDTO().setId(s.getId()).setTitle(s.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesBySearchKey(String key, PageParam param) {
        List<ArticleDO> records = articleDao.listArticlesByBySearchKey(key, param);
        return buildArticleListVo(records, param.getPageSize());
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesByUserAndType(Long userId, PageParam param, HomeSelectEnum select) {
        List<ArticleDO> records = null;
        if (select == HomeSelectEnum.ARTICLE) {
            // 用户文章列表
            records = articleDao.listArticlesByUserId(userId, param);
        } else if (select == HomeSelectEnum.READ) {
            List<Long> articleIds = userFootService.queryUserReadArticleList(userId, param);
            records = CollectionUtils.isEmpty(articleIds) ? Collections.emptyList() : articleDao.listByIds(articleIds);
            records = sortByIds(records);
        }

        if (CollectionUtils.isEmpty(records)) {
            return PageListVo.emptyVo();
        }
        return buildArticleListVo(records, param.getPageSize());
    }

    private List<ArticleDO> sortByIds(List<ArticleDO> records) {
        return records.stream()
                .sorted((r1, r2) -> Long.compare(r2.getId(), r1.getId()))
                .collect(Collectors.toList());
    }
    // 实验一下stream中sort传入指定比较规则
    public static void main(String[] args) {
        List<ArticleDO> articles = new ArrayList<>();
        ArticleDO a1 = new ArticleDO();
        a1.setId(12L);
        a1.setTitle("第一个");
        ArticleDO a2 = new ArticleDO();
        a2.setId(10L);
        a2.setTitle("第二个");
        ArticleDO a3 = new ArticleDO();
        a3.setId(11L);
        a3.setTitle("第三个");
        articles.add(a1);
        articles.add(a2);
        articles.add(a3);
        System.out.println(articles);
        System.out.println(articles.get(1).getId());
        articles = articles.stream()
                .sorted((l1, l2) -> Long.compare(l2.getId(), l1.getId()))
                .collect(Collectors.toList());
        System.out.println(articles);
    }


    @Override
    public PageListVo<ArticleDTO> buildArticleListVo(List<ArticleDO> records, long pageSize) {
        List<ArticleDTO> result = records.stream().map(this::fillArticleRelationInfo).collect(Collectors.toList());
        return PageListVo.newVo(result, pageSize);
    }

    /**
     * 补全文章的阅读计数， 作者，分类，标签等信息
     *
     * @param record
     * @return
     */
    private ArticleDTO fillArticleRelationInfo(ArticleDO record) {
        ArticleDTO dto = ArticleConverter.toDto(record);
        // 分类信息
        dto.getCategory().setCategory(categoryService.queryCategoryName(record.getCategoryId()));
        // 标签列表
        dto.setTags(articleTagDao.queryArticleTagDetails(record.getId()));
        // 阅读计数统计
        dto.setCount(countService.queryArticleStatisticInfo(record.getId()));
        // 作者信息
        BaseUserInfoDTO author = userService.queryBasicUserInfo(dto.getAuthor());
        dto.setAuthorName(author.getUserName());
        dto.setAuthorAvatar(author.getPhoto());
        return dto;
    }

    @Override
    public PageListVo<SimpleArticleDTO> queryHotArticlesForRecommend(PageParam param) {
        List<SimpleArticleDTO> list = articleDao.listHotArticles(param);
        return PageListVo.newVo(list, param.getPageSize());
    }

    @Override
    public Long getArticleCount() {
        return articleDao.countArticle();
    }
}
