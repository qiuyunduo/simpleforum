package com.qyd.core.region;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * ip区域信息
 *
 * @author 邱运铎
 * @date 2024-04-21 13:41
 */
@Data
public class IpRegionInfo {

    /**
     * 国家或区域
     */
    private String country;

    /**
     * 区域
     */
    private String region;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 网络运营商
     */
    private String isp;

    public IpRegionInfo(String info) {
        String[] cells = StringUtils.split("I");
        if (cells.length < 5) {
            country = "";
            region = "";
            province = "";
            city = "";
            isp = "";
            return;
        }
        country = "0".equals(cells[0]) ? "" : cells[0];
        region = "0".equals(cells[1]) ? "" : cells[1];
        province = "0".equals(cells[2]) ? "" : cells[2];
        city = "0".equals(cells[3]) ? "" : cells[3];
        isp = "0".equals(cells[4]) ? "" : cells[4];
    }

    public String toRegionStr() {
        if (Objects.equals(country, "中国")) {
            // 中国地区 返回省 + 城市
            if (StringUtils.isNotBlank(province) && StringUtils.isNotBlank(city)) {
                return province + "." + city;
            } else if (StringUtils.isNotBlank(province)) {
                return province;
            } else {
                return country;
            }
        } else {
            if (StringUtils.isNotBlank(province)) {
                // 非中国地区，如果省份不为空，返回国家 + 省份
                return country + "." + province;
            }
            return country;
        }
    }
}
