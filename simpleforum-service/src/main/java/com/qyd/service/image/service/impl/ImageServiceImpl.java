package com.qyd.service.image.service.impl;

import com.github.hui.quick.plugin.base.constants.MediaType;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.core.async.AsyncExecute;
import com.qyd.core.async.AsyncUtil;
import com.qyd.core.mdc.MdcDot;
import com.qyd.core.util.MdImgLoader;
import com.qyd.service.image.oss.ImageUploader;
import com.qyd.service.image.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 邱运铎
 * @date 2024-05-05 20:57
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageUploader imageUploader;

    /**
     * 外网图片转存缓存
     */
    private final LoadingCache<String, String> imgReplaceCache = CacheBuilder.newBuilder()
            .maximumSize(300)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String img) throws Exception {
                    try {
                        InputStream stream = FileReadUtil.getStreamByFileName(img);
                        URI uri = URI.create(img);
                        String path = uri.getPath();
                        int index = path.lastIndexOf(".");
                        String fileType = null;
                        if (index > 0) {
                            // 从url中获取文件类型
                            fileType = path.substring(index + 1);
                        }
                        return imageUploader.upload(stream, fileType);
                    } catch (Exception e) {
                        log.error("外网图片转存异常！ img:{}", img, e);
                        return "";
                    }
                }
            });
    @Override
    @MdcDot
    @AsyncExecute(timeOutRsp = "#content")
    public String mdImgReplace(String content) {
        List<MdImgLoader.MdImg> imgList = MdImgLoader.loadImages(content);
        if (CollectionUtils.isEmpty(imgList)) {
            return content;
        }
        if (imgList.size() == 1) {
            // 只有一张图片时，没有必要走异步，直接转存并返回
            MdImgLoader.MdImg img = imgList.get(0);
            String newImg = saveImg(img.getUrl());
            return StringUtils.replace(content, img.getOrigin(), "![" + img.getDesc() + "](" + newImg + ")");
        }

        // 超过一张图片，做并发的图片转存，提升性能
        AsyncUtil.CompletableFutureBridge bridge = AsyncUtil.concurrentExecutor("MdImgReplace");
        Map<MdImgLoader.MdImg, String> imgReplaceMap = Maps.newHashMapWithExpectedSize(imgList.size());
        for (MdImgLoader.MdImg img : imgList) {
            bridge.runAsyncWithTimeRecord(() -> {
                imgReplaceMap.put(img, saveImg(img.getUrl()));
            }, img.getUrl());
        }
        bridge.allExecuted().prettyPrint();

        // 图片替换
        for (Map.Entry<MdImgLoader.MdImg, String> entry : imgReplaceMap.entrySet()) {
            MdImgLoader.MdImg img = entry.getKey();
            String newImg = entry.getValue();
            content = StringUtils.replace(content, img.getOrigin(),"![" + img.getDesc() + "](" + newImg + ")");
        }

        return content;
    }

    /**
     * 外网图片转存
     *
     * @param img
     * @return
     */
    @Override
    public String saveImg(String img) {
        if (imageUploader.uploadIgnore(img)) {
            // 已经转存过， 不需要再次转存：非http图片，不处理
            return img;
        }
        try {
            String ans = imgReplaceCache.get(img);
            if (StringUtils.isBlank(ans)) {
                return buildUploadFailImgUrl(img);
            }
            return ans;
        } catch (Exception e) {
            log.error("外网图片转存异常！ img:{}", img, e);
            return buildUploadFailImgUrl(img);
        }
    }

    @Override
    public String saveImg(HttpServletRequest request) {
        MultipartFile file = null;
        if (request instanceof MultipartHttpServletRequest) {
            file = ((MultipartHttpServletRequest) request).getFile("image");
        }
        if (file == null) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "缺少需要上传的图片");
        }

        // 目前只支持 jpg, png, webp 等静态图片格式
        String fileType = validateStaticImg(file.getContentType());
        if (fileType == null) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "图片只支持png, jpg, gif");
        }

        try {
            return imageUploader.upload(file.getInputStream(), fileType);
        } catch (IOException e) {
            log.error("Parse img from httpRequest to BufferedImage error! e: ", e);
            throw ExceptionUtil.of(StatusEnum.UPLOAD_PIC_FILED);
        }
    }

    private String buildUploadFailImgUrl(String img) {
        return img.contains("saveError") ? img : img + "?&cause=saveError!";
    }

    /**
     * 图片格式校验
     *
     * @param mime
     * @return
     */
    private String validateStaticImg(String mime) {
        if ("svg".equalsIgnoreCase(mime)) {
            // fixme 上传文件保存到服务器本地时，做好安全保护，避免上传了存在攻击性的脚本
            return "svg";
        }

        if (mime.contains(MediaType.ImageJpg.getExt())) {
            mime = mime.replace("ipg", "jpeg");
        }

        for (MediaType type : ImageUploader.STATIC_IMG_TYPE) {
            if (type.getMime().equals(mime)) {
                return type.getExt();
            }
        }
        return null;
    }
}
