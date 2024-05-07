package com.qyd.core.net;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 请求工具类
 *
 * @author 邱运铎
 * @date 2024-05-05 22:08
 */
@Slf4j
public class HttpRequestHelper {
    // http请求头中的User-Agent字段携带的浏览器，系统等信息
    public static final String CHROME_UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36";

    /**
     * rest template
     */
    private static LoadingCache<String, RestTemplate> restTemplateMap;

    static {
        restTemplateMap = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, RestTemplate>() {
                    @Override
                    public RestTemplate load(String key) throws Exception {
                        return buildRestTemplate();
                    }
                });
    }

    /**
     * build rest template
     */
    private static RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(15000);
        return new RestTemplate(factory);
    }

    /**
     * 从每天 0 点开始 每隔一小时执行一次缓存维护
     * 这里的cleanup并不是清空缓存，而是基于缓存的实现来对缓存进行维护处理
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public static void refreshRestTemplate() {
        restTemplateMap.cleanUp();
    }

    /**
     * 文件上传
     *
     * @param url       上传url
     * @param paramName 参数名
     * @param fileName  上传的文件名
     * @param bytes     上传文件流
     * @return
     */
    public static String upload(String url, String paramName, String fileName, byte[] bytes) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 设置请求体，注意是LinkedMultiValueMap
        ByteArrayResource fileSystemResource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        LinkedMultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        // post文件
        form.add(paramName, fileSystemResource);

        // 用HttpEntity封装整个请求报文
        HttpEntity<LinkedMultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        String threadName = Thread.currentThread().getName();
        RestTemplate restTemplate = restTemplateMap.getUnchecked(threadName);
        ResponseEntity<String> res = restTemplate.postForEntity(url, files, String.class);
        return res.getBody();
    }

    /**
     * 使用代理进行请求，如果代理不可用，就尝试不用代理
     *
     * @param url
     * @param method
     * @param params
     * @param headers
     * @param responseClass
     * @return
     * @param <R>
     */
    public static <R> R fetchContentWithProxy(String url, HttpMethod method,
                                                 Map<String, String> params,
                                                 HttpHeaders headers,
                                                 Class<R> responseClass) {
        R result = fetchContent(url, method, params, headers, responseClass, true);
        if (result == null) {
            return fetchContent(url, method, params, headers, responseClass, false);
        }

        return result;
    }

    /**
     * 不使用代理进行请求
     *
     * @param url
     * @param method
     * @param params
     * @param headers
     * @param responseClass
     * @return
     * @param <R>
     */
    public static <R> R fetchContentWithoutProxy(String url, HttpMethod method,
                                                 Map<String, String> params,
                                                 HttpHeaders headers,
                                                 Class<R> responseClass) {
        return fetchContent(url, method, params, headers, responseClass, false);
    }

    /**
     * fetch content
     *
     * @param url
     * @param method
     * @param params
     * @param headers
     * @param responseClass
     * @param userProxy
     * @return
     * @param <R>
     */
    private static <R> R fetchContent(String url, HttpMethod method,
                                      Map<String, String> params,
                                      HttpHeaders headers,
                                      Class<R> responseClass,
                                      boolean userProxy) {
        String threadName = Thread.currentThread().getName();
        RestTemplate restTemplate = restTemplateMap.getUnchecked(threadName);

        String host = "";
        try {
            host = new URL(url).getHost();
        } catch (MalformedURLException e) {
            log.error("Failed to parse url: {}", url);
        }

        if (userProxy) {
            ensureProxy(restTemplate, host);
        } else  {
            ensureProxy(restTemplate, "");
        }

        return fetchContentInternal(restTemplate, url, method, params, headers, responseClass);
    }

    /**
     * ensure proxy
     *
     * @param restTemplate
     * @param host
     */
    private static void ensureProxy(RestTemplate restTemplate, String host) {
        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        if (StringUtils.isBlank(host)) {
            factory.setProxy(null);
            return;
        }

        Optional.ofNullable(ProxyCenter.loadProxy(host)).ifPresent(factory::setProxy);
    }

    /**
     * fetch content
     *
     * @param restTemplate
     * @param url
     * @param method
     * @param params
     * @param headers
     * @param responseClass
     * @return
     * @param <R>
     */
    @SuppressWarnings("unchecked")
    private static <R> R fetchContentInternal(RestTemplate restTemplate, String url, HttpMethod method,
                                              Map<String, String> params, HttpHeaders headers, Class<R> responseClass) {
        ResponseEntity<R> responseEntity;

        try {
            SslUtils.ignoreSSL();
            if (method.equals(HttpMethod.POST)) {
                HttpEntity<?> entity = new HttpEntity<>(headers);
                responseEntity = restTemplate.exchange(url, method, entity, responseClass);
            } else {
                MultiValueMap<String, String> args = new LinkedMultiValueMap<>();
                args.setAll(params);
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(args, headers);
                responseEntity = restTemplate.exchange(url, method, entity, responseClass);
            }
        } catch (RestClientResponseException e) {
            String res = e.getResponseBodyAsString();
            // A.isAssignableFrom(B) 判断B 是否为 A 的子类或是否是同一个类
            if (String.class.isAssignableFrom(responseClass)) {
                return (R) res;
            } else if (JSONObject.class.isAssignableFrom(responseClass)) {
                return (R) JSONObject.parseObject(res);
            }
            return null;
        } catch (Exception e) {
            log.warn("Failed to fetch content, url: {}, params: {}, exception: {}", url, params, e.getMessage());
            return null;
        }

        return responseEntity.getBody();
    }

    public static <R> R fetchByRequestBody(String url, Map<String, Object> params, HttpHeaders headers,
                                           Class<R> responseClass) {
        ResponseEntity<R> responseEntity;
        try {
            String threadName = Thread.currentThread().getName();
            RestTemplate restTemplate = restTemplateMap.getUnchecked(threadName);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
            // exchange 通用协议请求方法， 支持GET,POST,DELETE,PUT,POTIONS,PATCH等Http方法
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, responseClass);
        } catch (Exception e) {
            log.warn("Failed to fetch content, url: {}. params: {}. exception: {}", url, params, e.getMessage());
            return null;
        }

        if (responseEntity != null) {
            return responseEntity.getBody();
        }

        return null;
    }
}
