package com.qyd.service.article.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.service.article.repository.entity.TagDO;

/**
 * 标签mapper接口
 * mybatis-plus提供的基础查询方法足以满足使用需求，不必额外编写其他sql查询
 *
 * @author 邱运铎
 * @date 2024-05-04 20:00
 */
public interface TagMapper extends BaseMapper<TagDO> {
}
