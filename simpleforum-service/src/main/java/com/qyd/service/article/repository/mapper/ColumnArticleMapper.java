package com.qyd.service.article.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.article.dto.ColumnArticleDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-27 22:38
 */
public interface ColumnArticleMapper extends BaseMapper<ColumnArticleDO> {

    /**
     * 查询教程内的文章列表
     *
     * @param columnId
     * @return
     */
    List<SimpleArticleDTO> listColumnArticles(@Param("columnId") Long columnId);

    /**
     * 根据教程id和排序编号查询教程内指定排序的文章
     *
     * @param columnId
     * @param section
     * @return
     */
    ColumnArticleDO getColumnArticle(@Param("columnId") Long columnId,
                                       @Param("section") Integer section);

    /**
     * 统计专栏的阅读人数
     *
     * @param columnId
     * @return
     */
    Long countColumnReadUserNums(@Param("columnId") Long columnId);

    /**
     * 根据教程ID, 文章标题，查询文章列表
     * 对应的场景应该是在教程内模糊查询文章
     *
     * @param columnId
     * @param articleTitle
     * @param pageParam
     * @return
     */
    List<ColumnArticleDTO> listColumnArticlesByColumnIdArticleName(@Param("columnId") Long columnId,
                                                                   @Param("articleTitle") String articleTitle,
                                                                   @Param("pageParam") PageParam pageParam);

    Long countColumnArticlesByColumnIdArticleName(@Param("columnId") Long columnId,
                                                  @Param("articleTitle") String articleTitle);
    /**
     * 查询教程中section最大的文章即最新更新
     *
     * @param columnId
     * @return  教程内吴文章时，返回0
     */
    @Select("select ifnull(max(section), 0) from column_article where column_id = #{columnId}")
    int selectMaxSection(@Param("columnId") Long columnId);
}
