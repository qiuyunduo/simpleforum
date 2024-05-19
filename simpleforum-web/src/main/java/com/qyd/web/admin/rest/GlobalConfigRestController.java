package com.qyd.web.admin.rest;

import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.config.GlobalConfigReq;
import com.qyd.api.model.vo.config.SearchGlobalConfigReq;
import com.qyd.api.model.vo.config.dto.GlobalConfigDTO;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.config.service.GlobalConfigService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 全局配置管理控制器
 *
 * @author 邱运铎
 * @date 2024-05-19 18:47
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "全局配置管理控制器", tags = "全局配置")
@RequestMapping(path = {"api/admin/global/config/", "admin/global/config/"})
public class GlobalConfigRestController {
    @Autowired
    private GlobalConfigService globalConfigService;

    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody GlobalConfigReq req) {
        globalConfigService.save(req);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "delete")
    public ResVo<String> delete(@RequestParam(name = "id") Long id) {
        globalConfigService.delete(id);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "list")
    public ResVo<PageVo<GlobalConfigDTO>> list(@RequestBody SearchGlobalConfigReq req) {
        PageVo<GlobalConfigDTO> list = globalConfigService.getList(req);
        return ResVo.ok(list);
    }
}
