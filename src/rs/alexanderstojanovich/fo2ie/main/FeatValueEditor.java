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

import rs.alexanderstojanovich.fo2ie.action.Action;
import rs.alexanderstojanovich.fo2ie.action.FeatureAction;
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
import rs.alexanderstojanovich.fo2ie.feature.MyRectangle;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.Resolution;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class FeatValueEditor extends JFrame {

    private static FeatValueEditor instance;

    public static FeatValueEditor getInstance(GUI gui) {
        if (instance == null) {
            instance = new FeatValueEditor() {
                @Override
                public void execute() {
                    gui.buildModuleComponents();
                    gui.updateBaseFeaturePreview();
                    gui.updateComponentsPreview();
                    gui.updateDerivedFeaturePreview();
                }
            };
        }

        return instance;
    }

    private FeatValueEditor() {
        this.setType(Type.POPUP);
        this.setAlwaysOnTop(true);
        this.setIconImages(GUI.ICONS);
        this.setPreferredSize(new Dimension(300, 225));
        initPosition();
    }

    // Center the GUI window into center of the screen
    private void initPosition() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void apply(FeatureKey featureKey, FeatureValue featureValue, Intrface intrface) {
        FeatureValue oldFeatureValue = null;
        oldFeatureValue = intrface.getWorkingBinds().getCommonFeatMap().get(featureKey);
        intrface.getWorkingBinds().commonFeatMap.replace(featureKey, featureValue);
        Action action = new FeatureAction.EditFeature(intrface, oldFeatureValue, GLComponent.Inheritance.BASE, featureKey);
        GUI.ACTIONS.add(action);
    }

    private void apply(FeatureKey featureKey, FeatureValue featureValue, Intrface intrface, Resolution resolution) {
        FeatureValue oldFeatureValue = null;
        ResolutionPragma resolutionPragma = intrface.getWorkingBinds().customResolutions.stream().filter(x -> x.getResolution().equals(resolution)).findFirst().orElse(null);
        if (resolutionPragma != null) {
            oldFeatureValue = resolutionPragma.getCustomFeatMap().get(featureKey);
            resolutionPragma.getCustomFeatMap().replace(featureKey, featureValue);
        }
        Action action = new FeatureAction.EditFeature(intrface, oldFeatureValue, GLComponent.Inheritance.DERIVED, featureKey);
        GUI.ACTIONS.add(action);
    }

    public void popUp(FeatureKey featureKey, FeatureValue featureValue, Intrface intrface) {
        // remove all components
        this.getContentPane().removeAll();
        this.setTitle(featureKey.getStringValue());
        // this value is kept for reset
        final String strFVal = featureValue.getStringValue();

        final JButton btnSet = new JButton("Set");
        final JButton btnReset = new JButton("Reset");
        final JButton btnCancel = new JButton("Cancel");

        switch (featureValue.getType()) {
            case IMAGE:
                this.setLayout(new GridLayout(3, 2));
                final JLabel lbl = new JLabel(featureKey.getStringValue() + ":");
                this.getContentPane().add(lbl);
                final JTextField txtFld = new JTextField(featureValue.getStringValue(), 16);
                this.getContentPane().add(txtFld);
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        featureValue.setStringValue(txtFld.getText());
                        execute();
                        apply(featureKey, featureValue, intrface);

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
                final JTextField txtVal = new JTextField(featureValue.getStringValue());
                this.getContentPane().add(txtVal);
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        featureValue.setStringValue(txtVal.getText());
                        execute();
                        apply(featureKey, featureValue, intrface);

                        FeatValueEditor.this.dispose();
                    }
                });
                this.getContentPane().add(btnSet);
                btnReset.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtVal.setText(strFVal);
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
            case RECT4:
                this.setLayout(new GridLayout(3, 4));
                MyRectangle myRect = (MyRectangle) featureValue;

                JLabel lblTL = new JLabel("Top Left:");
                JSpinner spinX = new JSpinner(new SpinnerNumberModel(Math.round(myRect.minX), -5000, 5000, 1));
                JSpinner spinY = new JSpinner(new SpinnerNumberModel(Math.round(myRect.minY), -5000, 5000, 1));
                JLabel lblBR = new JLabel("Bottom Right:");
                JSpinner spinZ = new JSpinner(new SpinnerNumberModel(Math.round(myRect.maxX), -5000, 5000, 1));
                JSpinner spinW = new JSpinner(new SpinnerNumberModel(Math.round(myRect.maxY), -5000, 5000, 1));
                JComponent[] comps = {lblTL, spinX, spinY, lblBR, spinZ, spinW};
                for (JComponent comp : comps) {
                    this.getContentPane().add(comp);
                }
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MyRectangle myRect4 = (MyRectangle) featureValue;
                        myRect4.minX = (int) spinX.getValue();
                        myRect4.minY = (int) spinY.getValue();
                        myRect4.maxX = (int) spinZ.getValue();
                        myRect4.maxY = (int) spinW.getValue();
                        execute();
                        apply(featureKey, featureValue, intrface);

                        FeatValueEditor.this.dispose();
                    }
                });
                this.getContentPane().add(btnSet);

                btnReset.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        spinX.setValue(Math.round(myRect.minX));
                        spinY.setValue(Math.round(myRect.minY));
                        spinZ.setValue(Math.round(myRect.maxX));
                        spinW.setValue(Math.round(myRect.maxY));
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

    public void popUp(FeatureKey featureKey, FeatureValue featureValue, Intrface intrface, Resolution resolution) {
        // remove all components
        this.getContentPane().removeAll();
        this.setTitle(featureKey.getStringValue());
        // this value is kept for reset
        final String strFVal = featureValue.getStringValue();

        final JButton btnSet = new JButton("Set");
        final JButton btnReset = new JButton("Reset");
        final JButton btnCancel = new JButton("Cancel");

        switch (featureValue.getType()) {
            case IMAGE:
                this.setLayout(new GridLayout(3, 2));
                final JLabel lbl = new JLabel(featureKey.getStringValue() + ":");
                this.getContentPane().add(lbl);
                final JTextField txtFld = new JTextField(featureValue.getStringValue(), 16);
                this.getContentPane().add(txtFld);
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        featureValue.setStringValue(txtFld.getText());
                        execute();
                        apply(featureKey, featureValue, intrface, resolution);

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
                final JTextField txtVal = new JTextField(featureValue.getStringValue());
                this.getContentPane().add(txtVal);
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        featureValue.setStringValue(txtVal.getText());
                        execute();
                        apply(featureKey, featureValue, intrface, resolution);

                        FeatValueEditor.this.dispose();
                    }
                });
                this.getContentPane().add(btnSet);
                btnReset.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtVal.setText(strFVal);
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
            case RECT4:
                this.setLayout(new GridLayout(3, 4));
                MyRectangle myRect = (MyRectangle) featureValue;

                JLabel lblTL = new JLabel("Top Left:");
                JSpinner spinX = new JSpinner(new SpinnerNumberModel(Math.round(myRect.minX), -5000, 5000, 1));
                JSpinner spinY = new JSpinner(new SpinnerNumberModel(Math.round(myRect.minY), -5000, 5000, 1));
                JLabel lblBR = new JLabel("Bottom Right:");
                JSpinner spinZ = new JSpinner(new SpinnerNumberModel(Math.round(myRect.maxX), -5000, 5000, 1));
                JSpinner spinW = new JSpinner(new SpinnerNumberModel(Math.round(myRect.maxY), -5000, 5000, 1));
                JComponent[] comps = {lblTL, spinX, spinY, lblBR, spinZ, spinW};
                for (JComponent comp : comps) {
                    this.getContentPane().add(comp);
                }
                btnSet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MyRectangle myRect4 = (MyRectangle) featureValue;
                        myRect4.minX = (int) spinX.getValue();
                        myRect4.minY = (int) spinY.getValue();
                        myRect4.maxX = (int) spinZ.getValue();
                        myRect4.maxY = (int) spinW.getValue();
                        execute();
                        apply(featureKey, featureValue, intrface, resolution);

                        FeatValueEditor.this.dispose();
                    }
                });
                this.getContentPane().add(btnSet);

                btnReset.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        spinX.setValue(Math.round(myRect.minX));
                        spinY.setValue(Math.round(myRect.minY));
                        spinZ.setValue(Math.round(myRect.maxX));
                        spinW.setValue(Math.round(myRect.maxY));
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

}
