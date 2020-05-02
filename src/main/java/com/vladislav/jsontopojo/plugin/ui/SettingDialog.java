package com.vladislav.jsontopojo.plugin.ui;

import com.vladislav.jsontopojo.plugin.Setting;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingDialog extends JDialog {
    private JPanel contentPanel;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JComboBox<String> annotateDeserializeFieldWith;
    private JCheckBox usePrimitiveTypes;

    private JCheckBox vanillaNoArgsConstructor;
    private JCheckBox vanillaAllArgsConstructor;
    private JCheckBox vanillaGetters;
    private JCheckBox vanillaSetters;

    private JCheckBox lombokData;
    private JCheckBox lombokValue;
    private JCheckBox lombokBuilder;
    private JCheckBox lombokNoArgsConstructor;
    private JCheckBox lombokRequiredArgsConstructor;
    private JCheckBox lombokAllArgsConstructor;
    private JCheckBox lombokGetter;
    private JCheckBox lombokSetter;
    private JCheckBox lombokSetterOnClass;
    private JCheckBox lombokGetterOnClass;
    private JRadioButton vanillaJsonToPojo;
    private JRadioButton lombokJsonToPojo;
    private JCheckBox useFinalFieldsCheckBox;
    private JPanel vanillaSettings;
    private JPanel lombokSettings;

    private final Setting setting = Setting.getInstance();

    public SettingDialog() {
        setContentPane(contentPanel);
        setModal(true);
        setResizable(false);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        bindConfig();
    }

    private void bindConfig() {
        usePrimitiveTypes.setSelected(setting.isFieldTypePrimitive());
        annotateDeserializeFieldWith.setSelectedIndex(setting.getFieldNameAnnotation());

        vanillaNoArgsConstructor.setSelected(setting.isVanillaNoArgsConstructor());
        vanillaAllArgsConstructor.setSelected(setting.isVanillaAllArgsConstructor());
        vanillaGetters.setSelected(setting.isVanillaGetters());
        vanillaSetters.setSelected(setting.isVanillaSetters());

        lombokData.setSelected(setting.isLombokData());
        lombokValue.setSelected(setting.isLombokValue());
        lombokBuilder.setSelected(setting.isLombokBuilder());
        lombokNoArgsConstructor.setSelected(setting.isLombokNoArgsConstructor());
        lombokRequiredArgsConstructor.setSelected(setting.isLombokRequiredArgsConstructor());
        lombokAllArgsConstructor.setSelected(setting.isLombokAllArgsConstructor());
        lombokGetter.setSelected(setting.isLombokGetter());
        lombokSetter.setSelected(setting.isLombokSetter());
        lombokGetterOnClass.setSelected(setting.isLombokGetter());
        lombokSetterOnClass.setSelected(setting.isLombokSetter());
    }

    private void onOK() {
        setting.setLombokData(lombokData.isSelected())
                .setLombokValue(lombokValue.isSelected())
                .setLombokBuilder(lombokBuilder.isSelected())
                .setLombokNoArgsConstructor(lombokNoArgsConstructor.isSelected())
                .setLombokRequiredArgsConstructor(lombokRequiredArgsConstructor.isSelected())
                .setLombokAllArgsConstructor(lombokAllArgsConstructor.isSelected())
                .setLombokGetter(lombokGetter.isSelected())
                .setLombokSetter(lombokSetter.isSelected())
                .setLombokSetterOnClass(lombokSetterOnClass.isSelected())
                .setLombokGetterOnClass(lombokGetterOnClass.isSelected())
                .setFieldTypePrimitive(usePrimitiveTypes.isSelected())
                .setFieldNameAnnotation(annotateDeserializeFieldWith.getSelectedIndex())
                .setVanillaAllArgsConstructor(vanillaAllArgsConstructor.isSelected())
                .setVanillaNoArgsConstructor(vanillaNoArgsConstructor.isSelected())
                .setVanillaGetters(vanillaGetters.isSelected())
                .setVanillaSetters(vanillaSetters.isSelected())
                .save();
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
