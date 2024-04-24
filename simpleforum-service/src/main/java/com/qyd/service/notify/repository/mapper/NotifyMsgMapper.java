package com.qyd.service.notify.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.notify.dto.NotifyMsgDTO;
import com.qyd.service.notify.repository.entity.NotifyMsgDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-24 18:24
 */
public interface NotifyMsgMapper extends BaseMapper<NotifyMsgDO> {

    /**
     * 查询文章相关的通知列表
     *
     * @param userId
     * @param type
     * @param pageParam 分页
     * @return
     */
    List<NotifyMsgDTO> listArticleRelatedNotices(@Param("userId") long userId,
                                                 @Param("type") int type,
                                                 @Param("pageParam")PageParam pageParam);

    /**
     * 查询关注，系统等没有关联Id的通知列表
     *
     * @param userId
     * @param type
     * @param pageParam
     * @return
     */
    List<NotifyMsgDTO> listNormalNotices(@Param("userId") long userId,
                                         @Param("type") int type,
                                         @Param("pageParam")PageParam pageParam);

    /**
     * 标记消息为已阅读
     *
     * @param ids
     */
    void updateNoticeRead(@Param("ids") List<Long> ids);

}
