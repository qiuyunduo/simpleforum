/**
 * 1. 提供报的整体注释卓明
 * simple forum网站主页相关调用服务模块
 * package-info 的作用和说明
 * 提供包的整体注释说明
 * <b>package-info 不是平常类，其作用有三个</b>
 *      1). 为标注在包上的Annotation提供便利
 *      2). 声明友好类和包常量
 *      3). 提供包的整体注释说明（描述和记录本包信息）
 * @author 邱运铎
 * @date 2024-05-09 11:21
 */
// 2. 为标注在包上Annotion提供便利
// @注解
@PkgAnnotation(value = "com.qyd.web.front")
package com.qyd.web.front;

import com.qyd.web.front.test.PkgAnnotation;

// 3. 声明包的私有常量
// 包常来那个，只运行包内访问，适用于分"包"开发
class PkgConst {
    final static String PREFIX = "com.qyd.web";
}

// 4. 声明包的私有类
// 包类，声明一个包使用的公共类，强调的是包的访问权限
class PkgClass {
    void say() {
        System.out.println("hello java.");
    }
}