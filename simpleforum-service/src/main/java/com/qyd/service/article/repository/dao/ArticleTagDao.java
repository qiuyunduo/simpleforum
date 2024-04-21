package com.qyd.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyd.api.model.enums.YesOrNoEnum;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.ArticleTagDO;
import com.qyd.service.article.repository.mapper.ArticleTagMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 文章标签映射mapper接口
 *
 * @author 邱运铎
 * @date 2024-04-15 22:49
 */
@Repository
public class ArticleTagDao extends ServiceImpl<ArticleTagMapper, ArticleTagDO> {
    /**
     * 批量保存
     *
     * @param articleId
     * @param tags
     */
    public void batchSave(Long articleId, Collection<Long> tags) {
        List<ArticleTagDO> insertList = new ArrayList<>(tags.size());
        tags.forEach(tagId -> {
            ArticleTagDO tag = new ArticleTagDO();
            tag.setTagId(tagId);
            tag.setArticleId(articleId);
            tag.setDeleted(YesOrNoEnum.NO.getCode());
            insertList.add(tag);
        });
        saveBatch(insertList);
    }

    /**
     * 更新文章标签
     * 1. 原来有， 新的没有： 则删除旧的
     * 2. 原来有， 新的有： 不做操作
     * 3，原来没有， 新的有： 插入
     *
     * @param articleId
     * @param newTags
     */
    public void updateTags(Long articleId, Set<Long> newTags) {
        List<ArticleTagDO> dbTags = listArticleTags(articleId);
        // 遍历旧的标签集，将不在新的标签集合里面的旧标签设置为删除
        List<Long> toDeleted = new ArrayList<>();
        dbTags.forEach(tag -> {
            if (!newTags.contains(tag.getTagId())) {
                toDeleted.add(tag.getId());
            } else {
                // 旧标签集合已存在该标签，移除新标签集合中的该标签
                newTags.remove(tag.getTagId());
            }
        });
        if (!toDeleted.isEmpty()) {
            baseMapper.deleteBatchIds(toDeleted);
        }
        if (!newTags.isEmpty()) {
            batchSave(articleId, newTags);
        }

    }

    /**
     * 查询文章标签
     *
     * @param articleId
     * @return
     */
    public List<TagDTO> queryArticleTagDetails(Long articleId) {
        return baseMapper.listArticleTagDetails(articleId);
    }

    public List<ArticleTagDO> listArticleTags(@Param("articleId") Long articleId) {
        return lambdaQuery()
                .eq(ArticleTagDO::getArticleId, articleId)
                .eq(ArticleTagDO::getDeleted, YesOrNoEnum.NO.getCode())
                .list();
    }
}
