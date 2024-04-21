package com.qyd.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.user.dto.ArticleFootCountDTO;
import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.qyd.api.model.vo.user.dto.UserFootStatisticDTO;
import com.qyd.service.user.repository.entity.UserFootDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户足迹 mapper 接口
 *
 * @author 邱运铎
 * @date 2024-04-16 0:54
 */
public interface UserFootMapper extends BaseMapper<UserFootDO> {

    /**
     * 查询文章的相关计数信息
     *
     * @param articleId
     * @return
     */
    ArticleFootCountDTO countArticleByArticleId(@Param("articleId") Long articleId);

    /**
     * 查询作者的文章统计
     *
     * @param author
     * @return
     */
    ArticleFootCountDTO countArticleByUserId(@Param("userId") Long author);

    /**
     * 查询作者的所有文章阅读计数
     *
     * @param author
     * @return
     */
    Integer countArticleReadsByUserId(@Param("userId") Long author);

    /**
     * 查询用户收藏的文章列表 支持分页
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<Long> listCollectedArticlesByUserId(@Param("userId") Long userId, @Param("pageParam") PageParam pageParam);

    /**
     * 查询用户阅读的文章列表 支持分页
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<Long> listReadArticleByUserId(@Param("userId") Long userId, @Param("pageParam") PageParam pageParam);

    /**
     * 查询文章/评论的点赞列表
     *
     * @param documentId    文章/评论的ID
     * @param type          文章/评论
     * @param size          限制展示多少条数据
     * @return
     */
    List<SimpleUserInfoDTO> listSimpleUserInfosByArticleId(@Param("documentId") Long documentId,
                                                           @Param("type") Integer type,
                                                           @Param("size") int size);

    UserFootStatisticDTO getFootCount();
}
