package com.vladislav.jsontopojo.plugin.ui;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladislav.jsontopojo.JsonToLombokPojo;
import com.vladislav.jsontopojo.JsonToVanillaPojo;
import com.vladislav.jsontopojo.Utils;
import com.vladislav.jsontopojo.plugin.Setting;
import com.vladislav.jsontopojo.plugin.Settings;
import org.jetbrains.annotations.NotNull;

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
                        if (setting.isLombokJsonToPojo()) {
                            JsonToLombokPojo.newBuilder(destinationPath, packageName, indicator)
                                    .setFieldAnnotations(Settings.getFieldAnnotations(setting))
                                    .setClassAnnotations(Settings.getClassAnnotations(setting))
                                    .setPrimitiveFields(setting.isUsePrimitiveTypes())
                                    .build()
                                    .apply(jsonTextArea.getText(), className.getText());
                        } else {
                            JsonToVanillaPojo.newBuilder(destinationPath, packageName, indicator)
                                    .setFieldAnnotations(Settings.getFieldAnnotations(setting))
                                    .setClassAnnotations(Settings.getClassAnnotations(setting))
                                    .setPrimitiveFields(setting.isUsePrimitiveTypes())
                                    .setCreateGetters(setting.isVanillaGetters())
                                    .setCreateSetters(setting.isVanillaSetters())
                                    .setCreateAllArgsConstructor(setting.isLombokAllArgsConstructor())
                                    .setCreateNoArgsConstructor(setting.isVanillaNoArgsConstructor())
                                    .setCreateFinalFields(setting.isVanillaUseFinalFields())
                                    .build()
                                    .apply(jsonTextArea.getText(), className.getText());
                        }
                        ProjectView.getInstance(project).refresh();
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

}
