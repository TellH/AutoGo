package com.autogo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by tlh on 2016/7/31.
 */
@Retention(CLASS) @Target(FIELD)
public @interface IntentValue {
    //key in map
    String value() default "";
}
