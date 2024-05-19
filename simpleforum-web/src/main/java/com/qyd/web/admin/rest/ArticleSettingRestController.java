package com.qyd.web.admin.rest;

import com.qyd.api.model.context.ReqInfoContext;
import com.qyd.api.model.enums.OperateArticleEnum;
import com.qyd.api.model.vo.PageVo;
import com.qyd.api.model.vo.ResVo;
import com.qyd.api.model.vo.article.ArticlePostReq;
import com.qyd.api.model.vo.article.SearchArticleReq;
import com.qyd.api.model.vo.article.dto.ArticleAdminDTO;
import com.qyd.api.model.vo.article.dto.ArticleDTO;
import com.qyd.api.model.vo.article.dto.SimpleArticleDTO;
import com.qyd.api.model.vo.constants.StatusEnum;
import com.qyd.core.permission.Permission;
import com.qyd.core.permission.UserRole;
import com.qyd.core.util.NumUtil;
import com.qyd.service.article.service.ArticleReadService;
import com.qyd.service.article.service.ArticleSettingService;
import com.qyd.service.article.service.ArticleWriteService;
import com.qyd.web.front.search.vo.SearchArticleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 文章管理后台
 *
 * @author 邱运铎
 * @date 2024-05-18 16:06
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "文章设置管理控制器", tags = "文章管理")
@RequestMapping(path = {"/api/admin/article", "admin/article"})
public class ArticleSettingRestController {
    @Autowired
    private ArticleSettingService articleSettingService;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private ArticleWriteService articleWriteService;

    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody ArticlePostReq req) {
        if (NumUtil.nullOrZero(req.getArticleId())) {
            // 新增文章
            this.articleWriteService.saveArticle(req, ReqInfoContext.getReqInfo().getUserId());
        } else {
            // 更新文章
            this.articleWriteService.saveArticle(req, null);
        }
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "update")
    public ResVo<String> update(@RequestBody ArticlePostReq req) {
        articleSettingService.updateArticle(req);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "operate")
    public ResVo<String> operate(@RequestParam(name = "articleId") Long articleId,
                                 @RequestParam(name = "operateType") Integer operateType) {
        OperateArticleEnum operate = OperateArticleEnum.fromCode(operateType);
        if (operate == OperateArticleEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "非法");
        }
        articleSettingService.operateArticle(articleId, operate);
        return ResVo.ok("ok");
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping("delete")
    public ResVo<String> delete(@RequestParam(name = "articleId") Long articleId) {
        articleSettingService.deleteArticle(articleId);
        return ResVo.ok("ok");
    }

    @ApiOperation("个根据文章id获取文章详情")
    @GetMapping(path = "detail")
    public ResVo<ArticleDTO> detail(@RequestParam(name = "articleId", required = false) Long articleId) {
        ArticleDTO articleDTO = new ArticleDTO();
        if (articleId != null) {
            // 查询文章详情
            articleDTO = articleReadService.queryDetailArticleInfo(articleId);
        }
        return ResVo.ok(articleDTO);
    }

    @ApiOperation("获取文章列表")
    @PostMapping(path = "list")
    public ResVo<PageVo<ArticleAdminDTO>> list(@RequestBody SearchArticleReq req) {
        PageVo<ArticleAdminDTO> articleAdminDTOPageVo = articleSettingService.getArticleList(req);
        return ResVo.ok(articleAdminDTOPageVo);
    }

    @ApiOperation("文章搜索")
    @GetMapping(path = "query")
    public ResVo<SearchArticleVo> queryArticleList(@RequestParam(name = "key", required = false) String key) {
        List<SimpleArticleDTO> list = articleReadService.querySimpleArticleBySearchKey(key);
        SearchArticleVo vo = new SearchArticleVo();
        vo.setKey(key);
        vo.setItems(list);
        return ResVo.ok(vo);
    }
}
