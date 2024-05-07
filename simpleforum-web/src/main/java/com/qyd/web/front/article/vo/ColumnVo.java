package com.qyd.web.front.article.vo;

import com.qyd.api.model.vo.PageListVo;
import com.qyd.api.model.vo.article.dto.ColumnDTO;
import com.qyd.api.model.vo.recommend.SideBarDTO;
import lombok.Data;

import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-04 19:44
 */
@Data
public class ColumnVo {

    /**
     * 专栏列表
     */
    private PageListVo<ColumnDTO> columns;

    /**
     * 侧边栏信息
     */
    private List<SideBarDTO> sideBarItems;
}
