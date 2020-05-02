package com.vladislav.jsontopojo.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class Settings {

    public Set<Class<? extends Annotation>> getClassAnnotations(Setting setting) {
        Set<Class<? extends Annotation>> annotations = new HashSet<>() {{
            if (setting.isLombokJsonToPojo()) {
                if (setting.isLombokData()) add(Data.class);
                if (setting.isLombokValue()) add(Value.class);
                if (setting.isLombokBuilder()) add(Builder.class);
                if (setting.isLombokNoArgsConstructor()) add(NoArgsConstructor.class);
                if (setting.isLombokRequiredArgsConstructor()) add(RequiredArgsConstructor.class);
                if (setting.isLombokAllArgsConstructor()) add(AllArgsConstructor.class);
                if (setting.isLombokGetterOnClass() && setting.isLombokGetter()) {
                    add(Getter.class);
                }
                if (setting.isLombokSetterOnClass() && setting.isLombokSetter()) {
                    add(Setter.class);
                }
            }
        }};
        return Collections.unmodifiableSet(annotations);
    }

    public Set<Class<? extends Annotation>> getFieldAnnotations(Setting setting) {
        return new HashSet<>() {{
            if (setting.getAnnotateDeserializeFieldWith() == 0) {
                add(SerializedName.class);
            } else if (setting.getAnnotateDeserializeFieldWith() == 1) {
                add(JsonProperty.class);
            }
            if (setting.isLombokJsonToPojo()) {
                if (!setting.isLombokGetterOnClass() && setting.isLombokGetter()) {
                    add(Getter.class);
                }
                if (!setting.isLombokSetterOnClass() && setting.isLombokSetter()) {
                    add(Setter.class);
                }
            }
        }};
    }

}
