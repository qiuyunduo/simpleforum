package com.qyd.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * markdown 文本中的图片识别
 *
 * @author 邱运铎
 * @date 2024-05-13 21:21
 */
public class MdImgLoader {
    private static Pattern IMG_PATTERN = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MdImg {
        /**
         * 原始文本
         */
        private String origin;

        /**
         * 图片描述
         */
        private String desc;

        /**
         * 图片地址
         */
        private String url;
    }

    public static List<MdImg> loadImages(String content) {
        Matcher matcher = IMG_PATTERN.matcher(content);
        List<MdImg> list = new ArrayList<>();
        while (matcher.find()) {
            // match.group(0) 指的是正则表达式中匹配的原始字符串
            // match.group(1) 指正则表达式中第一个括号中匹配的字符串依次类推
            list.add(new MdImg(matcher.group(0), matcher.group(1), matcher.group(2)));
        }
        return list;
    }

    public static void main(String[] args) {
        List<MdImg> list = loadImages("![hell](http://www.baidu.com/img/12.jpg)asdasd");
        System.out.println(list);
    }
}
