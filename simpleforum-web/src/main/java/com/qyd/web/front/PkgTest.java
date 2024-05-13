package com.qyd.web.front;

import com.qyd.web.front.test.PkgAnnotation;

import java.lang.annotation.Annotation;

/**
 * @author 邱运铎
 * @date 2024-05-09 11:48
 */
public class PkgTest extends PkgClass {

    public static void main(String[] args) {
        PkgTest pkgTest = new PkgTest();
        pkgTest.say();
        System.out.println();

        System.out.println(PkgConst.PREFIX);
        System.out.println();

        String pkgName = "com.qyd.web.front";
        Package pkg = Package.getPackage(pkgName);
        System.out.println(pkg.getName());
        System.out.println("");

        PkgAnnotation declaredAnnotation = pkg.getDeclaredAnnotation(PkgAnnotation.class);
        System.out.println(declaredAnnotation.value());
        System.out.println();

        PkgAnnotation pkgAnnotation = pkg.getAnnotation(PkgAnnotation.class);
        System.out.println(pkgAnnotation.value());
        System.out.println();

        Annotation[] annotations = pkg.getAnnotations();
        for (Annotation a : annotations) {
            if (a instanceof PkgAnnotation) {
                PkgAnnotation pkgA = (PkgAnnotation) a;
                System.out.println(pkgA.value());
            }
        }
    }

    @Override
    void say() {
        System.out.println("Hello Java computer programmer!");
    }
}
