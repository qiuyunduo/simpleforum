package com.qyd.api.model.enums;

/**
 * webSocket 连接 状态
 *
 * @author 邱运铎
 * @date 2024-05-10 19:34
 */
public enum WsConnectStateEnum {
    // 初始化
    INIT,
    // 连接中
    CONNECTING,
    // 已连接
    CONNECTED,
    // 连接失败
    FAILED,
    // 已关闭
    CLOSED,
    ;
}
