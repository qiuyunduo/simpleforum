package com.qyd.api.model.vo.seo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * fixme Seo代表什么目前是搜索到时用于网站搜索优化，但怎么个原理不清楚
 * Builder注解 旨在简化Java类的构建过程，建造者模式构建对象
 * 例如 通过：Seo.builder().ogp(new List<SeoTagVo>).build();构建一个Seo对象
 *
 * @author 邱运铎
 * @date 2024-04-11 1:08
 */
@Data
@Builder
public class Seo {
    private List<SeoTagVo> ogp;
    private Map<String, Object> jsonLd;
}
