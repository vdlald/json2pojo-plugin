package com.vladislav.jsontopojo.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.vladislav.jsontopojo.DeserializeAnnotation;
import com.vladislav.jsontopojo.Setting;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingDialog extends JDialog {
    private JPanel contentPanel;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JComboBox<DeserializeAnnotation> annotateDeserializeFieldWith;
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
    private JCheckBox vanillaUseFinalFields;
    private JPanel vanillaSettings;
    private JPanel lombokSettings;
    private JPanel otherSettings;
    private JCheckBox сonvertCyrillicCharactersIntoCheckBox;
    private JCheckBox suppressUnusedCheckBox;
    private JCheckBox useLongIntegersCheckBox;
    private JCheckBox useDoubleNumbersCheckBox;
    private JCheckBox addAdditionalPropertiesCheckBox;
    private JCheckBox lombokEqualsAndHashCodeCheckBox;
    private JCheckBox vanillaHashCodeCheckBox;
    private JCheckBox vanillaEqualsCheckBox;
    private JCheckBox vanillaToStringCheckBox;

    private final Setting setting = Setting.getInstance();

    public SettingDialog() {
        $$$setupUI$$$();
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
        сonvertCyrillicCharactersIntoCheckBox.setSelected(setting.isCyrillicToLatin());
        suppressUnusedCheckBox.setSelected(setting.isSuppressUnused());
        annotateDeserializeFieldWith.setSelectedItem(setting.getAnnotateDeserializeFieldWith());
        usePrimitiveTypes.setSelected(setting.isUsePrimitiveTypes());
        vanillaJsonToPojo.setSelected(setting.isVanillaJsonToPojo());
        lombokJsonToPojo.setSelected(setting.isLombokJsonToPojo());
        vanillaNoArgsConstructor.setSelected(setting.isVanillaNoArgsConstructor());
        vanillaAllArgsConstructor.setSelected(setting.isVanillaAllArgsConstructor());
        vanillaGetters.setSelected(setting.isVanillaGetters());
        vanillaSetters.setSelected(setting.isVanillaSetters());
        vanillaUseFinalFields.setSelected(setting.isVanillaUseFinalFields());
        lombokData.setSelected(setting.isLombokData());
        lombokValue.setSelected(setting.isLombokValue());
        lombokBuilder.setSelected(setting.isLombokBuilder());
        lombokNoArgsConstructor.setSelected(setting.isLombokNoArgsConstructor());
        lombokRequiredArgsConstructor.setSelected(setting.isLombokRequiredArgsConstructor());
        lombokAllArgsConstructor.setSelected(setting.isLombokAllArgsConstructor());
        lombokGetter.setSelected(setting.isLombokGetter());
        lombokSetter.setSelected(setting.isLombokSetter());
        lombokGetterOnClass.setSelected(setting.isLombokGetterOnClass());
        lombokSetterOnClass.setSelected(setting.isLombokSetterOnClass());
        useLongIntegersCheckBox.setSelected(setting.isUseLongIntegers());
        useDoubleNumbersCheckBox.setSelected(setting.isUseDoubleNumbers());
        addAdditionalPropertiesCheckBox.setSelected(setting.isAddAdditionalProperties());
        lombokEqualsAndHashCodeCheckBox.setSelected(setting.isLombokEqualsAndHashCode());
        vanillaHashCodeCheckBox.setSelected(setting.isVanillaHashCode());
        vanillaEqualsCheckBox.setSelected(setting.isVanillaEquals());
        vanillaToStringCheckBox.setSelected(setting.isVanillaToString());
    }

    private void onOK() {
        setting.setAnnotateDeserializeFieldWith((DeserializeAnnotation) annotateDeserializeFieldWith.getSelectedItem())
                .setCyrillicToLatin(сonvertCyrillicCharactersIntoCheckBox.isSelected())
                .setSuppressUnused(suppressUnusedCheckBox.isSelected())
                .setUseLongIntegers(useLongIntegersCheckBox.isSelected())
                .setUseDoubleNumbers(useDoubleNumbersCheckBox.isSelected())
                .setAddAdditionalProperties(addAdditionalPropertiesCheckBox.isSelected())
                .setUsePrimitiveTypes(usePrimitiveTypes.isSelected())
                .setVanillaJsonToPojo(vanillaJsonToPojo.isSelected())
                .setLombokJsonToPojo(lombokJsonToPojo.isSelected())
                .setVanillaNoArgsConstructor(vanillaNoArgsConstructor.isSelected())
                .setVanillaAllArgsConstructor(vanillaAllArgsConstructor.isSelected())
                .setVanillaGetters(vanillaGetters.isSelected())
                .setVanillaSetters(vanillaSetters.isSelected())
                .setVanillaHashCode(vanillaHashCodeCheckBox.isSelected())
                .setVanillaEquals(vanillaEqualsCheckBox.isSelected())
                .setVanillaToString(vanillaToStringCheckBox.isSelected())
                .setVanillaUseFinalFields(vanillaUseFinalFields.isSelected())
                .setLombokData(lombokData.isSelected())
                .setLombokValue(lombokValue.isSelected())
                .setLombokBuilder(lombokBuilder.isSelected())
                .setLombokNoArgsConstructor(lombokNoArgsConstructor.isSelected())
                .setLombokRequiredArgsConstructor(lombokRequiredArgsConstructor.isSelected())
                .setLombokAllArgsConstructor(lombokAllArgsConstructor.isSelected())
                .setLombokGetter(lombokGetter.isSelected())
                .setLombokSetter(lombokSetter.isSelected())
                .setLombokEqualsAndHashCode(lombokEqualsAndHashCodeCheckBox.isSelected())
                .setLombokGetterOnClass(lombokGetterOnClass.isSelected())
                .setLombokSetterOnClass(lombokSetterOnClass.isSelected());
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        final DeserializeAnnotation[] values = DeserializeAnnotation.values();
        annotateDeserializeFieldWith = new ComboBox<>(values);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "General", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("When generated field name is different, annotate");
        panel2.add(label1);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        annotateDeserializeFieldWith.setModel(defaultComboBoxModel1);
        panel2.add(annotateDeserializeFieldWith);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lombokJsonToPojo = new JRadioButton();
        lombokJsonToPojo.setSelected(true);
        lombokJsonToPojo.setText("Generate Lombok POJO");
        panel3.add(lombokJsonToPojo);
        vanillaJsonToPojo = new JRadioButton();
        vanillaJsonToPojo.setText("Generate Vanilla POJO");
        panel3.add(vanillaJsonToPojo);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel1.add(panel4, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        usePrimitiveTypes = new JCheckBox();
        usePrimitiveTypes.setText("Use unboxed types");
        panel4.add(usePrimitiveTypes);
        useLongIntegersCheckBox = new JCheckBox();
        useLongIntegersCheckBox.setText("Use long integers");
        panel4.add(useLongIntegersCheckBox);
        useDoubleNumbersCheckBox = new JCheckBox();
        useDoubleNumbersCheckBox.setText("Use double numbers");
        panel4.add(useDoubleNumbersCheckBox);
        lombokSettings = new JPanel();
        lombokSettings.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(lombokSettings, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lombokSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Lombok", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        lombokSettings.add(panel5, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lombokNoArgsConstructor = new JCheckBox();
        lombokNoArgsConstructor.setText("@NoArgsConstructor");
        panel5.add(lombokNoArgsConstructor);
        lombokRequiredArgsConstructor = new JCheckBox();
        lombokRequiredArgsConstructor.setText("@RequiredArgsConstructor");
        panel5.add(lombokRequiredArgsConstructor);
        lombokAllArgsConstructor = new JCheckBox();
        lombokAllArgsConstructor.setText("@AllArgsConstructor");
        panel5.add(lombokAllArgsConstructor);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        lombokSettings.add(panel6, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lombokGetterOnClass = new JCheckBox();
        lombokGetterOnClass.setSelected(false);
        lombokGetterOnClass.setText("Annotate @Getter on the class");
        panel6.add(lombokGetterOnClass);
        lombokSetterOnClass = new JCheckBox();
        lombokSetterOnClass.setSelected(false);
        lombokSetterOnClass.setText("Annotate @Setter on the class");
        panel6.add(lombokSetterOnClass);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        lombokSettings.add(panel7, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lombokData = new JCheckBox();
        lombokData.setSelected(false);
        lombokData.setText("@Data");
        panel7.add(lombokData);
        lombokValue = new JCheckBox();
        lombokValue.setSelected(false);
        lombokValue.setText("@Value");
        panel7.add(lombokValue);
        lombokBuilder = new JCheckBox();
        lombokBuilder.setSelected(false);
        lombokBuilder.setText("@Builder");
        panel7.add(lombokBuilder);
        lombokGetter = new JCheckBox();
        lombokGetter.setSelected(false);
        lombokGetter.setText("@Getter");
        panel7.add(lombokGetter);
        lombokSetter = new JCheckBox();
        lombokSetter.setSelected(false);
        lombokSetter.setText("@Setter");
        panel7.add(lombokSetter);
        lombokEqualsAndHashCodeCheckBox = new JCheckBox();
        lombokEqualsAndHashCodeCheckBox.setText("@EqualsAndHashCode");
        panel7.add(lombokEqualsAndHashCodeCheckBox);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        contentPanel.add(panel8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel8.add(buttonCancel);
        buttonOK = new JButton();
        buttonOK.setText("Ok");
        panel8.add(buttonOK);
        vanillaSettings = new JPanel();
        vanillaSettings.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(vanillaSettings, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        vanillaSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Vanilla", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        vanillaSettings.add(panel9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        vanillaNoArgsConstructor = new JCheckBox();
        vanillaNoArgsConstructor.setText("No args constructor");
        panel9.add(vanillaNoArgsConstructor);
        vanillaAllArgsConstructor = new JCheckBox();
        vanillaAllArgsConstructor.setText("All args constructor");
        panel9.add(vanillaAllArgsConstructor);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        vanillaSettings.add(panel10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        vanillaGetters = new JCheckBox();
        vanillaGetters.setSelected(false);
        vanillaGetters.setText("Getters");
        panel10.add(vanillaGetters);
        vanillaSetters = new JCheckBox();
        vanillaSetters.setSelected(false);
        vanillaSetters.setText("Setters");
        panel10.add(vanillaSetters);
        vanillaUseFinalFields = new JCheckBox();
        vanillaUseFinalFields.setText("Use final fields");
        panel10.add(vanillaUseFinalFields);
        vanillaHashCodeCheckBox = new JCheckBox();
        vanillaHashCodeCheckBox.setText("HashCode");
        panel10.add(vanillaHashCodeCheckBox);
        vanillaEqualsCheckBox = new JCheckBox();
        vanillaEqualsCheckBox.setText("Equals");
        panel10.add(vanillaEqualsCheckBox);
        vanillaToStringCheckBox = new JCheckBox();
        vanillaToStringCheckBox.setText("ToString");
        panel10.add(vanillaToStringCheckBox);
        otherSettings = new JPanel();
        otherSettings.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(otherSettings, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        otherSettings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Other", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        otherSettings.add(panel11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        сonvertCyrillicCharactersIntoCheckBox = new JCheckBox();
        сonvertCyrillicCharactersIntoCheckBox.setText("Сonvert Cyrillic characters into Latin characters");
        panel11.add(сonvertCyrillicCharactersIntoCheckBox);
        suppressUnusedCheckBox = new JCheckBox();
        suppressUnusedCheckBox.setText("Suppress unused");
        panel11.add(suppressUnusedCheckBox);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        otherSettings.add(panel12, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addAdditionalPropertiesCheckBox = new JCheckBox();
        addAdditionalPropertiesCheckBox.setText("Add additionalProperties");
        panel12.add(addAdditionalPropertiesCheckBox);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(lombokJsonToPojo);
        buttonGroup.add(vanillaJsonToPojo);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }
}
