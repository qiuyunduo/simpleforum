package com.qyd.web.front.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.qyd.service.sitemap.model.SiteMapVo;
import com.qyd.service.sitemap.service.SitemapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.Charset;

/**
 * 生成sitemap.xml
 *
 * @author 邱运铎
 * @date 2024-05-04 19:00
 */
@RestController
public class SiteMapController {

    private XmlMapper xmlMapper = new XmlMapper();

    @Resource
    private SitemapService sitemapService;

    @RequestMapping(path = "/sitemap",
            produces = "application/xml;charset=utf-8")
    public SiteMapVo siteMap() {
        return sitemapService.getSiteMap();
    }

    @RequestMapping(path = "/sitemap.xml", produces = "text/xml")
    public byte[] siteMapXml() throws JsonProcessingException {
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        SiteMapVo vo = sitemapService.getSiteMap();
        String ans = xmlMapper.writeValueAsString(vo);
        ans = ans.replaceAll("xmlns=\"\"", "");

        return ans.getBytes(Charset.defaultCharset());
    }

    @GetMapping(path = "/sitemap/refresh")
    public Boolean refresh() {
        sitemapService.refreshSiteMap();
        return true;
    }
}
