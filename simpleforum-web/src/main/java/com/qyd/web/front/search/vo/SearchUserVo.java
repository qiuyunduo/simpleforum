package com.qyd.web.front.search.vo;

import com.qyd.api.model.vo.user.dto.SimpleUserInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-05-09 17:22
 */
@Data
@ApiModel("用户信息")
public class SearchUserVo implements Serializable {
    private static final long serialVersionUID = 8721664523276513215L;

    @ApiModelProperty("搜索的关键词")
    private String key;

    @ApiModelProperty("用户列表")
    private List<SimpleUserInfoDTO> items;
}
