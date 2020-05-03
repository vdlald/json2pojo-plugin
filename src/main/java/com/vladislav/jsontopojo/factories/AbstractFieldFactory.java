package com.vladislav.jsontopojo.factories;

import com.google.gson.JsonPrimitive;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class AbstractFieldFactory implements FieldFactory {

    protected final JCodeModel model;

    @Override
    public JFieldVar createPrimitiveField(JDefinedClass clazz, int fieldMods, String fieldName, JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isBoolean()) {
            return createBooleanField(clazz, fieldMods, fieldName);
        } else if (jsonPrimitive.isNumber()) {
            Number number = jsonPrimitive.getAsNumber();
            if (number.doubleValue() == number.longValue()) {
                if (number.doubleValue() == number.intValue()) {
                    return createIntField(clazz, fieldMods, fieldName);
                } else {
                    return createLongField(clazz, fieldMods, fieldName);
                }
            } else {
                return createDoubleField(clazz, fieldMods, fieldName);
            }
        } else if (jsonPrimitive.isString()) {
            return createStringField(clazz, fieldMods, fieldName);
        }
        return null;
    }

    @Override
    public JFieldVar createStringField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.ref(String.class), fieldName); // todo: try cache ref
    }
}
