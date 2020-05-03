package com.vladislav.jsontopojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.openapi.progress.ProgressIndicator;
import com.sun.codemodel.*;
import com.vladislav.jsontopojo.factories.ClassFieldFactory;
import com.vladislav.jsontopojo.factories.FieldFactory;
import com.vladislav.jsontopojo.factories.PrimitiveFieldFactory;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonToVanillaPojo extends AbstractJsonToPojo {

    private final boolean createGetters;
    private final boolean createSetters;
    private final boolean createNoArgsConstructor;
    private final boolean createAllArgsConstructor;

    protected JsonToVanillaPojo(Builder builder) {
        super(
                builder.classAnnotations,
                builder.fieldAnnotations,
                builder.deserializeAnnotation,
                builder.fieldFactory,
                builder.packageName,
                builder.destinationPath,
                builder.createFinalFields ? JMod.PRIVATE | JMod.FINAL : JMod.PRIVATE,
                builder.model
        );
        this.createGetters = builder.createGetters;
        this.createSetters = builder.createSetters;
        this.createNoArgsConstructor = builder.createNoArgsConstructor;
        this.createAllArgsConstructor = builder.createAllArgsConstructor;
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
        final Map<String, JFieldVar> fields = clazz.fields();
        if (createGetters) {
            fields.forEach((fieldName, field) -> {
                final JMethod method = clazz.method(
                        JMod.PUBLIC,
                        field.type(),
                        "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)
                );
                method.body()._return(field);
            });
        }
        if (createSetters) {
            fields.forEach((fieldName, field) -> {
                final JMethod method = clazz.method(
                        JMod.PUBLIC,
                        Void.TYPE,
                        "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)
                );
                method.param(field.type(), fieldName);
                method.body().assign(JExpr._this().ref(field), JExpr.ref(fieldName));
            });
        }
        if (createNoArgsConstructor) {
            clazz.constructor(JMod.PUBLIC);
        }
        if (createAllArgsConstructor) {
            final JMethod constructor = clazz.constructor(JMod.PUBLIC);
            final JBlock body = constructor.body();
            fields.forEach((fieldName, field) -> {
                constructor.param(field.type(), fieldName);
                body.assign(JExpr._this().ref(field), JExpr.ref(fieldName));
            });
        }
    }

    public static class Builder {
        private final String destinationPath;
        private final String packageName;
        private final ProgressIndicator indicator;

        private final JCodeModel model;

        private Set<Class<? extends Annotation>> fieldAnnotations;
        private Set<Class<? extends Annotation>> classAnnotations;
        private FieldFactory fieldFactory;
        private boolean createGetters;
        private boolean createSetters;
        private boolean createNoArgsConstructor;
        private boolean createAllArgsConstructor;
        private boolean createFinalFields;
        private Class<? extends Annotation> deserializeAnnotation;

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
            createGetters = true;
            createSetters = true;
            createNoArgsConstructor = true;
            createAllArgsConstructor = true;
            createFinalFields = false;
            deserializeAnnotation = null;
        }

        public Builder setFieldAnnotations(Set<Class<? extends Annotation>> fieldAnnotations) {
            this.fieldAnnotations = fieldAnnotations;
            return this;
        }

        public Builder setDeserializeAnnotation(Class<? extends Annotation> deserializeAnnotation) {
            this.deserializeAnnotation = deserializeAnnotation;
            return this;
        }

        public Builder setClassAnnotations(Set<Class<? extends Annotation>> classAnnotations) {
            this.classAnnotations = classAnnotations;
            return this;
        }

        public Builder setPrimitiveFields(boolean isPrimitive) {
            if (isPrimitive) {
                fieldFactory = new PrimitiveFieldFactory(model);
            } else {
                fieldFactory = new ClassFieldFactory(model);
            }
            return this;
        }

        public Builder setCreateGetters(boolean createGetters) {
            this.createGetters = createGetters;
            return this;
        }

        public Builder setCreateSetters(boolean createSetters) {
            this.createSetters = createSetters;
            return this;
        }

        public Builder setCreateNoArgsConstructor(boolean createNoArgsConstructor) {
            this.createNoArgsConstructor = createNoArgsConstructor;
            return this;
        }

        public Builder setCreateAllArgsConstructor(boolean createAllArgsConstructor) {
            this.createAllArgsConstructor = createAllArgsConstructor;
            return this;
        }

        public Builder setCreateFinalFields(boolean createFinalFields) {
            this.createFinalFields = createFinalFields;
            return this;
        }

        public JsonToVanillaPojo build() {
            return new JsonToVanillaPojo(this);
        }
    }
}
