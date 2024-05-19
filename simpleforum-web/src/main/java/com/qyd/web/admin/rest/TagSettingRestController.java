package com.qyd.web.admin.rest;

import com.qyd.api.model.enums.PushStatusEnum;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.article.SearchTagReq;
import com.qyd.api.model.vo.article.TagReq;
import com.qyd.api.model.vo.article.dto.TagDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.article.service.TagSettingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 标签后台
 *
 * @author 邱运铎
 * @date 2024-05-19 19:36
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "文章标签管理控制器", tags = "标签管理")
@RequestMapping(path = {"api/admin/tag/", "admin/tag/"})
public class TagSettingRestController {
    @Autowired
    private TagSettingService tagSettingService;

    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody TagReq req) {
        tagSettingService.saveTag(req);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "delete")
    public ResVo<String> delete(@RequestParam(name = "tagId") Integer tagId) {
        tagSettingService.deleteTag(tagId);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "operate")
    public ResVo<String> operate(@RequestParam(name = "tagId") Integer tagId,
                                 @RequestParam(name = "pushStatus") Integer pushStatus) {
        if (pushStatus != PushStatusEnum.OFFLINE.getCode() && pushStatus != PushStatusEnum.ONLINE.getCode()) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS);
        }
        tagSettingService.operateTag(tagId, pushStatus);
        return ResVo.ok("ok");
    }

    @PostMapping(path = "list")
    public ResVo<PageVo<TagDTO>> list(@RequestBody SearchTagReq req) {
        PageVo<TagDTO> tagDTOPageVo = tagSettingService.getTagList(req);
        return ResVo.ok(tagDTOPageVo);
    }
}
