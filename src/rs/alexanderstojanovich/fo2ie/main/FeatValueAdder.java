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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.ImageWrapper;
import rs.alexanderstojanovich.fo2ie.feature.MyArray;
import rs.alexanderstojanovich.fo2ie.feature.MyRectangle;
import rs.alexanderstojanovich.fo2ie.feature.SingleValue;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.Resolution;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.intrface.Section;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class FeatValueAdder extends JFrame {

    private final JLabel lblInheritance = new JLabel("Inheritance:");
    private JComboBox<GLComponent.Inheritance> cmbInheritance;

    private final JLabel lblFeatName = new JLabel("Feature key:");
    private JComboBox<FeatureKey> cmbFtKeys;

    private final JLabel lblFtValType = new JLabel("Feature value type:");
    private final JTextField txtFldFtValType = new JTextField();

    private final JButton btnOK = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");

    private static FeatValueAdder instance;

    public static FeatValueAdder getInstance(GUI gui) {
        if (instance == null) {
            instance = new FeatValueAdder() {
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

    public FeatValueAdder() {
        this.setTitle("Add feature");
        this.setType(Window.Type.POPUP);
        this.setAlwaysOnTop(true);
        this.setIconImages(GUI.ICONS);
        this.setPreferredSize(new Dimension(300, 225));
        initPosition();
    }

    /**
     * Execute (command) on OK
     */
    public abstract void execute();

    // Center the GUI window into center of the screen
    private void initPosition() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    // add feature (internal)
    // depending on the selected type of val corresponding feature (featKey, featValue) will be added
    private boolean addFeature(Intrface intrface) {
        this.setTitle("Add feature");

        boolean ok = false;

        GLComponent.Inheritance inheritance = (GLComponent.Inheritance) cmbInheritance.getSelectedItem();

        FeatureKey featKey = (FeatureKey) cmbFtKeys.getSelectedItem();

        if (featKey == null) {
            JOptionPane.showMessageDialog(this, "Feature name is erroneous!", "Feature Key Error", JOptionPane.ERROR_MESSAGE);
        } else {
            FeatureValue featVal = null;
            FeatureValue.Type featValType = FeatureValue.Type.valueOf(txtFldFtValType.getText());
            switch (featValType) {
                case IMAGE:
                    featVal = new ImageWrapper("");
                    break;
                case SINGLE_VALUE:
                    featVal = new SingleValue();
                    break;
                case ARRAY:
                    featVal = new MyArray();
                    break;
                case RECT4:
                    featVal = new MyRectangle();
                    break;
            }

            if (featVal == null) {
                JOptionPane.showMessageDialog(this, "Feature value type does not match its key!", "Feature Value Error", JOptionPane.ERROR_MESSAGE);
            } else {
                intrface.getWorkingBinds().commonFeatMap.put(featKey, featVal);
                ok = true;
            }
        }

        if (ok) {
            Action action = new FeatureAction.AddFeature(intrface, inheritance, featKey);
            GUI.ACTIONS.add(action);
        }

        return ok;
    }

    // add feature (internal)
    // depending on the selected type of val corresponding feature (featKey, featValue) will be added
    private boolean addFeature(Intrface intrface, Resolution resolution) {
        this.setTitle("Add feature");

        boolean ok = false;

        GLComponent.Inheritance inheritance = (GLComponent.Inheritance) cmbInheritance.getSelectedItem();

        FeatureKey featKey = (FeatureKey) cmbFtKeys.getSelectedItem();

        if (featKey == null) {
            JOptionPane.showMessageDialog(this, "Feature name is erroneous!", "Feature Key Error", JOptionPane.ERROR_MESSAGE);
        } else {
            FeatureValue featVal = null;
            FeatureValue.Type featValType = FeatureValue.Type.valueOf(txtFldFtValType.getText());
            switch (featValType) {
                case IMAGE:
                    featVal = new ImageWrapper("");
                    break;
                case SINGLE_VALUE:
                    featVal = new SingleValue();
                    break;
                case ARRAY:
                    featVal = new MyArray();
                    break;
                case RECT4:
                    featVal = new MyRectangle();
                    break;
            }

            if (featVal == null) {
                JOptionPane.showMessageDialog(this, "Feature value type does not match its key!", "Feature Value Error", JOptionPane.ERROR_MESSAGE);
            } else {
                ResolutionPragma resolutionPragma = intrface.getWorkingBinds().customResolutions.stream().filter(x -> x.getResolution().equals(resolution)).findFirst().orElse(null);
                if (resolutionPragma != null) {
                    resolutionPragma.getCustomFeatMap().put(featKey, featVal);
                    ok = true;
                }
            }
        }

        if (ok) {
            Action action = new FeatureAction.AddFeature(intrface, inheritance, featKey);
            GUI.ACTIONS.add(action);
        }

        return ok;
    }

    private void setInitCmbInheritance(Section section, Intrface intrface) {
        FeatureKey[] unmappedKeys = intrface.getUnmappedKeys(section);
        DefaultComboBoxModel<FeatureKey> model = new DefaultComboBoxModel<>();
        for (FeatureKey fk : unmappedKeys) {
            model.addElement(fk);
        }
        cmbFtKeys.setModel(model);
    }

    private void setInitCmbInheritance(Section section, Intrface intrface, Resolution resolution) {
        FeatureKey[] unmappedKeys = intrface.getUnmappedKeys(section, resolution);
        DefaultComboBoxModel<FeatureKey> model = new DefaultComboBoxModel<>();
        for (FeatureKey fk : unmappedKeys) {
            model.addElement(fk);
        }
        cmbFtKeys.setModel(model);
    }

    private void setInitTxtFld() {
        FeatureKey featKey = (FeatureKey) cmbFtKeys.getSelectedItem();
        FeatureValue.Type featValType = FeatureValue.Type.UNKNOWN;
        if (featKey != null) { // fix for null ptr exception!
            switch (featKey.getType()) {
                case PIC:
                    featValType = FeatureValue.Type.IMAGE;
                    break;
                case OFFSET:
                case VALUE:
                    featValType = FeatureValue.Type.SINGLE_VALUE;
                    break;
                case ARRAY:
                    featValType = FeatureValue.Type.ARRAY;
                    break;
                case PIC_POS:
                case TXT:
                    featValType = FeatureValue.Type.RECT4;
                    break;
            }
        }
        txtFldFtValType.setText(featValType.name());
    }

    public void popUp(Section section, Intrface intrface) {
        this.setTitle("Add feature");
        this.getContentPane().removeAll(); // removes all the components

        cmbInheritance = new JComboBox<>(GLComponent.Inheritance.values());
        FeatureKey[] unmappedKeys = intrface.getUnmappedKeys(section);
        cmbFtKeys = new JComboBox<>(unmappedKeys);

        this.setLayout(new GridLayout(4, 3));
        this.getContentPane().add(lblInheritance);
        this.getContentPane().add(cmbInheritance);

        this.getContentPane().add(lblFeatName);
        this.getContentPane().add(cmbFtKeys);

        this.getContentPane().add(lblFtValType);
        this.getContentPane().add(txtFldFtValType);

        this.txtFldFtValType.setEditable(false);

        this.cmbInheritance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setInitCmbInheritance(section, intrface);
                setInitTxtFld();
            }
        });
        setInitCmbInheritance(section, intrface);

        this.cmbFtKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setInitTxtFld();
            }
        });
        setInitTxtFld();

        this.btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ok = addFeature(intrface);
                if (ok) {
                    execute();
                    dispose();
                }
            }
        });

        this.btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        this.getContentPane().add(btnOK);
        this.getContentPane().add(btnCancel);
    }

    public void popUp(Section section, Intrface intrface, Resolution resolution) {
        this.setTitle("Add feature");
        this.getContentPane().removeAll(); // removes all the components

        cmbInheritance = new JComboBox<>(GLComponent.Inheritance.values());
        FeatureKey[] unmappedKeys = intrface.getUnmappedKeys(section, resolution);
        cmbFtKeys = new JComboBox<>(unmappedKeys);

        this.setLayout(new GridLayout(4, 3));
        this.getContentPane().add(lblInheritance);
        this.getContentPane().add(cmbInheritance);

        this.getContentPane().add(lblFeatName);
        this.getContentPane().add(cmbFtKeys);

        this.getContentPane().add(lblFtValType);
        this.getContentPane().add(txtFldFtValType);

        this.txtFldFtValType.setEditable(false);

        this.cmbInheritance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setInitCmbInheritance(section, intrface, resolution);
                setInitTxtFld();
            }
        });
        setInitCmbInheritance(section, intrface, resolution);

        this.cmbFtKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setInitTxtFld();
            }
        });
        setInitTxtFld();

        this.btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ok = addFeature(intrface);
                if (ok) {
                    execute();
                    dispose();
                }
            }
        });

        this.btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        this.getContentPane().add(btnOK);
        this.getContentPane().add(btnCancel);
    }

    public JLabel getLblFeatName() {
        return lblFeatName;
    }

    public JComboBox<FeatureKey> getCmbFtKeys() {
        return cmbFtKeys;
    }

    public JLabel getLblFtValType() {
        return lblFtValType;
    }

    public JTextField getTxtFldFtValType() {
        return txtFldFtValType;
    }

    public JButton getBtnOK() {
        return btnOK;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

}
