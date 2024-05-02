package com.qyd.web.global;

import com.qyd.api.model.vo.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 全局属性配置
 *
 * @author 邱运铎
 * @date 2024-05-01 23:18
 */
public class BaseViewController {

    @Autowired
    protected GlobalInitService globalInitService;

    public PageParam buildPageParams(Long page, Long pageSize) {
        if (page <= 0) {
            page = PageParam.DEFAULT_PAGE_NUM;
        }
        if (pageSize == null || pageSize > PageParam.DEFAULT_PAGE_SIZE) {
            pageSize = PageParam.DEFAULT_PAGE_SIZE;
        }
        return PageParam.newPageInstance(page, pageSize);
    }

    /**
     * fixme: 推荐使用下面这种方法替代 GlobalViewInterceptor 中的全局属性设置。
     * 全局属性配置
     * 下面 @ModelAttribute注解作用：
     * 保证该注解的方法会在controller类中的请求方法执行前调用执行
     *
     * @param model
     */
    @ModelAttribute
    public void globalAttr(Model model) {
        model.addAttribute("global", globalInitService.globalAttr());
    }
}
