package com.tellh.brewer;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.tellh.entity.KeyValueGroup;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;

/**
 * Created by tlh on 2016/8/2.
 */
public abstract class CodeBrewer {
    protected Filer mFileUtils;
    protected Elements mElementUtils;
    protected Messager mMessager;
    public CodeBrewer(Filer mFileUtils, Elements mElementUtils, Messager mMessager) {
        this.mFileUtils = mFileUtils;
        this.mElementUtils = mElementUtils;
        this.mMessager = mMessager;
    }
    public abstract void brewCode(KeyValueGroup group) throws IOException;
    public static void brewJavaFile(String packageName, TypeSpec classType, Filer fileUtils) throws IOException {
        JavaFile javaFile = JavaFile.builder(packageName, classType)
                .build();
        javaFile.writeTo(fileUtils);
    }
}
