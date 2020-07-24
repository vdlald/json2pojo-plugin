package com.vladislav.jsontopojo;

import com.squareup.javapoet.ClassName;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DeserializeAnnotation {
    NONE("Don't annotate", null),
    SERIALIZED_NAME("@SerializedName", ClassName.get("com.google.gson.annotations", "SerializedName")),
    JSON_PROPERTY("@JsonProperty", ClassName.get("com.fasterxml.jackson.annotation", "JsonProperty"));

    public final String name;
    public final ClassName typeName;

    @Override
    public String toString() {
        return name;
    }
}
