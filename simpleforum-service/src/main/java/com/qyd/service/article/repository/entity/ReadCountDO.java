package com.qyd.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qyd.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * fixme 访问计数，后续改用redis替换
 *
 * @author 邱运铎
 * @date 2024-04-10 23:09
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("read_count")
public class ReadCountDO extends BaseDO {

    /**
     * 文档Id（文章/评论）
     */
    private Long documentId;

    /**
     * 文档类型： 1-文章，2-评论
     */
    private Integer documentType;

    /**
     * 计数
     */
    private Integer cnt;
}
