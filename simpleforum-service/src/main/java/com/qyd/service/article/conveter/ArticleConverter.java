package com.qyd.service.article.conveter;

import com.qyd.api.model.enums.ArticleTypeEnum;
import com.qyd.api.model.enums.SourceTypeEnum;
import com.qyd.api.model.enums.YesOrNoEnum;
import com.qyd.api.model.vo.article.ArticlePostReq;
import com.qyd.api.model.vo.article.CategoryReq;
import com.qyd.api.model.vo.article.SearchArticleReq;
import com.qyd.api.model.vo.article.TagReq;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.service.article.repository.entity.ArticleDO;
import com.qyd.service.article.repository.entity.CategoryDO;
import com.qyd.service.article.repository.entity.TagDO;
import com.qyd.service.article.repository.params.SearchArticleParams;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章转换
 *
 * @author 邱运铎
 * @date 2024-04-09 17:19
 */
public class ArticleConverter {

    public static ArticleDO toArticleDO(ArticlePostReq req, Long author) {
        ArticleDO article = new ArticleDO();
        //设置作者ID
        article.setUserId(author);
        article.setId(req.getArticleId());
        article.setTitle(req.getTitle());
        article.setShortTitle(req.getShortTitle());
        article.setArticleType(ArticleTypeEnum.valueOf(req.getArticleType().toUpperCase()).getCode());
        article.setPicture(req.getCover() == null ? "" : req.getCover());
        article.setCategoryId(req.getCategoryId());
        article.setSource(req.getSource());
        article.setSourceUrl(req.getSourceUrl());
        article.setSummary(req.getSummary());
        article.setStatus(req.pushStatus().getCode());
        article.setDeleted(req.deleted() ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode());
        return article;
    }

    public static ArticleDTO toDto(ArticleDO articleDO) {
        if (articleDO == null) {
            return null;
        }
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setAuthor(articleDO.getUserId());
        articleDTO.setArticleId(articleDO.getId());
        articleDTO.setArticleType(articleDO.getArticleType());
        articleDTO.setTitle(articleDO.getTitle());
        articleDTO.setShortTitle(articleDO.getShortTitle());
        articleDTO.setSummary(articleDO.getSummary());
        articleDTO.setCover(articleDO.getPicture());
        articleDTO.setSourceType(SourceTypeEnum.fromCode(articleDO.getSource()).getDesc());
        articleDTO.setSourceUrl(articleDO.getSourceUrl());
        articleDTO.setStatus(articleDO.getStatus());
        articleDTO.setCreateTime(articleDO.getCreateTime().getTime());
        articleDTO.setLastUpdateTime(articleDO.getUpdateTime().getTime());
        articleDTO.setOfficialStat(articleDO.getOfficialStat());
        articleDTO.setToppingStat(articleDO.getToppingStat());
        articleDTO.setCreamStat(articleDO.getCreamStat());

        // 设置类目id
        articleDTO.setCategory(new CategoryDTO(articleDO.getCategoryId(), null));
        return articleDTO;
    }

    public static List<ArticleDTO> toArticleDtoList(List<ArticleDO> articleDOS) {
        return articleDOS.stream().map(ArticleConverter::toDto).collect(Collectors.toList());
    }

    /**
     * do转换
     *
     * @param tag
     * @return
     */
    public static TagDTO toDto(TagDO tag) {
        if (tag == null) {
            return null;
        }
        TagDTO dto = new TagDTO();
        dto.setTag(tag.getTagName());
        dto.setTagId(tag.getId());
        dto.setStatus(tag.getStatus());
        return dto;
    }

    public static List<TagDTO> toDtoList(List<TagDO> tags) {
        return tags.stream().map(ArticleConverter::toDto).collect(Collectors.toList());
    }

    public static CategoryDTO toDto(CategoryDO categoryDO) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategory(categoryDO.getCategoryName());
        dto.setCategoryId(categoryDO.getId());
        dto.setRank(categoryDO.getRank());
        dto.setStatus(categoryDO.getStatus());
        dto.setSelected(false);
        return dto;
    }

    public static List<CategoryDTO> toCategoryDtoList(List<CategoryDO> categorys) {
        return categorys.stream().map(ArticleConverter::toDto).collect(Collectors.toList());
    }

    public static TagDO toDO(TagReq tagReq) {
        if (tagReq == null) {
            return null;
        }
        TagDO tagDO = new TagDO();
        tagDO.setTagName(tagReq.getTag());
        return tagDO;
    }

    public static CategoryDO toDO(CategoryReq categoryReq) {
        if (categoryReq == null) {
            return null;
        }
        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setCategoryName(categoryReq.getCategory());
        categoryDO.setRank(categoryReq.getRank());
        return categoryDO;
    }

    public static SearchArticleParams toSearchParams(SearchArticleReq req) {
        if (req == null) {
            return null;
        }
        SearchArticleParams searchArticleParams = new SearchArticleParams();
        searchArticleParams.setTitle(req.getTitle());
        searchArticleParams.setArticleId(req.getArticleId());
        searchArticleParams.setUserId(req.getUserId());
        searchArticleParams.setStatus(req.getStatus());
        searchArticleParams.setOfficialStat(req.getOfficialStat());
        searchArticleParams.setToppingStat(req.getToppingStat());
        searchArticleParams.setPageNum(req.getPageNumber());
        searchArticleParams.setPageSize(req.getPageSize());
        return searchArticleParams;
    }
}
