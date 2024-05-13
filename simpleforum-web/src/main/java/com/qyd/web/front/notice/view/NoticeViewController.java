package com.qyd.web.front.notice.view;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.NotifyTypeEnum;
import com.qyd.api.model.vo.PageParam;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.notify.service.NotifyService;
import com.qyd.web.front.notice.vo.NoticeResVo;
import com.qyd.web.global.BaseViewController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
import java.util.Map;

/**
 * 消息通知
 *
 * @author 邱运铎
 * @date 2024-05-09 16:29
 */
@Controller
@Permission(role = UserRole.LOGIN)
@RequestMapping(path = "notice")
public class NoticeViewController extends BaseViewController {
    private NotifyService notifyService;

    public NoticeViewController(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @RequestMapping(path = {"/{type}", "/"})
    public String list(@PathVariable(name = "type", required = false) String type, Model model) {
        Long loginUserId = ReqInfoContext.getReqInfo().getUserId();
        Map<String, Integer> map = notifyService.queryUnreadCounts(loginUserId);

        NotifyTypeEnum typeEnum = type == null ? null : NotifyTypeEnum.typeOf(type);
        if (typeEnum == null) {
            // 若没有指定查询的消息类别，则找一个存在消息未读数的进行展示
            typeEnum = map.entrySet().stream()
                    .filter(s -> s.getValue() > 0)
                    .map(s -> NotifyTypeEnum.typeOf(s.getKey()))
                    .findAny()
                    .orElse(NotifyTypeEnum.COMMENT);
        }

        NoticeResVo vo = new NoticeResVo();
        vo.setList(notifyService.queryUserNotices(loginUserId, typeEnum, PageParam.newPageInstance()));
        vo.setSelectType(typeEnum.name().toLowerCase());
        vo.setUnreadCountMap(notifyService.queryUnreadCounts(loginUserId));
        model.addAttribute("vo", vo);
        return "views/notice/index";
    }
}
