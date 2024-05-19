package com.qyd.web.admin.rest;

import com.qyd.api.model.enums.PushStatusEnum;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.banner.ConfigReq;
import com.qyd.api.model.vo.banner.SearchConfigReq;
import com.qyd.api.model.vo.banner.dto.ConfigDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.config.service.ConfigSettingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台运营配置管理控制器
 *
 * @author 邱运铎
 * @date 2024-05-19 18:18
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "后台运营配置管理控制器", tags = "配置管理")
@RequestMapping(path = {"api/admin/config/", "admin/config/"})
public class ConfigSettingRestController {
    @Autowired
    private ConfigSettingService configSettingService;

    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody ConfigReq configReq) {
        configSettingService.saveConfig(configReq);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping("delete")
    public ResVo<String> delete(@RequestParam(name = "configId") Integer configId) {
        configSettingService.deleteConfig(configId);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping("operate")
    public ResVo<String> operate(@RequestParam(name = "configId") Integer configId,
                                 @RequestParam(name = "pushStatus") Integer pushStatus) {
        if (pushStatus != PushStatusEnum.OFFLINE.getCode() && pushStatus != PushStatusEnum.ONLINE.getCode()) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS);
        }
        configSettingService.operateConfig(configId, pushStatus);
        return ResVo.ok("ok");
    }

    @PostMapping(path = "list")
    public ResVo<PageVo<ConfigDTO>> list(@RequestBody SearchConfigReq req) {
        PageVo<ConfigDTO> configList = configSettingService.getConfigList(req);
        return ResVo.ok(configList);
    }
}
