package com.qyd.api.model.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * 配置变更消息事件
 *
 * @author 邱运铎
 * @date 2024-05-13 13:04
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ConfigRefreshEvent extends ApplicationEvent {
    private String key;
    private String val;

    public ConfigRefreshEvent(Object source, String key, String value) {
        super(source);
        this.key = key;
        this.val = value;
    }
}
