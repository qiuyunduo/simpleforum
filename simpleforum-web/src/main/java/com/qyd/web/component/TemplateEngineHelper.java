package com.qyd.web.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * @author 邱运铎
 * @date 2024-04-24 17:56
 */
@Component
public class TemplateEngineHelper {

    @Autowired
    private SpringTemplateEngine springTemplateEngine;


}
