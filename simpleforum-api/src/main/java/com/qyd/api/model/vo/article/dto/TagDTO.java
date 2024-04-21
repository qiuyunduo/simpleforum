package com.qyd.api.model.vo.article.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-04-10 21:55
 */
@Data
public class TagDTO implements Serializable {
    private static final long serialVersionUID = -8614833588325787479L;;

    private Long tagId;

    private String tag;

    private Integer status;

    private Boolean selected;
}
