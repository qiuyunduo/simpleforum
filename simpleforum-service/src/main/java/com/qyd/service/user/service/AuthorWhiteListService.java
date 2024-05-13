package com.qyd.service.user.service;

import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-13 15:38
 */
public interface AuthorWhiteListService {

    /**
     * 判断作者是否在文章发布的白名单中
     * 该白名单主要用于控制作者发文章后是否需要进行审核
     *
     * @param authorId
     * @return
     */
    boolean authorInArticleWhiteList(Long authorId);

    /**
     * 获取所有的白名单用户
     *
     * @return
     */
    List<BaseUserInfoDTO> queryAllArticleWhiteAuthors();

    /**
     * 将用户添加到白名单中
     *
     * @param userId
     */
    void addAuthor2ArticleWhiteList(Long userId);

    /**
     * 将用户从白名单中移除
     *
     * @param userId
     */
    void removeAuthorFromArticleWhiteList(Long userId);
}
