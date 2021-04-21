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

import java.awt.BorderLayout;
import java.awt.Font;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public final class ProgressWindow extends JWindow {

    protected final JLabel jLabel = new JLabel("Building progress");
    protected final JProgressBar progressBar = new JProgressBar(1, 100);

    public ProgressWindow() {
        final URL urlBuild = getClass().getResource(GUI.RESOURCES_DIR + GUI.BUILD_ICON);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 26);
        jLabel.setFont(font);
        jLabel.setIcon(new ImageIcon(urlBuild));
        progressBar.setStringPainted(true);

        this.setLayout(new BorderLayout());
        this.getContentPane().add(jLabel, BorderLayout.CENTER);
        this.getContentPane().add(progressBar, BorderLayout.SOUTH);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
        this.setLocation(GUI.DIM.width / 2 - this.getSize().width / 2, GUI.DIM.height / 2 - this.getSize().height / 2);
        this.pack();
    }

    public JLabel getjLabel() {
        return jLabel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

}
