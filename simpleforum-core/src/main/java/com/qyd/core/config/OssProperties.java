package com.qyd.core.config;

import lombok.Data;

/**
 * @author 邱运铎
 * @date 2024-05-05 21:20
 */
@Data
public class OssProperties {

    /**
     * 上传文件前缀路径
     */
    private String prefix;

    /**
     * oss类型
     */
    private String type;

    /**
     * 下面几个是oss的配置参数
     */
    private String endPoint;

    private String ak;

    private String sk;

    private String bucket;

    private String host;
}
