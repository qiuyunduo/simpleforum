package com.qyd.web.admin.rest;

import com.qyd.api.model.enums.PushStatusEnum;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.article.CategoryReq;
import com.qyd.api.model.vo.article.SearchCategoryReq;
import com.qyd.api.model.vo.article.dto.CategoryDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.article.service.CategorySettingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类后台
 *
 * @author 邱运铎
 * @date 2024-05-19 16:49
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "文章类目管理控制器", tags = "类目管理")
@RequestMapping(path = {"api/admin/category", "admin/category"})
public class CategorySettingRestController {
    @Autowired
    private CategorySettingService categorySettingService;

    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody CategoryReq req) {
        categorySettingService.saveCategory(req);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "delete")
    public ResVo<String> delete(@RequestParam(name = "categoryId") Integer categoryId) {
        categorySettingService.deleteCategory(categoryId);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "operate")
    public ResVo<String> operate(@RequestParam(name = "categoryId") Integer categoryId,
                                 @RequestParam(name = "pushStatus") Integer pushStatus) {
        if (pushStatus != PushStatusEnum.OFFLINE.getCode()
                && pushStatus != PushStatusEnum.ONLINE.getCode()) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS);
        }
        categorySettingService.operateCategory(categoryId, pushStatus);
        return ResVo.ok("ok");
    }

    @PostMapping(path = "list")
    public ResVo<PageVo<CategoryDTO>> list(@RequestBody SearchCategoryReq req) {
        PageVo<CategoryDTO> categoryList = categorySettingService.getCategoryList(req);
        return ResVo.ok(categoryList);
    }
}
