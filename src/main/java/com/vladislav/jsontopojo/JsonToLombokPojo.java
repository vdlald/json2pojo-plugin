package com.vladislav.jsontopojo;

import com.intellij.openapi.progress.ProgressIndicator;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.vladislav.jsontopojo.factories.ClassFieldFactory;
import com.vladislav.jsontopojo.factories.FieldFactory;
import com.vladislav.jsontopojo.factories.PrimitiveFieldFactory;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class JsonToLombokPojo extends AbstractJsonToPojo {

    protected JsonToLombokPojo(Builder builder) {
        super(
                builder.classAnnotations,
                builder.fieldAnnotations,
                builder.fieldFactory,
                builder.packageName,
                builder.destinationPath,
                builder.model
        );
    }

    public static Builder newBuilder(
            String destinationPath,
            String packageName,
            ProgressIndicator indicator
    ) {
        return new Builder(destinationPath, packageName, indicator);
    }

    @Override
    protected void afterClassCreation(JDefinedClass clazz) {

    }

    public static class Builder {
        private final String destinationPath;
        private final String packageName;
        private final ProgressIndicator indicator;

        private final JCodeModel model;

        private Set<Class<? extends Annotation>> fieldAnnotations;
        private Set<Class<? extends Annotation>> classAnnotations;
        private FieldFactory fieldFactory;

        Builder(
                @NonNull String destinationPath,
                @NonNull String packageName,
                @NonNull ProgressIndicator indicator
        ) {
            model = new JCodeModel();
            this.destinationPath = destinationPath;
            this.packageName = packageName;
            this.indicator = indicator;
            fieldFactory = new ClassFieldFactory(model);
            fieldAnnotations = new HashSet<>();
            classAnnotations = new HashSet<>();
        }

        public Builder setFieldAnnotations(Set<Class<? extends Annotation>> fieldAnnotations) {
            this.fieldAnnotations = fieldAnnotations;
            return this;
        }

        public Builder setClassAnnotations(Set<Class<? extends Annotation>> classAnnotations) {
            this.classAnnotations = classAnnotations;
            return this;
        }

        public Builder isPrimitiveFields(boolean isPrimitive) {
            if (isPrimitive) {
                fieldFactory = new PrimitiveFieldFactory(model);
            } else {
                fieldFactory = new ClassFieldFactory(model);
            }
            return this;
        }

        public JsonToLombokPojo build() {
            return new JsonToLombokPojo(this);
        }
    }
}
