package com.qyd.web.admin.rest;

import com.qyd.api.model.enums.UserAIStatEnum;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.user.SearchStarUserReq;
import com.qyd.api.model.vo.user.StarUserBatchOperateReq;
import com.qyd.api.model.vo.user.StarUserPostReq;
import com.qyd.api.model.vo.user.dto.StarUserInfoDTO;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.user.service.StarWhiteListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 知识星球作者白名单服务
 *
 * @author 邱运铎
 * @date 2024-05-19 21:22
 */
@RestController
@Permission(role = UserRole.ADMIN)
@Api(value = "星球用户白名单管理控制器", tags = "星球白名单")
@RequestMapping(path = {"api/admin/zsxq/whitelist"})
public class StarWhiteListController {
    @Autowired
    private StarWhiteListService starWhiteListService;

    @ApiOperation("获取知识星球白名单用户列表")
    @PostMapping(path = "")
    public ResVo<PageVo<StarUserInfoDTO>> list(@RequestBody SearchStarUserReq req) {
        PageVo<StarUserInfoDTO> userInfoDTOPageVo = starWhiteListService.getList(req);
        return ResVo.ok(userInfoDTOPageVo);
    }

    /**
     * 改变用户状态，审核通过
     */
    @ApiOperation("改变用户状态")
    @GetMapping(path = "operate")
    public ResVo<String> operate(@RequestParam(name = "id") Long id,
                                 @RequestParam(name = "status") Integer status) {
        UserAIStatEnum operate = UserAIStatEnum.fromCode(status);
        starWhiteListService.operate(id, operate);
        return ResVo.ok("ok");
    }

    @GetMapping(path = "reset")
    public ResVo<String> reset(@RequestParam(name = "authorId") Integer authorId) {
        starWhiteListService.reset(authorId);
        return ResVo.ok("ok");
    }

    @ApiOperation("批量审核通过")
    @PostMapping(path = "batchOperate")
    public ResVo<String> batchOperate(@RequestBody StarUserBatchOperateReq req) {
        UserAIStatEnum operate = UserAIStatEnum.fromCode(req.getStatus());
        starWhiteListService.batchOperate(req.getIds(), operate);
        return ResVo.ok("ok");
    }

    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody StarUserPostReq req) {
        starWhiteListService.update(req);
        return ResVo.ok("ok");
    }
}
