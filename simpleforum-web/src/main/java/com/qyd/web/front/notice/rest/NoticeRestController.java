package com.qyd.web.front.notice.rest;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.exception.ExceptionUtil;
import com.qyd.api.model.vo.NextPageHtmlVo;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.notify.dto.NotifyMsgDTO;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.notify.service.NotifyService;
import com.qyd.web.component.TemplateEngineHelper;
import com.qyd.web.front.notice.vo.NoticeResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 消息通知
 *
 * @author 邱运铎
 * @date 2024-05-09 16:56
 */
@RestController
@Permission(role = UserRole.LOGIN)
@RequestMapping(path = "notice/api")
public class NoticeRestController {
    @Resource
    private TemplateEngineHelper templateEngineHelper;

    @Autowired
    private NotifyService notifyService;

    private PageListVo<NotifyMsgDTO> listItems(String type, Long page, Long pageSize) {
        NotifyTypeEnum typeEnum = NotifyTypeEnum.typeOf(type);
        if (typeEnum == null) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "非法type: " + type);
        }
        pageSize = Optional.ofNullable(pageSize).orElse(PageParam.DEFAULT_PAGE_SIZE);
        return notifyService.queryUserNotices(ReqInfoContext.getReqInfo().getUserId(),
                typeEnum, PageParam.newPageInstance(page, pageSize));
    }

    /**
     * 消息通知列表，用于前后端分离的场景，目前暂时用Knife4j来测试
     *
     * @param type
     * @see NotifyTypeEnum
     * @return
     */
    @RequestMapping(path = "list")
    public ResVo<PageListVo<NotifyMsgDTO>> list(@RequestParam(name = "type") String type,
                                                @RequestParam(name = "page") Long page,
                                                @RequestParam(name = "pageSize", required = false) Long pageSize) {
        return ResVo.ok(listItems(type, page, pageSize));
    }

    /**
     * 返回渲染好的分页信息
     *
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(path = "items")
    public ResVo<NextPageHtmlVo> listForView(@RequestParam(name = "type") String type,
                                             @RequestParam(name = "page") Long page,
                                             @RequestParam(name = "pageSize", required = false) Long pageSize) {
        type = type.toLowerCase().trim();
        PageListVo<NotifyMsgDTO> list = listItems(type, page, pageSize);
        NoticeResVo vo = new NoticeResVo();
        vo.setList(list);
        vo.setSelectType(type);
        String html = templateEngineHelper.render("views/notice/tab/notify-" + type, vo);
        return ResVo.ok(new NextPageHtmlVo(html, list.getHasMore()));
    }
}
