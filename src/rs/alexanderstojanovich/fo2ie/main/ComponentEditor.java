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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.MyRectangle;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.PrimitiveQuad;
import rs.alexanderstojanovich.fo2ie.ogl.Text;
import rs.alexanderstojanovich.fo2ie.util.Pair;
import rs.alexanderstojanovich.fo2ie.util.ScalingUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class ComponentEditor extends JFrame {

    private static ComponentEditor instance;

    public static ComponentEditor getInstance(final GUI gui) {
        if (instance == null) {
            instance = new ComponentEditor() {
                @Override
                public void execute() {
                    gui.buildModuleComponents();
                    gui.updateBaseFeaturePreview();
                    gui.updateDerivedFeaturePreview();
                    gui.updateComponentsPreview();
                }
            };
        }

        return instance;
    }

    private ComponentEditor() {
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

    private void apply(FeatureKey featureKey, FeatureValue featureValue, Intrface intrface, boolean allRes) {
        if (allRes) {
            intrface.getCommonFeatMap().replace(featureKey, featureValue);
        } else {
            ResolutionPragma resolutionPragma = intrface.getResolutionPragma();
            if (resolutionPragma != null) {
                resolutionPragma.getCustomFeatMap().replace(featureKey, featureValue);
            }
        }
    }

    public void popUp(FeatureKey featureKey, FeatureValue featureValue, Intrface intrface, boolean allRes, GLComponent glComponent) {
        this.setTitle(featureKey.getStringValue());
        this.getContentPane().removeAll(); // removes all the components

        final JLabel lblPosition = new JLabel("Position:");

        final JButton btnSet = new JButton("Set");
        final JButton btnReset = new JButton("Reset");
        final JButton btnCancel = new JButton("Cancel");

        // this value is kept for         
        final int posX = Math.round(glComponent.getPos().x - glComponent.getWidth() / 2.0f);
        final int posY = Math.round(glComponent.getPos().y - glComponent.getHeight() / 2.0f);

        final JSpinner spinPosX = new JSpinner(new SpinnerNumberModel(posX, -5000, 5000, 1));
        final JSpinner spinPosY = new JSpinner(new SpinnerNumberModel(posY, -5000, 5000, 1));

        final String strFVal = featureValue.getStringValue();

        this.setLayout(new GridLayout(2, 3));

        this.getContentPane().add(lblPosition);
        this.getContentPane().add(spinPosX);
        this.getContentPane().add(spinPosY);

        btnSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (featureValue instanceof MyRectangle) {
                    MyRectangle myChngdRect = (MyRectangle) featureValue;

                    glComponent.getPos().x += (int) spinPosX.getValue() - posX;
                    glComponent.getPos().y += (int) spinPosY.getValue() - posY;

                    if (glComponent instanceof Text) {
                        Text textKey = (Text) glComponent;
                        PrimitiveQuad primitive = textKey.getOverlay();

                        primitive.getPos().x += (int) spinPosX.getValue() - posX;
                        primitive.getPos().y += (int) spinPosY.getValue() - posY;
                    }

                    Pair<Float, Float> skvp = ScalingUtils.scaleXYFactor(intrface.getModeWidth(), intrface.getModeHeight(), intrface.getMainPicWidth(), intrface.getMainPicHeight());

                    myChngdRect.minX += Math.round(((int) spinPosX.getValue() - posX) / skvp.getKey());
                    myChngdRect.minY += Math.round(((int) spinPosY.getValue() - posY) / skvp.getValue());
                    myChngdRect.maxX += Math.round(((int) spinPosX.getValue() - posX) / skvp.getKey());
                    myChngdRect.maxY += Math.round(((int) spinPosY.getValue() - posY) / skvp.getValue());

                    featureValue.setStringValue(myChngdRect.getStringValue());
                }

                execute();
                apply(featureKey, featureValue, intrface, allRes);

                ComponentEditor.this.dispose();
            }
        });
        this.getContentPane().add(btnSet);
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spinPosX.setValue(posX);
                spinPosY.setValue(posY);
                featureValue.setStringValue(strFVal);
            }
        });
        this.getContentPane().add(btnReset);

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ComponentEditor.this.dispose();
            }
        });
        this.getContentPane().add(btnCancel);
    }

    /**
     * Execute (command) on set
     */
    public abstract void execute();

}
