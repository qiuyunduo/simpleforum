package com.qyd.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.repository.mapper.ColumnArticleMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-27 23:51
 */
@Repository
public class ColumnArticleDao extends ServiceImpl<ColumnArticleMapper, ColumnArticleDO> {
//    作者 这里的注入冗余了，直接使用 baseMapper 即可
//    @Resource
//    private ColumnArticleMapper columnArticleMapper;

    /**
     * 返回专栏最大更新章节数
     *
     * @param columnId
     * @return  专栏内无文章时，返回0，否则返回当前最新章节数
     */
    public int selectMaxSection(Long columnId) {
        return baseMapper.selectMaxSection(columnId);
    }

    /**
     * 根据文章id, 查询文章所属的专栏信息
     * fixme: 如果一片文章在多个专栏内，就会有问题
     *
     * @param articleId
     * @return
     */
    public ColumnArticleDO selectColumnArticleByArticleId(Long articleId) {
        List<ColumnArticleDO> list = lambdaQuery()
                .eq(ColumnArticleDO::getArticleId, articleId)
                .list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public ColumnArticleDO selectBySection(Long columnId, Integer sort) {
        return lambdaQuery()
                .eq(ColumnArticleDO::getColumnId, columnId)
                .eq(ColumnArticleDO::getSection, sort)
                .one();
    }
}
