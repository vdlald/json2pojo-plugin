package com.vladislav.jsontopojo.plugin;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Accessors(chain = true)
public class Setting implements Serializable {

    private static volatile Setting instance;
    private static final Path path = Paths.get(System.getProperty("user.home") + "/.jsonToPojo");
    private static String SETTINGS_FILENAME = "settings.out";

    private int annotateDeserializeFieldWith = 0;
    private boolean usePrimitiveTypes = false;
    private boolean vanillaJsonToPojo = false;
    private boolean lombokJsonToPojo = true;

    private boolean vanillaNoArgsConstructor = true;
    private boolean vanillaAllArgsConstructor = true;
    private boolean vanillaGetters = true;
    private boolean vanillaSetters = true;
    private boolean vanillaUseFinalFields = false;

    private boolean lombokData = true;
    private boolean lombokValue = false;
    private boolean lombokBuilder = false;
    private boolean lombokNoArgsConstructor = true;
    private boolean lombokRequiredArgsConstructor = false;
    private boolean lombokAllArgsConstructor = true;
    private boolean lombokGetter = false;
    private boolean lombokSetter = false;
    private boolean lombokSetterOnClass = false;
    private boolean lombokGetterOnClass = false;

    private int windowWidth = 500;
    private int windowHeight = 450;
    private int windowX = 100;
    private int windowY = 100;

    public void save() {
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
        try (ObjectInputStream inputStream = new ObjectInputStream(
                new FileInputStream(path + "/" + SETTINGS_FILENAME)
        )) {
            return (Setting) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return new Setting();
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

    public Setting setVanillaJsonToPojo(boolean vanillaJsonToPojo) {
        lombokJsonToPojo = !vanillaJsonToPojo;
        this.vanillaJsonToPojo = vanillaJsonToPojo;
        return this;
    }

    public Setting setLombokJsonToPojo(boolean lombokJsonToPojo) {
        vanillaJsonToPojo = !lombokJsonToPojo;
        this.lombokJsonToPojo = lombokJsonToPojo;
        return this;
    }
}
