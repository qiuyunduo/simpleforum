package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作文章
 *
 * @author 邱运铎
 * @date 2024-05-18 16:21
 */
@Getter
@AllArgsConstructor
public enum OperateArticleEnum {

    EMPTY(0, "") {
        @Override
        public int getDbStatCode() {
            return 0;
        }
    },

    OFFICIAL(1, "官方") {
        @Override
        public int getDbStatCode() {
            return OfficialStatEnum.OFFICIAL.getCode();
        }
    },
    CANCEL_OFFICIAL(2, "非官方"){
        @Override
        public int getDbStatCode() {
            return OfficialStatEnum.NOT_OFFICIAL.getCode();
        }
    },
    TOPPING(3, "置顶"){
        @Override
        public int getDbStatCode() {
            return ToppingStatEnum.TOPPING.getCode();
        }
    },
    CANCEL_TOPPING(4, "不置顶"){
        @Override
        public int getDbStatCode() {
            return ToppingStatEnum.NOT_TOPPING.getCode();
        }
    },
    CREAM(5, "加精"){
        @Override
        public int getDbStatCode() {
            return CreamStatEnum.CREAM.getCode();
        }
    },
    CANCEL_CREAM(6, "不加精"){
        @Override
        public int getDbStatCode() {
            return CreamStatEnum.NOT_CREAM.getCode();
        }
    };
    ;

    private final Integer code;
    private final String desc;

    public static OperateArticleEnum fromCode(Integer code) {
        for (OperateArticleEnum operate : values()) {
            if (operate.getCode().equals(code)) {
                return operate;
            }
        }
        return OFFICIAL;
    }

    public abstract int getDbStatCode();
}
