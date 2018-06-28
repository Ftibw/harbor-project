package com.whxm.harbor.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KeepAliveDetect {
    long value() default 30;//默认30分钟检测一次
}
