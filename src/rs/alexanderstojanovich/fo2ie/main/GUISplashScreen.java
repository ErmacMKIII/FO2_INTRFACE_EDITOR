/*
 * Copyright (C) 2019 Coa
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Coa
 */
public class GUISplashScreen extends JWindow implements Runnable {

    // label which contains splash image
    private final JLabel splashImgLbl = new JLabel();
    // progress bar (the progress is dependant on the GUI initialization progress)
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    public static final Color COLOR = new Color(38, 23, 38);

    //--------------------------------------------------------------------------
    // A - CONSTRUCTORS 
    //--------------------------------------------------------------------------
    public GUISplashScreen() {
        URL splashImgURL = this.getClass().getResource(GUI.RESOURCES_DIR + GUI.SPLASH_FILE_NAME);

        if (splashImgURL == null) {
            throw new RuntimeException("Splash image not found!");
        }

        ImageIcon splashImgIcon = new ImageIcon(splashImgURL);
        this.splashImgLbl.setSize(splashImgIcon.getIconWidth(), splashImgIcon.getIconHeight());
        this.splashImgLbl.setIcon(splashImgIcon);
        this.progressBar.setForeground(COLOR);
    }

    //--------------------------------------------------------------------------
    // B - METHODS
    //--------------------------------------------------------------------------
    // Center the JFrame which is splash screen into center of the screen
    private void setUpPosition() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getWidth() / 2, dim.height / 2 - this.getHeight() / 2);
    }

    // call this method after constructor to add components and finish the effects
    public void setUp() {
        this.setLayout(new BorderLayout());
        this.setSize(splashImgLbl.getWidth(), splashImgLbl.getHeight());
        this.setUpPosition();
        this.getContentPane().add(splashImgLbl, BorderLayout.CENTER);
        this.getContentPane().add(progressBar, BorderLayout.SOUTH);
        this.toFront();
        this.setVisible(true);
    }

    // this what this Runnable things to, updates it's progress bar depending on
    // the GUI initialization tasks
    @Override
    public void run() {
        while (GUI.getProgress() < 100.0f) {
            this.progressBar.setValue(Math.round(GUI.getProgress()));
            this.progressBar.validate();
        }
        this.toFront();
        this.progressBar.setValue(Math.round(GUI.getProgress()));
        this.progressBar.validate();

        FO2IELogger.reportInfo("App Initialized!", null);
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        }
        this.dispose();
    }

    //--------------------------------------------------------------------------
    // C - GETTERS
    //--------------------------------------------------------------------------
    public JLabel getSplashImgLbl() {
        return splashImgLbl;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
