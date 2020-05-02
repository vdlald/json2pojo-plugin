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
    }

    public void apply(@NonNull String json, @NonNull String className) {
        final JsonElement jsonElement = JsonParser.parseString(json);
        classes = new HashSet<>();
        jPackage = codeModel._package(packageName);
        try {
            parseObject(jsonElement.getAsJsonObject(), className);
            codeModel.build(new File(destinationPath));
            classes.forEach(jPackage::remove);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JDefinedClass parseObject(JsonObject jsonObject, String className) throws Exception {
        JDefinedClass clazz;

        clazz = jPackage._class(formatClassName(className));
        classAnnotations.forEach(clazz::annotate);
        classes.add(clazz);

        jsonObject.entrySet().forEach(entry -> {
            final String key = entry.getKey();
            final JsonElement value = entry.getValue();

            if (value.isJsonPrimitive()) {
                createPrimitiveField(clazz, key, value.getAsJsonPrimitive());
            } else if (value.isJsonObject()) {
                try {
                    createField(
                            clazz,
                            parseObject(value.getAsJsonObject(), className + formatClassName(key)),
                            key
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (value.isJsonArray()) {
                final JType typeOfJsonArray = getTypeOfJsonArray(value.getAsJsonArray());
                createField(clazz, codeModel.ref(List.class).narrow(typeOfJsonArray), key);
            } else {
                // todo: isJsonNull
            }
        });
        afterClassCreation(clazz);
        return clazz;
    }

    // region Work with Fields
    private void createField(JDefinedClass clazz, JClass ref, String name) {
        final String formatName = formatFieldName(name);
        final JFieldVar field = clazz.field(fieldMods, ref, formatName);
        if (!formatName.equals(name)) {
            field.annotate(deserializeAnnotation).param("value", name);
        }
        fieldAnnotations.forEach(field::annotate);
    }

    private void createPrimitiveField(JDefinedClass clazz, String name, JsonPrimitive primitive) {
        final JFieldVar field = fieldFactory.createPrimitiveField(
                clazz,
                fieldMods,
                formatFieldName(name),
                primitive
        );
        fieldAnnotations.forEach(field::annotate);
    }

    private static String formatFieldName(String name) {
        return Utils.toCamelCase(name, true);
    }
    // endregion

    // region Work with JsonArray
    private JType getTypeOfJsonArray(JsonArray array) {
        JsonElement jsonElement;
        int i = 0;
        do {
            jsonElement = array.get(i);
            i++;
        } while (jsonElement.isJsonNull() && i < array.size());
        if (i == array.size() && jsonElement.isJsonNull()) {
            return objectRef;
        } else if (i == array.size()) {
            return getTypeOfJsonArrayElement(jsonElement);
        } else {
            Set<JType> types = new HashSet<>();
            types.add(getTypeOfJsonArrayElement(jsonElement));
            for (; i < array.size(); i++)
                types.add(getTypeOfJsonArrayElement(array.get(i)));
            final JType[] typesArray = types.toArray(new JType[0]);
            if (typesArray.length == 1)
                return typesArray[0];
            else {
                boolean containInteger = false;
                boolean containDouble = false;
                boolean containLong = false;
                boolean containString = false;
                boolean containBoolean = false;
                boolean containList = false;
                for (JType jType : typesArray) {
                    if (jType == objectRef)
                        return objectRef;
                    else if (jType.isArray())
                        containList = true;
                    else if (jType == stringRef)
                        containString = true;
                    else if (jType == booleanRef)
                        containBoolean = true;
                    else if (jType == doubleRef)
                        containDouble = true;
                    else if (jType == integerRef)
                        containInteger = true;
                    else if (jType == longRef)
                        containLong = true;
                }
                boolean containNumber = containInteger || containDouble || containLong;
                boolean mixedType =
                        (containString && (containNumber || containList || containBoolean)) ||
                                (containList && (containNumber || containBoolean)) ||
                                (containNumber && containBoolean);
                if (mixedType) {
                    return objectRef;
                } else if (containString) {
                    return stringRef;
                } else if (containList) {
                    for (JType jType : typesArray)
                        if (jType.isArray())
                            return jType;
                } else if (containNumber) {
                    if (containDouble) {
                        return doubleRef;
                    } else if (containLong) {
                        return longRef;
                    } else {
                        return integerRef;
                    }
                }
            }
        }
        return objectRef;
    }

    private JType getTypeOfJsonArrayElement(JsonElement element) {
        if (element.isJsonPrimitive())
            return getTypeOfJsonArrayPrimitive(element.getAsJsonPrimitive());
        else if (element.isJsonArray())
            return codeModel.ref(List.class).narrow(getTypeOfJsonArray(element.getAsJsonArray()));
        else
            return objectRef;  // todo: bug, parse object
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
