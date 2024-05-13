package com.qyd.core.autoconf;

import com.qyd.api.model.event.ConfigRefreshEvent;
import com.qyd.core.autoconf.property.SpringValueRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 配置刷新事件监听
 *
 * @author 邱运铎
 * @date 2024-05-13 13:02
 */
@Service
public class ConfigRefreshEventListener implements ApplicationListener<ConfigRefreshEvent> {
    @Resource
    private DynamicConfigContainer dynamicConfigContainer;

    /**
     * 监听配置变更事件
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ConfigRefreshEvent event) {
        dynamicConfigContainer.reloadConfig();
        SpringValueRegistry.updateValue(event.getKey());
    }
}
