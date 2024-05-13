package com.qyd.web.front.user.rest;

import cn.hutool.db.Page;
import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.FollowTypeEnum;
import com.qyd.api.model.enums.HomeSelectEnum;
import com.qyd.api.model.vo.NextPageHtmlVo;
import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.PageParam;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.api.model.vo.user.UserInfoSaveReq;
import com.qyd.api.model.vo.user.UserRelationReq;
import com.qyd.api.model.vo.user.dto.FollowUserInfoDTO;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.user.service.UserService;
import com.qyd.service.user.service.relation.UserRelationServiceImpl;
import com.qyd.web.component.TemplateEngineHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author 邱运铎
 * @date 2024-05-09 0:45
 */
@RestController
@RequestMapping(path = "user/api")
public class UserRestController {

    /**
     * 这里和下面的userRelationService， 一灰都是使用对应Impl类
     * 我感觉这样就可以了，看后面会不会有问题
     */
    @Resource
    private UserService userService;
    /**
     * 猜测是不是通过具体的类名可以使Resource注解更效率根据名称找到需要注入的对象
     * 还有就是一灰打算展示@Resource用法的多样性
     * 再要么猜测就是随意写，没注意到前后一致规范问题
     */
    @Resource
    private UserRelationServiceImpl userRelationService;
    @Resource
    private TemplateEngineHelper templateEngineHelper;
    @Resource
    private ArticleReadService articleReadService;

    /**
     * 保存用户关系
     *
     * @param req
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "saveUserRelation")
    public ResVo<Boolean> saveUserRelation(@RequestBody UserRelationReq req) {
        userRelationService.saveUserRelation(req);
        return ResVo.ok(true);
    }

    /**
     * 保存用户详情
     *
     * @param req
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "saveUserInfo")
    @Transactional(rollbackFor = Exception.class)
    public ResVo<Boolean> saveUserInfo(@RequestBody UserInfoSaveReq req) {
        if (req.getUserId() == null || !Objects.equals(req.getUserId(), ReqInfoContext.getReqInfo().getUserId())) {
            // 不能修改其他用户的信息
            return ResVo.fail(StatusEnum.FORBID_ERROR_MIXED, "无权限修改");
        }
        userService.saveUserInfo(req);
        return ResVo.ok(true);
    }

    /**
     * 用户的文章列表翻页
     *
     * @return
     */
    @GetMapping(path = "articleList")
    public ResVo<NextPageHtmlVo> articleList(@RequestParam(name = "userId") Long userId,
                                             @RequestParam(name = "homeSelectType") String homeSelectType,
                                             @RequestParam("page") Long page,
                                             @RequestParam(name = "pageSize", required = false) Long pageSize) {
        HomeSelectEnum select = HomeSelectEnum.fromCode(homeSelectType);
        if (select == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS);
        }

        if (pageSize == null) pageSize = PageParam.DEFAULT_PAGE_SIZE;
        PageParam pageParam = PageParam.newPageInstance(page, pageSize);
        PageListVo<ArticleDTO> dto = articleReadService.queryArticlesByUserAndType(userId, pageParam, select);
        String html = templateEngineHelper.rendToVO("views/user/articles/index", "homeSelectList", dto);
        return ResVo.ok(new NextPageHtmlVo(html, dto.getHasMore()));
    }

    @GetMapping(path = "followList")
    public ResVo<NextPageHtmlVo> followList(@RequestParam(name = "userId") Long userId,
                                            @RequestParam(name = "followSelectType") String followSelectType,
                                            @RequestParam("page") Long page,
                                            @RequestParam(name = "pageSize", required = false) Long pageSize) {
        if (pageSize == null) pageSize = PageParam.DEFAULT_PAGE_SIZE;
        PageParam pageParam = PageParam.newPageInstance(page, pageSize);
        PageListVo<FollowUserInfoDTO> followList;
        boolean needUpdateRelation = false;
        if (followSelectType.equals(FollowTypeEnum.FOLLOW.getCode())) {
            followList = userRelationService.getUserFollowList(userId, pageParam);
        } else {
            // 查询粉丝列表是，只能确定粉丝关注了userId,但不能反向判断，因此需要再更新下映射关系，判断userId是否有关注这个用户
            followList = userRelationService.getUserFansList(userId, pageParam);
            needUpdateRelation = true;
        }

        Long loginUserId = ReqInfoContext.getReqInfo().getUserId();
        if (!Objects.equals(loginUserId, userId) || needUpdateRelation) {
            userRelationService.updateUserFollowRelation(followList, userId);
        }
        String html = templateEngineHelper.rendToVO("views/user/follows/index", "followList", followList);
        return ResVo.ok(new NextPageHtmlVo(html, followList.getHasMore()));
    }
}
