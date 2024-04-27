package com.qyd.service.article.conveter;

import com.qyd.api.model.vo.article.ColumnArticleReq;
import com.qyd.api.model.vo.article.ColumnReq;
import com.qyd.api.model.vo.article.dto.ColumnDTO;
import com.qyd.service.article.repository.entity.ColumnArticleDO;
import com.qyd.service.article.repository.entity.ColumnInfoDO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 邱运铎
 * @date 2024-04-28 0:04
 */
public class ColumnConvert {

    public static ColumnDTO toDTO(ColumnInfoDO info) {
        ColumnDTO dto = new ColumnDTO();
        dto.setColumnId(info.getId());
        dto.setColumn(info.getColumnName());
        dto.setCover(info.getCover());
        dto.setIntroduction(info.getIntroduction());
        dto.setState(info.getState());
        dto.setNums(info.getNums());
        dto.setAuthor(info.getUserId());
        dto.setSection(info.getSection());
        dto.setPublishTime(info.getPublishTime().getTime());
        dto.setType(info.getType());
        dto.setFreeStartTime(info.getFreeStartTime().getTime());
        dto.setFreeEndTime(info.getFreeEndTime().getTime());
        return dto;
    }

    public static List<ColumnDTO> toDTOs(List<ColumnInfoDO> columnInfoDOS) {
        List<ColumnDTO> columnDTOS = new ArrayList<>();
        columnInfoDOS.forEach(info -> columnDTOS.add(ColumnConvert.toDTO(info)));
        return columnDTOS;
    }

    public static ColumnInfoDO toDO(ColumnReq columnReq) {
        if (columnReq == null) {
            return null;
        }
        ColumnInfoDO columnInfoDO = new ColumnInfoDO();
        columnInfoDO.setColumnName(columnReq.getColumn());
        columnInfoDO.setUserId(columnReq.getAuthor());
        columnInfoDO.setIntroduction(columnReq.getIntroduction());
        columnInfoDO.setCover(columnReq.getCover());
        columnInfoDO.setState(columnReq.getState());
        columnInfoDO.setSection(columnReq.getSection());
        columnInfoDO.setNums(columnReq.getNums());
        columnInfoDO.setType(columnReq.getType());
        columnInfoDO.setFreeStartTime(new Date(columnReq.getFreeStartTime()));
        columnInfoDO.setFreeEndTime(new Date(columnReq.getFreeEndTime()));
        return columnInfoDO;
    }

    public static ColumnArticleDO toDO(ColumnArticleReq columnArticleReq) {
        if (columnArticleReq == null) {
            return null;
        }
        ColumnArticleDO columnArticleDO = new ColumnArticleDO();
        columnArticleDO.setColumnId(columnArticleReq.getColumnId());
        columnArticleDO.setArticleId(columnArticleReq.getArticleId());
        columnArticleDO.setSection(columnArticleReq.getSort());
        return columnArticleDO;
    }
}
