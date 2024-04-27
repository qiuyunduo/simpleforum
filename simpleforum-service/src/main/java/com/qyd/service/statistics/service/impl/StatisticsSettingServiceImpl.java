package com.qyd.service.statistics.service.impl;

import com.qyd.api.model.vo.statistics.dto.StatisticsCountDTO;
import com.qyd.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.qyd.api.model.vo.user.dto.UserFootStatisticDTO;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.ColumnService;
import com.qyd.service.statistics.repository.entity.RequestCountDO;
import com.qyd.service.statistics.service.RequestCountService;
import com.qyd.service.statistics.service.StatisticsSettingService;
import com.qyd.service.user.service.UserFootService;
import com.qyd.service.user.service.UserService;
import com.qyd.service.user.service.conf.AiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据统计后台接口
 *
 * @author 邱运铎
 * @date 2024-04-27 21:35
 */
@Slf4j
@Service
public class StatisticsSettingServiceImpl implements StatisticsSettingService {

    @Autowired
    private RequestCountService requestCountService;

    @Autowired
    private UserService userService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private UserFootService userFootService;

    @Autowired
    private ArticleReadService articleReadService;

    @Resource
    private AiConfig aiConfig;

    @Override
    public void saveRequestCount(String host) {
        RequestCountDO requestCountDO = requestCountService.getRequestCount(host);
        if (requestCountDO == null) {
            requestCountService.insert(host);
        } else {
            // 改为数据库直接更新
            requestCountService.incrementCount(requestCountDO.getId());
        }
    }

    @Override
    public StatisticsCountDTO getStatisticCount() {
        // 从 user_foot 表中查询点赞数，收藏数，留言数，阅读数
        UserFootStatisticDTO userFootStatisticDTO = userFootService.getFootCount();
        if (userFootStatisticDTO == null) {
            userFootStatisticDTO = new UserFootStatisticDTO();
        }

        return StatisticsCountDTO.builder()
                .userCount(userService.getUserCount())
                .articleCount(articleReadService.getArticleCount())
                .pvCount(requestCountService.getPvTotalCount())
                .tutorialCount(columnService.getTutorialCount())
                .commentCount(userFootStatisticDTO.getCommentCount())
                .collectCount(userFootStatisticDTO.getCollectionCount())
                .likeCount(userFootStatisticDTO.getPraiseCount())
                .readCount(userFootStatisticDTO.getReadCount())
                .starPayCount(aiConfig.getMaxNum().getStarNumber())
                .build();
    }

    @Override
    public List<StatisticsDayDTO> getPvUvDayList(Integer day) {
        return requestCountService.getPvUvDatList(day);
    }
}
