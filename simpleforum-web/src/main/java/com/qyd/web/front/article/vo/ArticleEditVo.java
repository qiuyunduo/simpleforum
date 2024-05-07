package com.qyd.web.front.article.vo;

import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.api.model.vo.article.dto.TagDTO;
import lombok.Data;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-04 19:40
 */
@Data
public class ArticleEditVo {

    private ArticleDTO article;

    private List<CategoryDTO> categories;

    private List<TagDTO> tags;
}
