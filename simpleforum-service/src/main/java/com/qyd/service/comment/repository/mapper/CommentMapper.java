package com.qyd.service.comment.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.service.comment.repository.entity.CommentDO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 评论mapper接口
 *
 * @author 邱运铎
 * @date 2024-04-18 18:21
 */
public interface CommentMapper extends BaseMapper<CommentDO> {

    /**
     * todo @MapKey, 指定top_comment_id为map对象的key,
     *  这里后面可以看下如果不设置该注解，默认的key是什么
     *  因为作者这里就没有处理这个，也是没有问题的
     *
     *  网上查了下资料，这里不使用@MapKey指定key,则输出 {id=1,name=张三} 这种形式map
     *  否则不加该注解是输出 {张三={id=1, name=张三}} 这种格式，所以差别还是挺大的
     *  所以这里不能加@MapKey注解，改为在intel idea设置中忽略@MapKey is required 错误
     *  因为该错误并不影响程序。
     *
     * @param articleId
     * @return
     */
//    @MapKey("top_comment_id")
    Map<String, Object> getHotTopCommentId(@Param("articleId") Long articleId);
}
