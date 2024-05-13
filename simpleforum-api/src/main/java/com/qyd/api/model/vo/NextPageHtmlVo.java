package com.qyd.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 邱运铎
 * @date 2024-05-08 12:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextPageHtmlVo implements Serializable {
    private String html;
    private Boolean hasMore;
}
