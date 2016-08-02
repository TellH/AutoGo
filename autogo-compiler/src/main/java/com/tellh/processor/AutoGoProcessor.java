package com.tellh.processor;

import com.autogo.annotation.IntentValue;
import com.google.auto.service.AutoService;
import com.tellh.brewer.AutoGoCodeBrewer;
import com.tellh.utils.ProcessorUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * Created by tlh on 2016/7/31.
 */
@AutoService(Processor.class)
public class AutoGoProcessor extends AbstractProcessor {
    private Filer mFileUtils;
    private Messager mMessager;
    private IntentValueHandler intentValueHandler;
    private AutoGoCodeBrewer.Builder autoGoBrewerBuilder;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFileUtils = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        autoGoBrewerBuilder = AutoGoCodeBrewer.builder(mFileUtils);
        intentValueHandler = new IntentValueHandler(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(IntentValue.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //Take note, this method could be invoke several times during compiling.
        if (!intentValueHandler.handle(roundEnv, autoGoBrewerBuilder))
            return true;
        try {
            autoGoBrewerBuilder.brew();
        } catch (IOException e) {
            ProcessorUtils.error(mMessager, e.getMessage());
        }
        return true;
    }

}
