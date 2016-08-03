package com.autogo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by tlh on 2016/8/3.
 */
@Retention(CLASS) @Target(FIELD)
public @interface Bundle {
    //key in map, if you do not pass the key, the field name will be the key.
    String value() default "";
}
