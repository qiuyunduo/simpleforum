package com.qyd.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作类型
 *
 * @author 邱运铎
 * @date 2024-04-15 23:33
 */
@AllArgsConstructor
@Getter
public enum OperateTypeEnum {
    EMPTY(0, ""){
        @Override
        public int getDbStatCode() {
            return 0;
        }
    },
    READ(1, "阅读") {
        @Override
        public int getDbStatCode() {
            return ReadStatEnum.READ.getCode();
        }
    },
    PRAISE(2, "点赞") {
        @Override
        public int getDbStatCode() {
            return PraiseStatEnum.PRAISE.getCode();
        }
    },
    COLLECTION(3, "收藏") {
        @Override
        public int getDbStatCode() {
            return CollectionStatEnum.COLLECTION.getCode();
        }
    },
    CANCEL_PRAISE(4, "取消点赞") {
        @Override
        public int getDbStatCode() {
            return PraiseStatEnum.CANCEL_PRAISE.getCode();
        }
    },
    CANCEL_COLLECTION(5, "取消收藏") {
        @Override
        public int getDbStatCode() {
            return CollectionStatEnum.CANCEL_COLLECTION.getCode();
        }
    },
    COMMENT(6, "评论") {
        @Override
        public int getDbStatCode() {
            return CommentStatEnum.COMMENT.getCode();
        }
    },
    DELETE_COMMENT(7, "删除评论") {
        @Override
        public int getDbStatCode() {
            return CommentStatEnum.DELETE_COMMENT.getCode();
        }
    },
    ;

    private final Integer code;
    private final String desc;

    private static Map<Integer, OperateTypeEnum> cache;

    static {
        cache = new HashMap<>();
        for (OperateTypeEnum item : values()) {
            cache.put(item.getCode(), item);
        }
    }

    public static OperateTypeEnum fromCode(Integer code) {
        return cache.getOrDefault(code, EMPTY);
    }

    public abstract int getDbStatCode();

    /**
     * 判断操作的是否是文章
     *
     * @param type
     * @return true 表示是文章的相关操作 false 表示是评论的相关操作
     */
    public static DocumentTypeEnum getOperateDocumentType(OperateTypeEnum type) {
        return (type == COMMENT || type == DELETE_COMMENT) ? DocumentTypeEnum.COMMENT : DocumentTypeEnum.ARTICLE;
    }

    public static NotifyTypeEnum getNotifyType(OperateTypeEnum type) {
        switch (type) {
            case PRAISE:
                return NotifyTypeEnum.PRAISE;
            case CANCEL_PRAISE:
                return NotifyTypeEnum.CANCEL_PRAISE;
            case COLLECTION:
                return NotifyTypeEnum.COLLECT;
            case CANCEL_COLLECTION:
                return NotifyTypeEnum.CANCEL_COLLECT;
            default:
                return null;
        }
    }
}
