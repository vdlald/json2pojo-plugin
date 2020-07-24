package com.vladislav.jsontopojo.ui;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.squareup.javapoet.*;
import com.vladislav.json2pojo.GeneratePojoFromJson;
import com.vladislav.json2pojo.GeneratePojoFromJsonBuilder;
import com.vladislav.jsontopojo.DeserializeAnnotation;
import com.vladislav.jsontopojo.Setting;
import com.vladislav.jsontopojo.Utils;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class GeneratorDialog extends JDialog {
    private final Project project;
    private final String packageName;
    private final String destinationPath;
    private final VirtualFile actionFolder;
    private final Setting setting;

    private JPanel contentPane;
    private JButton generate;
    private JButton cancel;
    private JTextArea jsonTextArea;
    private JTextField className;
    private JButton settings;

    public GeneratorDialog(Project project, String packageName, VirtualFile actionFolder) {
        this.project = project;
        this.packageName = packageName;
        this.actionFolder = actionFolder;
        this.destinationPath = actionFolder.getPath();
        setting = Setting.getInstance();

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(generate);
        setLocation(setting.getWindowX(), setting.getWindowY());
        setSize(setting.getWindowWidth(), setting.getWindowHeight());
        setMinimumSize(new Dimension(420, 400));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                setting.setWindowHeight(getHeight())
                        .setWindowWidth(getWidth())
                        .setWindowX(getX())
                        .setWindowY(getY())
                        .save();
            }
        });

        generate.addActionListener(e -> onOK());
        cancel.addActionListener(e -> onCancel());
        settings.addActionListener(e -> {
            SettingDialog dialog = new SettingDialog();
            dialog.setTitle("settings");
            dialog.setLocationRelativeTo(this);
            dialog.pack();
            dialog.setVisible(true);
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        generate.setEnabled(false);
        final DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validate();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validate();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validate();
            }

            private void validate() {
                generate.setEnabled(
                        Utils.isValidClassName(className.getText()) && Utils.isJsonObject(jsonTextArea.getText())
                );
            }
        };
        className.getDocument().addDocumentListener(documentListener);
        jsonTextArea.getDocument().addDocumentListener(documentListener);

        try {
            final String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
            if (Utils.isJsonObject(data)) {
                jsonTextArea.setText(data);
            }
        } catch (UnsupportedFlavorException | IOException ignore) {
        }
    }

    private void onOK() {
        ProgressManager.getInstance().run(
                new Task.Backgroundable(project, "Generate a Set of POJOs from JSON", false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        final GeneratePojoFromJsonBuilder builder = GeneratePojoFromJson.builder();
                        if (setting.isCyrillicToLatin()) {
                            builder.addBeforeCreationClass(classNameRef -> classNameRef.className = Utils.cyrillicToLatin(classNameRef.className));
                            builder.addBeforeFieldCreationConsumers(fieldNameRef -> fieldNameRef.fieldName = Utils.cyrillicToLatin(fieldNameRef.fieldName));
                        }
                        if (setting.isUseLongIntegers()) {
                            builder.useLongIntegers(true);
                        }
                        if (setting.isUseDoubleNumbers()) {
                            builder.useDoubleNumbers(true);
                        }
                        if (setting.isAddAdditionalProperties()) {
                            builder.addAfterCreationClass(wrapper -> {
                                final TypeSpec.Builder typeSpec = wrapper.typeSpec;
                                final ParameterizedTypeName typeName = ParameterizedTypeName.get(
                                        Map.class, String.class, Object.class);
                                final String fieldName = "additionalProperties";
                                final FieldSpec additionalProperties = wrapper.fieldFactory
                                        .createField(typeName, fieldName);
                                typeSpec.addField(additionalProperties);
                                typeSpec.addMethod(MethodSpec.methodBuilder("get" + Utils.firstLetterToUpperCase(fieldName))
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(typeName)
                                        .addStatement("return this.$N", fieldName)
                                        .addAnnotation(JsonAnyGetter.class)
                                        .build());
                                typeSpec.addMethod(MethodSpec.methodBuilder("set" + Utils.firstLetterToUpperCase(fieldName))
                                        .addModifiers(Modifier.PUBLIC)
                                        .addParameter(String.class, "name")
                                        .addParameter(Object.class, "value")
                                        .addStatement("this.$N.put(name, value)", fieldName)
                                        .addAnnotation(JsonAnySetter.class)
                                        .build());
                            });
                        }
                        builder.addAfterFieldCreationConsumers(wrapper -> wrapper.fieldBuilder.addModifiers(Modifier.PRIVATE));
                        builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addModifiers(Modifier.PUBLIC));
                        if (setting.isUsePrimitiveTypes()) {
                            builder.useUnboxedPrimitive();
                        } else {
                            builder.useBoxedPrimitive();
                        }
                        final DeserializeAnnotation annotateDeserializeFieldWith = setting.getAnnotateDeserializeFieldWith();
                        if (annotateDeserializeFieldWith != DeserializeAnnotation.NONE) {
                            builder.addAfterFieldCreationConsumers(wrapper -> {
                                if (!wrapper.fieldName.equals(wrapper.fieldNameFormatted)) {
                                    final AnnotationSpec jsonProperty = AnnotationSpec.builder(annotateDeserializeFieldWith.typeName)
                                            .addMember("value", String.format("\"%s\"", wrapper.fieldName))
                                            .build();
                                    wrapper.fieldBuilder.addAnnotation(jsonProperty);
                                }
                            });
                        }
                        if (setting.isLombokJsonToPojo()) {
                            if (setting.isLombokData()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(Data.class));
                            } else if (setting.isLombokValue()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(Value.class));
                            }
                            if (setting.isLombokBuilder()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(Builder.class));
                            }
                            if (setting.isLombokNoArgsConstructor()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(NoArgsConstructor.class));
                            }
                            if (setting.isLombokRequiredArgsConstructor()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(RequiredArgsConstructor.class));
                            }
                            if (setting.isLombokAllArgsConstructor()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(AllArgsConstructor.class));
                            }
                            if (setting.isLombokGetter()) {
                                builder.addAfterFieldCreationConsumers(wrapper -> wrapper.fieldBuilder.addAnnotation(Getter.class));
                            } else if (setting.isLombokGetterOnClass()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(Getter.class));
                            }
                            if (setting.isLombokEqualsAndHashCode()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(EqualsAndHashCode.class));
                            }
                            if (setting.isLombokGetter()) {
                                builder.addAfterFieldCreationConsumers(wrapper -> wrapper.fieldBuilder.addAnnotation(Getter.class));
                            } else if (setting.isLombokGetterOnClass()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(Getter.class));
                            }
                        } else {
                            if (setting.isVanillaNoArgsConstructor()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addMethod(MethodSpec.constructorBuilder()
                                        .addModifiers(Modifier.PUBLIC)
                                        .build()));
                            }
                            if (setting.isVanillaAllArgsConstructor()) {
                                builder.addAfterCreationClass(wrapper -> {
                                    final MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                                            .addModifiers(Modifier.PUBLIC);
                                    wrapper.typeSpec.fieldSpecs.forEach(fieldSpec -> {
                                        final String name = fieldSpec.name;
                                        constructor.addParameter(fieldSpec.type, name)
                                                .addStatement("this.$N = $N", name, name);
                                    });
                                    wrapper.typeSpec.addMethod(constructor.build());
                                });
                            }
                            if (setting.isVanillaGetters()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.fieldSpecs.forEach(fieldSpec -> {
                                    final String fieldName = fieldSpec.name;
                                    final String methodName = "get" + Utils.firstLetterToUpperCase(fieldName);
                                    final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                                            .returns(fieldSpec.type)
                                            .addStatement("return $N", fieldName)
                                            .addModifiers(Modifier.PUBLIC)
                                            .build();
                                    wrapper.typeSpec.addMethod(methodSpec);
                                }));
                            }
                            if (setting.isVanillaSetters()) {
                                builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.fieldSpecs.forEach(fieldSpec -> {
                                    final String fieldName = fieldSpec.name;
                                    final String methodName = "set" + Utils.firstLetterToUpperCase(fieldName);
                                    final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                                            .addParameter(fieldSpec.type, fieldName)
                                            .addStatement("this.$N = $N", fieldName, fieldName)
                                            .addModifiers(Modifier.PUBLIC)
                                            .build();
                                    wrapper.typeSpec.addMethod(methodSpec);
                                }));
                            }
                            if (setting.isVanillaUseFinalFields()) {
                                builder.addAfterFieldCreationConsumers(wrapper -> wrapper.fieldBuilder.addModifiers(Modifier.FINAL));
                            }
                            if (setting.isVanillaHashCode()) {
                                builder.addAfterCreationClass(wrapper -> {
                                    final TypeSpec.Builder typeSpec = wrapper.typeSpec;
                                    StringBuilder statement = new StringBuilder();
                                    typeSpec.fieldSpecs.forEach(fieldSpec -> statement.append(".append(")
                                            .append(fieldSpec.name).append(")"));
                                    statement.append(".toHashCode()");
                                    typeSpec.addMethod(MethodSpec.methodBuilder("hashCode")
                                            .returns(TypeName.INT)
                                            .addModifiers(Modifier.PUBLIC)
                                            .addAnnotation(Override.class)
                                            .addStatement("return new $T()" + statement.toString(),
                                                    ClassName.get("org.apache.commons.lang3.builder", "HashCodeBuilder"))
                                            .build());
                                });
                            }
                            if (setting.isVanillaEquals()) {
                                builder.addAfterCreationClass(wrapper -> {
                                    final TypeSpec.Builder typeSpec = wrapper.typeSpec;
                                    StringBuilder statement = new StringBuilder();
                                    typeSpec.fieldSpecs.forEach(fieldSpec -> statement.append(".append(")
                                            .append(fieldSpec.name).append(", casted.").append(fieldSpec.name).append(")"));
                                    statement.append(".isEquals()");
                                    final String className = wrapper.className;
                                    typeSpec.addMethod(MethodSpec.methodBuilder("equals")
                                            .returns(TypeName.BOOLEAN)
                                            .addModifiers(Modifier.PUBLIC)
                                            .addAnnotation(Override.class)
                                            .addParameter(Object.class, "other")
                                            .beginControlFlow("if (other == this)")
                                            .addStatement("return true")
                                            .nextControlFlow("else if ((other instanceof $N))", className)
                                            .addStatement("return false")
                                            .endControlFlow()
                                            .addStatement("$N casted = ($N) other", className, className)
                                            .addStatement("return new $T()" + statement.toString(),
                                                    ClassName.get("org.apache.commons.lang3.builder", "EqualsBuilder"))
                                            .build());
                                });
                            }
                            if (setting.isVanillaToString()) {
                                builder.addAfterCreationClass(wrapper -> {
                                    final TypeSpec.Builder typeSpec = wrapper.typeSpec;
                                    StringBuilder statement = new StringBuilder();
                                    typeSpec.fieldSpecs.forEach(fieldSpec -> statement.append(".append(")
                                            .append(fieldSpec.name).append(")"));
                                    statement.append(".toString()");
                                    typeSpec.addMethod(MethodSpec.methodBuilder("toString")
                                            .returns(String.class)
                                            .addModifiers(Modifier.PUBLIC)
                                            .addAnnotation(Override.class)
                                            .addStatement("return new $T(this)" + statement.toString(),
                                                    ClassName.get("org.apache.commons.lang3.builder", "ToStringBuilder"))
                                            .build());
                                });
                            }
                        }
                        if (setting.isSuppressUnused()) {
                            builder.addAfterCreationClass(wrapper -> wrapper.typeSpec.addAnnotation(
                                    AnnotationSpec.builder(SuppressWarnings.class)
                                            .addMember("value", "\"unused\"")
                                            .build()));
                        }
                        final GeneratePojoFromJson generatePojoFromJson = builder.build();
                        final JavaFile javaFile = generatePojoFromJson.invoke(packageName, className.getText(),
                                jsonTextArea.getText());
                        final Path path = Paths.get(destinationPath);
                        try {
                            javaFile.writeTo(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        actionFolder.refresh(false, true);
                    }
                }
        );
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        cancel = new JButton();
        cancel.setText("Cancel");
        panel1.add(cancel);
        settings = new JButton();
        settings.setText("Setting");
        panel1.add(settings);
        generate = new JButton();
        generate.setText("Generate");
        panel1.add(generate);
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPane.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        jsonTextArea = new JTextArea();
        scrollPane1.setViewportView(jsonTextArea);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        className = new JTextField();
        panel2.add(className, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Class name:");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
