package com.qyd.service.sitemap.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邱运铎
 * @date 2024-04-25 14:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * JacksonXmlRootElement用于bean和xml之间的转换
 * localName 指定xml根元素标签名称，namespace指定xml根元素命名空间名称
 */
@JacksonXmlRootElement(localName = "url")
public class SiteUrlVo {

    //  @JacksonXmlProperty 指定根元素标签下的子标签
    @JacksonXmlProperty(localName = "loc")
    private String loc;

    @JacksonXmlProperty(localName = "lastmod")
    private String lastMode;
}
