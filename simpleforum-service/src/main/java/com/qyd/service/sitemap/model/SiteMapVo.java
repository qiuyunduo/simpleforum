package com.qyd.service.sitemap.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-25 14:09
 */
@Data
@JacksonXmlRootElement(localName = "urlset", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
public class SiteMapVo {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:news")
    private String xmlnsNews = "http://www.google.com/schemas/sitemap-news/0.9";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xhtml")
    private String xmlnsXhtml = "http://www.w3.org/1999/xhtml";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:image")
    private String xmlnsImage = "http://www.google.com/schemas/sitemap-image/1.1";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:video")
    private String xmlnsVideo = "http://www.google.com/schemas/sitemap-video/1.1";

    /**
     * 将列表数据直接转为url标签xml节点，useWrapping = false 表示不要外围标签名
     * 例如： 如果为 true 且设置该列表标签名为 urls 转为xml格式是
     * <urls><url></url>...<url><url/><urls/>
     * 如果为false
     * <url><url/>...<url></url>
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "url")
    private List<SiteUrlVo> url;

    public SiteMapVo() {
        url = new ArrayList<>();
    }

    public void addUrl(SiteUrlVo xmlUrl) {
        url.add(xmlUrl);
    }
}
