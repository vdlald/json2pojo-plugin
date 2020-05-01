package com.vladislav.jsontopojo.factories;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;

public class PrimitiveFieldFactory extends AbstractFieldFactory {

    public PrimitiveFieldFactory(JCodeModel model) {
        super(model);
    }

    @Override
    public JFieldVar createBooleanField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.BOOLEAN, fieldName);
    }

    @Override
    public JFieldVar createIntField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.INT, fieldName);
    }

    @Override
    public JFieldVar createLongField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.LONG, fieldName);
    }

    @Override
    public JFieldVar createDoubleField(JDefinedClass clazz, int fieldMods, String fieldName) {
        return clazz.field(fieldMods, model.DOUBLE, fieldName);
    }
}
