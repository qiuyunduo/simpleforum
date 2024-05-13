package com.qyd.service.user.service.whitelist;

import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import com.qyd.core.cache.RedisClient;
import com.qyd.service.user.service.AuthorWhiteListService;
import com.qyd.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author 邱运铎
 * @date 2024-05-13 15:50
 */
@Service
public class AuthorWhiteListServiceImpl implements AuthorWhiteListService {

    /**
     * 使用 redis - set 结构来存储允许直接发文章的白名单
     */
    private static final String ARTICLE_WHITE_LIST = "auth_article_white_list";

    @Autowired
    private UserService userService;

    @Override
    public boolean authorInArticleWhiteList(Long authorId) {
        return RedisClient.sIsMember(ARTICLE_WHITE_LIST, authorId);
    }

    /**
     * 获取所有的白名单用户
     *
     * @return
     */
    @Override
    public List<BaseUserInfoDTO> queryAllArticleWhiteAuthors() {
        Set<Long> users = RedisClient.sGetAll(ARTICLE_WHITE_LIST, Long.class);
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<BaseUserInfoDTO> userInfo = userService.batchQueryBasicUserInfo(users);
        return userInfo;
    }

    @Override
    public void addAuthor2ArticleWhiteList(Long userId) {
        RedisClient.sPut(ARTICLE_WHITE_LIST, userId);
    }

    @Override
    public void removeAuthorFromArticleWhiteList(Long userId) {
        RedisClient.sDel(ARTICLE_WHITE_LIST, userId);
    }
}
