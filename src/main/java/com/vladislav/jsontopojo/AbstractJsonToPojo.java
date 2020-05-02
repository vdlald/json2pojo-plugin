package com.vladislav.jsontopojo;

import com.google.gson.*;
import com.sun.codemodel.*;
import com.vladislav.jsontopojo.factories.FieldFactory;
import lombok.NonNull;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NotThreadSafe
public abstract class AbstractJsonToPojo {

    private final Set<Class<? extends Annotation>> classAnnotations;
    private final Set<Class<? extends Annotation>> fieldAnnotations;
    private final Class<? extends Annotation> deserializeAnnotation;
    private final FieldFactory fieldFactory;
    private final String packageName;
    private final String destinationPath;
    private final int fieldMods;
    private final JCodeModel codeModel;

    private JPackage jPackage;
    private Set<JDefinedClass> classes;

    private final JClass stringRef;
    private final JClass objectRef;
    private final JClass booleanRef;
    private final JClass integerRef;
    private final JClass longRef;
    private final JClass doubleRef;
    private final JClass listRef;
    private final JClass objectListRef;

    public AbstractJsonToPojo(
            Set<Class<? extends Annotation>> classAnnotations,
            Set<Class<? extends Annotation>> fieldAnnotations,
            Class<? extends Annotation> deserializeAnnotation,
            FieldFactory fieldFactory,
            String packageName,
            String destinationPath,
            int fieldMods,
            JCodeModel model
    ) {
        this.classAnnotations = classAnnotations;
        this.fieldAnnotations = fieldAnnotations;
        this.deserializeAnnotation = deserializeAnnotation;
        this.fieldFactory = fieldFactory;
        this.packageName = packageName;
        this.destinationPath = destinationPath;
        this.fieldMods = fieldMods;
        this.codeModel = model;
        stringRef = codeModel.ref(String.class);
        objectRef = codeModel.ref(Object.class);
        booleanRef = codeModel.ref(Boolean.class);
        integerRef = codeModel.ref(Integer.class);
        longRef = codeModel.ref(Long.class);
        doubleRef = codeModel.ref(Double.class);
        listRef = codeModel.ref(List.class);
        objectListRef = listRef.narrow(objectRef);
    }

    public void apply(@NonNull String json, @NonNull String className) {
        final JsonElement jsonElement = JsonParser.parseString(json);
        classes = new HashSet<>();
        jPackage = codeModel._package(packageName);
        try {
            parseObjectToClass(jsonElement.getAsJsonObject(), className);
            codeModel.build(new File(destinationPath));
            classes.forEach(jPackage::remove);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JDefinedClass parseObjectToClass(JsonObject jsonObject, String className) throws JClassAlreadyExistsException {
        JDefinedClass clazz;

        clazz = jPackage._class(formatClassName(className));
        classAnnotations.forEach(clazz::annotate);
        classes.add(clazz);

        jsonObject.entrySet().forEach(entry -> {
            final String fieldName = entry.getKey();
            final JsonElement value = entry.getValue();

            if (value.isJsonPrimitive()) {
                createPrimitiveField(clazz, fieldName, value.getAsJsonPrimitive());
            } else if (value.isJsonObject()) {
                try {
                    createField(
                            clazz,
                            parseObjectToClass(value.getAsJsonObject(), className + formatClassName(fieldName)),
                            fieldName
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (value.isJsonArray()) {
                try {
                    parseArrayToField(value.getAsJsonArray(), fieldName, clazz);
                } catch (JClassAlreadyExistsException e) {
                    e.printStackTrace();
                }
            } else {
                createField(clazz, objectRef, fieldName);
            }
        });
        afterClassCreation(clazz);
        return clazz;
    }

    // region Work with Fields
    private JFieldVar createField(JDefinedClass clazz, JClass ref, String name) {
        final String formatName = formatFieldName(name);
        final JFieldVar field = clazz.field(fieldMods, ref, formatName);
        if (!formatName.equals(name)) {
            field.annotate(deserializeAnnotation).param("value", name);
        }
        fieldAnnotations.forEach(field::annotate);
        return field;
    }

    private JFieldVar createPrimitiveField(JDefinedClass clazz, String name, JsonPrimitive primitive) {
        final JFieldVar field = fieldFactory.createPrimitiveField(
                clazz,
                fieldMods,
                formatFieldName(name),
                primitive
        );
        fieldAnnotations.forEach(field::annotate);
        return field;
    }

    private static String formatFieldName(String name) {
        return Utils.toCamelCase(name, true);
    }
    // endregion

    // region Work with JsonArray
    private JFieldVar parseArrayToField(JsonArray array, String fieldName, JDefinedClass clazz) throws JClassAlreadyExistsException {
        JsonElement jsonElement;
        int i = 0;
        do {
            jsonElement = array.get(i);
            i++;
        } while (jsonElement.isJsonNull() && i < array.size());
        if (i == array.size() && jsonElement.isJsonNull()) {
            return createField(clazz, objectListRef, fieldName);
        }
        if (jsonElement.isJsonPrimitive()) {
            for (int j = i + 1; j < array.size(); j++) {
                final JsonElement element = array.get(j);
                if (element.isJsonNull()) continue;
                if (!array.get(j).isJsonPrimitive()) {
                    return createField(clazz, objectListRef, fieldName);
                }
            }
            return createField(
                    clazz,
                    listRef.narrow(getTypeOfJsonArrayPrimitive(jsonElement.getAsJsonPrimitive())),
                    fieldName
            );
        } else if (jsonElement.isJsonObject()) {
            for (int j = i + 1; j < array.size(); j++) {
                final JsonElement element = array.get(j);
                if (element.isJsonNull()) continue;
                if (!array.get(j).isJsonObject()) {
                    return createField(clazz, objectListRef, fieldName);
                }
            }
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (int j = i + 1; j < array.size(); j++) {
                final JsonElement element = array.get(j);
                if (element.isJsonNull()) continue;
                final JsonObject object = element.getAsJsonObject();
                if (!object.keySet().equals(jsonObject.keySet()))
                    return createField(clazz, objectListRef, fieldName);
            }
            return createField(
                    clazz,
                    listRef.narrow(parseObjectToClass(jsonObject, clazz.name() + formatClassName(fieldName))),
                    fieldName
            );
        } else if (jsonElement.isJsonArray()) {
            throw new RuntimeException();
        }
        return createField(clazz, objectListRef, fieldName);
    }

    private JType getTypeOfJsonArrayPrimitive(JsonPrimitive primitive) {
        if (primitive.isString()) {
            return stringRef;
        } else if (primitive.isBoolean()) {
            return booleanRef;
        } else if (primitive.isNumber()) {
            Number number = primitive.getAsNumber();
            if (number.doubleValue() == number.longValue())
                if (number.doubleValue() == number.intValue()) {
                    return integerRef;
                } else {
                    return longRef;
                }
            else {
                return doubleRef;
            }
        } else
            return objectRef;
    }
    // endregion

    private static String formatClassName(String name) {
        return Utils.toCamelCase(name, false);
    }

    protected abstract void afterClassCreation(JDefinedClass clazz);

}
