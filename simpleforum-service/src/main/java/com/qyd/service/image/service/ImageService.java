package com.qyd.service.image.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 邱运铎
 * @date 2024-05-05 20:54
 */
public interface ImageService {

    /**
     * markdown中上传的图片转存
     *
     * @param content
     * @return
     */
    String mdImgReplace(String content);

    /**
     * 外网图片转存
     *
     * @param img
     * @return
     */
    String saveImg(String img);

    /**
     * 保存图片
     *
     * @param request
     * @return
     */
    String saveImg(HttpServletRequest request);
}
