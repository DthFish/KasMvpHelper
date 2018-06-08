package com.kascend.mvphelper.ui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GeneratorHelper extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton rbActivity;
    private JRadioButton rbFragment;
    private JTextField tfName;
    private JTextField tfNameDesc;
    private JTextField tfNotice;
    private JCheckBox cbNewPackage;
    private OnDismissListener listener;

    public GeneratorHelper() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onOK() {
        String name = tfName.getText().trim();
        if (name.equals("")) {
            tfNotice.setText("Must fill in the name！");
        } else {
            dispose();
            if (listener != null) {
                listener.onConfirm(name, rbActivity.isSelected(), cbNewPackage.isSelected());
            }

        }
    }

    private void onCancel() {
        if (listener != null) {
            listener.onCancel();
        }
        dispose();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    public interface OnDismissListener {
        void onConfirm(String name, boolean isActivity, boolean shouldNewPackage);

        void onCancel();
    }

    public static void main(String[] args) {
//        GeneratorHelper dialog = new GeneratorHelper();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);

    }
}
