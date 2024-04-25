package com.qyd.web.global;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.vo.article.dto.ColumnArticlesDTO;
import com.qyd.api.model.vo.article.dto.ColumnDTO;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.api.model.vo.seo.Seo;
import com.qyd.api.model.vo.seo.SeoTagVo;
import com.qyd.core.util.DateUtil;
import com.qyd.web.config.GlobalViewConfig;
import com.qyd.web.front.article.vo.ArticleDetailVo;
import com.qyd.web.front.user.vo.UserHomeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网站内容更好的被搜索引擎抓取
 * seo注入服务，下面这几个页面使用seo
 * - 首页
 * - 文章详情页
 * - 用户主页
 * - 专栏内容详情页
 * <p>
 * ogp seo 标签： <a href="https://ogp.me">开放内容协议</a>
 *
 * @author 邱运铎
 * @date 2024-04-25 10:56
 */
@Service
public class SeoInjectService {
    private static final String KEYWORDS = "Simple,开源社区,java,springboot,IT,程序员,开发者,mysql,redis,Java基础,多线程,JVM,虚拟机,数据库,MySQL,Spring,Redis,MyBatis,系统设计,分布式,RPC,高可用,高并发,海之子";
    private static final String DES = "Simple,一个基于 Spring Boot、MyBatis-Plus、MySQL、Redis、ElasticSearch、MongoDB、Docker、RabbitMQ 等技术栈实现的社区系统，采用主流的互联网技术架构、全新的UI设计、支持一键源码部署，拥有完整的文章&教程发布/搜索/评论/统计流程等，代码完全开源，没有任何二次封装，是一个非常适合二次开发/实战的现代化社区项目。学编程，就上Simple";

    @Resource
    private GlobalViewConfig globalViewConfig;

    /**
     * 文章详情页的seo标签
     *
     * @param detail
     */
    public void initArticleSeo(ArticleDetailVo detail) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        String title = detail.getArticle().getTitle();
        String description = detail.getArticle().getSummary();
        String authorName = detail.getAuthor().getUserName();
        String updateTime = DateUtil.time2LocalTime(detail.getArticle().getLastUpdateTime()).toString();
        String publishedTime = DateUtil.time2LocalTime(detail.getArticle().getCreateTime()).toString();
        String image = detail.getArticle().getCover();
        String tag = detail.getArticle().getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","));
        String category = detail.getArticle().getCategory().getCategory();

        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", description));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale","zh-CN"));
        list.add(new SeoTagVo("og:updated_time", updateTime));

        list.add(new SeoTagVo("article:modified_time", updateTime));
        list.add(new SeoTagVo("article:published_time", publishedTime));
        list.add(new SeoTagVo("article:tag", tag));
        list.add(new SeoTagVo("article:section", category));
        list.add(new SeoTagVo("article:author", authorName));

        list.add(new SeoTagVo("author", authorName));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", description));
        list.add(new SeoTagVo("keywords", category + "," + tag));

        if (StringUtils.isNotBlank(image)) {
            list.add(new SeoTagVo("og:image", image));
            jsonLd.put("image", image);
        }

        jsonLd.put("headLine", title);
        jsonLd.put("description", description);
        Map<String, Object> author = new HashMap<>();
        author.put("@type", "Person");
        author.put("name", authorName);
        jsonLd.put("author", author);
        jsonLd.put("dateModified", updateTime);
        jsonLd.put("datePublished", publishedTime);

        ReqInfoContext.getReqInfo().setSeo(seo);
    }

    /**
     * 教程详情Seo标签
     * 
     * @param detail
     * @param column
     */
    public void initColumnSeo(ColumnArticlesDTO detail, ColumnDTO column) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        String title = detail.getArticle().getTitle();
        String description = detail.getArticle().getSummary();
        String authorName = column.getAuthorName();
        String updateTime = DateUtil.time2LocalTime(detail.getArticle().getLastUpdateTime()).toString();
        String publishedTime = DateUtil.time2LocalTime(detail.getArticle().getCreateTime()).toString();
        String image = column.getCover();

        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", description));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));

        list.add(new SeoTagVo("og:updated_time", updateTime));
        list.add(new SeoTagVo("og:image", image));

        list.add(new SeoTagVo("article:modified_time", updateTime));
        list.add(new SeoTagVo("article:published_time", publishedTime));
        list.add(new SeoTagVo("article:tag", detail.getArticle().getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));
        list.add(new SeoTagVo("article:section", column.getColumn()));
        list.add(new SeoTagVo("article:author", authorName));

        list.add(new SeoTagVo("author", authorName));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", detail.getArticle().getSummary()));
        list.add(new SeoTagVo("keywords", detail.getArticle().getCategory().getCategory() + "," + detail.getArticle().getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));


        jsonLd.put("headline", title);
        jsonLd.put("description", description);
        Map<String, Object> author = new HashMap<>();
        author.put("@type", "Person");
        author.put("name", authorName);
        jsonLd.put("author", author);
        jsonLd.put("dateModified", updateTime);
        jsonLd.put("datePublished", publishedTime);
        jsonLd.put("image", image);
        
        if (ReqInfoContext.getReqInfo() != null) ReqInfoContext.getReqInfo().setSeo(seo);
    }

    /**
     * 用户个人主页的seo标签
     *
     * @param user
     */
    public void initUserSeo(UserHomeVo user) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        String title = "技术派 | " + user.getUserHome().getUserName() + " 的主页";
        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", user.getUserHome().getProfile()));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));

        list.add(new SeoTagVo("article:tag", "后端,前端,Java,Spring,计算机"));
        list.add(new SeoTagVo("article:section", "主页"));
        list.add(new SeoTagVo("article:author", user.getUserHome().getUserName()));

        list.add(new SeoTagVo("author", user.getUserHome().getUserName()));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", user.getUserHome().getProfile()));
        list.add(new SeoTagVo("keywords", KEYWORDS));

        jsonLd.put("headline", title);
        jsonLd.put("description", user.getUserHome().getProfile());
        Map<String, Object> author = new HashMap<>();
        author.put("@type", "Person");
        author.put("name", user.getUserHome().getUserName());
        jsonLd.put("author", author);

        if (ReqInfoContext.getReqInfo() != null) ReqInfoContext.getReqInfo().setSeo(seo);
    }

    public Seo defaultSeo() {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        list.add(new SeoTagVo("og:title", "Simple Forum"));
        list.add(new SeoTagVo("og:description", DES));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));

        list.add(new SeoTagVo("article:tag", "后端,前端,Java,Spring,计算机"));
        list.add(new SeoTagVo("article:section", "开源社区"));
        list.add(new SeoTagVo("article:author", "Simple Forum"));

        list.add(new SeoTagVo("author", "Simple Forum"));
        list.add(new SeoTagVo("title", "Simple Forum"));
        list.add(new SeoTagVo("description", DES));
        list.add(new SeoTagVo("keywords", KEYWORDS));

        Map<String, Object> jsonLd = seo.getJsonLd();
        jsonLd.put("@context", "https://schema.org");
        jsonLd.put("@type", "Article");
        jsonLd.put("headline", "Simple Forum");
        jsonLd.put("description", DES);

        if (ReqInfoContext.getReqInfo() != null) {
            ReqInfoContext.getReqInfo().setSeo(seo);
        }
        return seo;
    }
    private Seo initBasicSeoTag() {
        List<SeoTagVo> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        /**
         * RequestContextHolder 请求上下文的持有者，
         * 从中可以取到请求对象request
         * RequestContextHolder中的请求对象是在DispatchServlet中对http请求做处理的时候就塞入进入的
         * 存储方式是两个ThreadLocal对象，requestAttributesHolder ， inheritableRequestAttributesHolder（可被子线程继承的request）。
         *
         * RequestContextHolder.getRequestAttributes() 和下面的currentRequestAttributes()方法在没有使用JSF的项目中没有区别 --主要还是使用下面currentRequestAttributes会好一些
         * extra扩展:Java Server Faces（JSF）是一种用于构建用户界面的Java Web应用程序的框架。它提供了一套丰富的标签和组件，使开发者能够轻松地构建交互式和动态的Web应用程序界面。
         *
         *
         */
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = globalViewConfig.getHost() + request.getRequestURI();
        list.add(new SeoTagVo("og:url", url));
        map.put("url", url);
        return Seo.builder().jsonLd(map).ogp(list).build();

    }


}
