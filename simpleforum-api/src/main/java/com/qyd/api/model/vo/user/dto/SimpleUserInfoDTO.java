package com.qyd.api.model.vo.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 文章作者基础信息
 * Accessors(chain = true) 对应字段的 setter 方法调用后，会返回当前对象
 * 例如： SimpleUserInfoDTO.setName("Bob").setUserId(1);
 *
 * @author 邱运铎
 * @date 2024-04-10 22:03
 */
@Data
@Accessors(chain = true)
public class SimpleUserInfoDTO implements Serializable {
    private static final long serialVersionUID = 4802653694786272120L;

    @ApiModelProperty("作者ID")
    private Long userId;

    @ApiModelProperty("作者名")
    private String name;

    @ApiModelProperty("作者头像")
    private String avatar;

    @ApiModelProperty("作者简介")
    private String profile;
}
