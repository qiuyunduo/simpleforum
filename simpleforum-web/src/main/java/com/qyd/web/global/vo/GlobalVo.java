package com.qyd.web.global.vo;

import com.qyd.api.model.vo.seo.SeoTagVo;
import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import com.qyd.service.sitemap.model.SiteCntVo;
import com.qyd.web.config.GlobalViewConfig;
import lombok.Data;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-26 16:59
 */
@Data
public class GlobalVo {

    /**
     * 网站相关配置
     */
    private GlobalViewConfig siteInfo;

    /**
     * 总的站点统计信息
     */
    private SiteCntVo siteStatisticInfo;

    /**
     * 今日的站点统计信息
     */
    private SiteCntVo todaySiteStatisticInfo;

    /**
     * 环境
     */
    private String env;

    /**
     * 是否已登录
     */
    private Boolean isLogin;

    /**
     * 登录用户信息
     */
    private BaseUserInfoDTO user;

    /**
     * 未读消息通知数量
     */
    private Integer msgNum;

    /**
     * 在线用户人数
     */
    private Integer onlineCnt;

    /**
     * 当前业务领域
     * 目前只有三个：
     * 文章-article, 教程-column, 派聪明-chat
     */
    private String currentDomain;

    private List<SeoTagVo> ogp;

    private String jsonLd;
}