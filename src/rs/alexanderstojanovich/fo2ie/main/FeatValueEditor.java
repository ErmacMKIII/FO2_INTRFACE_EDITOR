/*
 * Copyright (C) 2021 Alexander Stojanovich <coas91@rocketmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rs.alexanderstojanovich.fo2ie.main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.MyVector4;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class FeatValueEditor extends JFrame {

    private final FeatureKey featureKey;
    private final FeatureValue featureValue;
    private final JButton btnSet = new JButton("Set");
    private final JButton btnReset = new JButton("Reset");
    private final JButton btnCancel = new JButton("Cancel");
    private final Intrface intrface;
    private final boolean allRes;

    public FeatValueEditor(FeatureKey featureKey, FeatureValue featureValue, Intrface intrface, boolean allRes) {
        this.setTitle(featureKey.getStringValue());
        this.featureKey = featureKey;
        this.featureValue = featureValue;
        this.intrface = intrface;
        this.allRes = allRes;
        this.setType(Type.POPUP);
        this.setAlwaysOnTop(true);
        init();
        initPosition();
    }

    // Center the GUI window into center of the screen
    private void initPosition() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void apply() {
        if (allRes) {
            intrface.getCommonFeatMap().replace(featureKey, featureValue);
        } else {
            ResolutionPragma resolutionPragma = intrface.getResolutionPragma();
            if (resolutionPragma != null) {
                resolutionPragma.getCustomFeatMap().replace(featureKey, featureValue);
            }
        }
    }

    private void init() {
        // this value is kept for reset
        final String strFVal = featureValue.getStringValue();

        switch (featureValue.getType()) {
            case IMAGE:
                this.setLayout(new GridLayout(3, 2));
                final JLabel lbl = new JLabel(featureKey.getStringValue() + ":");
                this.getContentPane().add(lbl);
                final JTextField txtFld = new JTextField(featureValue.getStringValue());
                this.getContentPane().add(txtFld);
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        featureValue.setStringValue(txtFld.getText());
                        execute();
                        apply();

                        FeatValueEditor.this.dispose();
                    }
                });
                this.getContentPane().add(btnSet);
                btnReset.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtFld.setText(strFVal);
                        featureValue.setStringValue(strFVal);
                    }
                });
                this.getContentPane().add(btnReset);

                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FeatValueEditor.this.dispose();
                    }
                });

                this.getContentPane().add(btnCancel);
                break;
            case ARRAY:
            case SINGLE_VALUE:
                this.setLayout(new GridLayout(3, 2));
                final JLabel lbla = new JLabel(featureKey.getStringValue() + ":");
                this.getContentPane().add(lbla);
                final JSpinner spinval = new JSpinner(new SpinnerNumberModel(Integer.parseInt(featureValue.getStringValue()), -5000, 5000, 1));
                this.getContentPane().add(spinval);
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        featureValue.setStringValue(String.valueOf((int) spinval.getValue()));
                        execute();
                        apply();

                        FeatValueEditor.this.dispose();
                    }
                });
                this.getContentPane().add(btnSet);
                btnReset.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        spinval.setValue(Integer.parseInt(strFVal));
                        featureValue.setStringValue(strFVal);
                    }
                });
                this.getContentPane().add(btnReset);
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FeatValueEditor.this.dispose();
                    }
                });
                this.getContentPane().add(btnCancel);
                break;
            case VECTOR4:
                this.setLayout(new GridLayout(3, 4));
                MyVector4 vec4 = (MyVector4) featureValue;

                JLabel lblTL = new JLabel("Top Left:");
                JSpinner spinX = new JSpinner(new SpinnerNumberModel(Math.round(vec4.x), -5000, 5000, 1));
                JSpinner spinY = new JSpinner(new SpinnerNumberModel(Math.round(vec4.y), -5000, 5000, 1));
                JLabel lblBR = new JLabel("Bottom Right:");
                JSpinner spinZ = new JSpinner(new SpinnerNumberModel(Math.round(vec4.z), -5000, 5000, 1));
                JSpinner spinW = new JSpinner(new SpinnerNumberModel(Math.round(vec4.w), -5000, 5000, 1));
                JComponent[] comps = {lblTL, spinX, spinY, lblBR, spinZ, spinW};
                for (JComponent comp : comps) {
                    this.getContentPane().add(comp);
                }
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MyVector4 vec4 = (MyVector4) featureValue;
                        vec4.x = (int) spinX.getValue();
                        vec4.y = (int) spinY.getValue();
                        vec4.z = (int) spinZ.getValue();
                        vec4.w = (int) spinW.getValue();
                        execute();
                        apply();

                        FeatValueEditor.this.dispose();
                    }
                });
                this.getContentPane().add(btnSet);

                btnReset.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        spinX.setValue(Math.round(vec4.x));
                        spinY.setValue(Math.round(vec4.y));
                        spinZ.setValue(Math.round(vec4.z));
                        spinW.setValue(Math.round(vec4.w));
                        featureValue.setStringValue(strFVal);
                    }
                });
                this.getContentPane().add(btnReset);

                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FeatValueEditor.this.dispose();
                    }
                });

                this.getContentPane().add(btnCancel);
                break;
        }

    }

    /**
     * Execute (command) on set
     */
    public abstract void execute();

    public FeatureKey getFeatureKey() {
        return featureKey;
    }

    public FeatureValue getFeatureValue() {
        return featureValue;
    }

    public JButton getBtnSet() {
        return btnSet;
    }

    public JButton getBtnReset() {
        return btnReset;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

}
