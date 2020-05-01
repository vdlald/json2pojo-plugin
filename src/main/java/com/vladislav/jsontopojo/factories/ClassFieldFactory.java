package com.vladislav.jsontopojo.factories;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;

public class ClassFieldFactory extends AbstractFieldFactory {

    public ClassFieldFactory(JCodeModel model) {
        super(model);
    }

    @Override
    public JFieldVar createBooleanField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.ref(Boolean.class), fieldName);
    }

    @Override
    public JFieldVar createIntField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.ref(Integer.class), fieldName);
    }

    @Override
    public JFieldVar createLongField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.ref(Long.class), fieldName);
    }

    @Override
    public JFieldVar createDoubleField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.ref(Double.class), fieldName);
    }
}
