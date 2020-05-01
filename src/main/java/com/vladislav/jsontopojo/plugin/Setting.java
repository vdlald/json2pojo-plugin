package com.vladislav.jsontopojo.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.*;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Data
@Accessors(chain = true)
public class Setting implements Serializable {

    private static volatile Setting instance;
    private static String SAVE_PATH = System.getProperty("user.home") + "/.jsonToPojo";
    private static String SETTINGS_FILENAME = "settings.out";

    private boolean fieldTypePrimitive = false;
    private int fieldNameAnnotation = 0;

    private boolean vanillaNoArgsConstructor;
    private boolean vanillaAllArgsConstructor;
    private boolean vanillaGetters;
    private boolean vanillaSetters;

    private boolean lombokData = true;
    private boolean lombokValue = false;
    private boolean lombokBuilder = false;
    private boolean lombokNoArgsConstructor = false;
    private boolean lombokRequiredArgsConstructor = true;
    private boolean lombokAllArgsConstructor = false;
    private boolean lombokGetter = false;
    private boolean lombokSetter = false;
    private boolean lombokSetterOnClass = true;
    private boolean lombokGetterOnClass = true;

    private int windowWidth = 500;
    private int windowHeight = 450;
    private int windowX = 100;
    private int windowY = 100;

    public void save() {
        final Path path = Paths.get(SAVE_PATH);

        try {
            Files.createDirectory(path);
        } catch (IOException ignored) {
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                new FileOutputStream(path + "/" + SETTINGS_FILENAME, false)
        )) {
            outputStream.writeObject(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Setting restore() {
        final Path path = Paths.get(SAVE_PATH);

        try (ObjectInputStream inputStream = new ObjectInputStream(
                new FileInputStream(path + "/" + SETTINGS_FILENAME)
        )) {
            return (Setting) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return new Setting();
    }

    public Class<? extends Annotation> getFieldAnnotation() {
        return new ArrayList<Class<? extends Annotation>>() {{
            add(null);
            add(SerializedName.class);
            add(JsonProperty.class);
        }}.get(this.fieldNameAnnotation);
    }

    public Set<Class<? extends Annotation>> getClassAnnotations() {
        Set<Class<? extends Annotation>> annotations = new HashSet<>() {{
            if (lombokData) add(Data.class);
            if (lombokValue) add(Value.class);
            if (lombokBuilder) add(Builder.class);
            if (lombokNoArgsConstructor) add(NoArgsConstructor.class);
            if (lombokRequiredArgsConstructor) add(RequiredArgsConstructor.class);
            if (lombokAllArgsConstructor) add(AllArgsConstructor.class);
        }};
        return Collections.unmodifiableSet(annotations);
    }

    public static Setting getInstance() {
        Setting localInstance = instance;
        if (localInstance == null) {
            synchronized (Setting.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = restore();
                }
            }
        }
        return localInstance;
    }
}
