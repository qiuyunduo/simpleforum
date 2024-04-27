package com.qyd.api.model.context;

import com.qyd.api.model.vo.seo.Seo;
import com.qyd.api.model.vo.user.dto.BaseUserInfoDTO;
import lombok.Data;
import java.security.Principal;

/**
 * 请求上下文，携带用户身份相关信息
 *
 * @author 邱运铎
 * @date 2024-04-11 0:49
 */
public class ReqInfoContext {
    private static ThreadLocal<ReqInfo> context = new InheritableThreadLocal<>();

    public static void addReqInfo(ReqInfo reqInfo) {
        context.set(reqInfo);
    }

    public static void clear() {
        context.remove();
    }

    public static ReqInfo getReqInfo() {
        return context.get();
    }

    @Data
    public static class ReqInfo implements Principal {
        /**
         * appkey
         */
        private String appKey;

        /**
         * 访问的域名
         */
        private String host;

        /**
         * 访问路径
         */
        private String path;

        /**
         * 客户端IP
         */
        private String clientIp;

        /**
         * referer 引用 -- 不清楚该字段含义
         */
        private String referer;

        /**
         * post 表单参数
         */
        private String payload;

        /**
         * 设备信息
         */
        private String userAgent;

        /**
         * 登录的会话
         */
        private String session;

        /**
         * 用户Id
         */
        private Long userId;

        /**
         * 用户基础信息
         */
        private BaseUserInfoDTO user;

        /**
         * 消息数量
         */
        private Integer msgNum;

        private Seo seo;

        private String deviceId;


        @Override
        public String getName() {
            return null;
        }
    }
}
