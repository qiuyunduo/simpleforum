package com.qyd.service.image.oss.impl;

import com.qyd.core.config.ImageProperties;
import com.qyd.core.net.HttpRequestHelper;
import com.qyd.core.util.JsonUtil;
import com.qyd.core.util.StopWatchUtil;
import com.qyd.service.image.oss.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于http的文件上传
 *
 * @author 邱运铎
 * @date 2024-05-05 22:02
 */
@Slf4j
@Component
@ConditionalOnExpression(value = "#{'rest'.equals(environment.getProperty('image.oss.type'))}")
public class RestOssWrapper implements ImageUploader {
    @Autowired
    private ImageProperties imageProperties;

    @Override
    public String upload(InputStream input, String fileType) {
        StopWatchUtil stopWatchUtil = StopWatchUtil.init("图片上传");
        try {
            byte[] bytes = stopWatchUtil.record("转字节", () -> StreamUtils.copyToByteArray(input));
            String res = stopWatchUtil.record("上传", () -> HttpRequestHelper.upload(imageProperties.getOss().getEndPoint(), "image", "img." + fileType, bytes));
            HashMap<?, ?> map = JsonUtil.toObj(res, HashMap.class);
            return (String) ((Map<?, ?>) map.get("result")).get("imagePath");
        } catch (Exception e) {
            log.error("upload image error response! uri: {}", imageProperties.getOss().getEndPoint(), e);
            return null;
        } finally {
            // 在需要只在debug环境下打印一些比较耗时的数据，加一层isDebugEnabled判断会好很多
            // 但如果只是简单的 log.debug("error")就没必要了。
            // 因为如果是 log.debug("{}", userMessage) userMessage始终会被调用，只是在最后判断是否应该输出，
            // 如果userMessage很耗时，会影响系统性能，所以需要加层判断。
            if (log.isDebugEnabled()) {
                log.debug("upload image cost: {}", stopWatchUtil.prettyPrint());
            }
        }
    }

    @Override
    public boolean uploadIgnore(String fileUrl) {
        if (StringUtils.isNotBlank(imageProperties.getOss().getHost()) && fileUrl.startsWith(imageProperties.getOss().getHost())) {
            return true;
        }
        return !fileUrl.startsWith("http");
    }
}
