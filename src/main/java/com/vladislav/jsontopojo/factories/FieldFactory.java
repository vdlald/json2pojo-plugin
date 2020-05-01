package com.vladislav.jsontopojo.factories;

import com.google.gson.JsonPrimitive;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

public interface FieldFactory {

    JFieldVar createPrimitiveField(JDefinedClass clazz, int fieldMods, String fieldName, JsonPrimitive jsonPrimitive);

    JFieldVar createBooleanField(JDefinedClass clazz, int fieldMods, String fieldName);

    JFieldVar createIntField(JDefinedClass clazz, int fieldMods, String fieldName);

    JFieldVar createLongField(JDefinedClass clazz, int fieldMods, String fieldName);

    JFieldVar createDoubleField(JDefinedClass clazz, int fieldMods, String fieldName);

    JFieldVar createStringField(JDefinedClass clazz, int fieldMods, String fieldName);

}
