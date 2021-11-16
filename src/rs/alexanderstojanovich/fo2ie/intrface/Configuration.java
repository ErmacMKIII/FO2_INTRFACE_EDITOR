/* 
 * Copyright (C) 2020 Alexander Stojanovich <coas91@rocketmail.com>
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
package rs.alexanderstojanovich.fo2ie.intrface;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Configuration {

    private static final String CONFIG_PATH = "fo2_intrfaceditor.ini";

    private static final String INPUT_DIR_PATH = "";
    private static final String OUTPUT_DIR_PATH = "";

    private File inDir = new File(INPUT_DIR_PATH);
    private File outDir = new File(OUTPUT_DIR_PATH);
    private Color txtCol = Color.GREEN;
    private Color txtOverlayCol = new Color(128, 128, 128, 128); // gray half translucent
    private Color selectCol = new Color(0, 0, 255, 128); // half blue translucent
    private Color qmarkCol = Color.MAGENTA;
    private int textureSize = 1024;

    private boolean keepAspectRatio = false;
    private String defaultIni = "default.ini";

    private boolean ignoreErrors = false;

    private static Configuration instance;

    private int animationTicks = 15;

    private Configuration() {

    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private Color readRGBA(String str) {
        String[] split = str.trim().split("^\\(|,|\\)$");
        int red = Integer.parseInt(split[1]);
        int green = Integer.parseInt(split[2]);
        int blue = Integer.parseInt(split[3]);
        int alpha = Integer.parseInt(split[4]);
        Color color = new Color(red, green, blue, alpha);
        return color;
    }

    private String writeRGBA(Color color) {
        return "(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", " + color.getAlpha() + ")";
    }

    /**
     * Reads configuration from the .ini file
     */
    public void readConfigFile() {
        File cfg = new File(CONFIG_PATH);
        if (cfg.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(cfg));
                String line;
                while ((line = br.readLine()) != null) {
                    // replace all white space chars with empty string
                    String[] words = line.replaceAll("\\s", "").split("=");
                    if (words.length == 2) {
                        switch (words[0]) {
                            case "InputDirPath":
                                inDir = new File(words[1].replaceAll("\"", ""));
                                break;
                            case "OutputDirPath":
                                outDir = new File(words[1].replaceAll("\"", ""));
                                break;
                            case "DefaultIni":
                                defaultIni = words[1].replaceAll("\"", "");
                                break;
                            case "TextColor":
                                txtCol = readRGBA(words[1]);
                                break;
                            case "TextOverlayColor":
                                txtOverlayCol = readRGBA(words[1]);
                                break;
                            case "QMarkColor":
                                qmarkCol = readRGBA(words[1]);
                                break;
                            case "SelectedColor":
                                selectCol = readRGBA(words[1]);
                                break;
                            case "TextureSize":
                                int number = Integer.parseInt(words[1]);
                                // if tex size is a non-zero power of two
                                if (number != 0 && (number & (number - 1)) == 0) {
                                    textureSize = number;
                                }
                                break;
                            case "AnimationTicks":
                                int numx = Integer.parseInt(words[1]);
                                if (numx >= 0) {
                                    animationTicks = numx;
                                }
                                break;
                            case "KeepAspectRatio":
                                keepAspectRatio = Boolean.parseBoolean(words[1]);
                                break;
                            case "IgnoreErrors":
                                ignoreErrors = Boolean.parseBoolean(words[1]);
                                break;
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                FO2IELogger.reportError(ex.getMessage(), ex);
            } catch (IOException ex) {
                FO2IELogger.reportError(ex.getMessage(), ex);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ex) {
                        FO2IELogger.reportError(ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    /**
     * Writes configuration to the .ini file (on app exit)
     */
    public void writeConfigFile() {
        File cfg = new File(CONFIG_PATH);
        if (cfg.exists()) {
            cfg.delete();
        }
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(cfg);
            pw.println("InputDirPath = " + "\"" + inDir.getPath() + "\"");
            pw.println("OutputDirPath = " + "\"" + outDir.getPath() + "\"");
            pw.println("DefaultIni = " + "\"" + defaultIni + "\"");
            pw.println();
            pw.println("TextColor = " + writeRGBA(txtCol));
            pw.println("TextOverlayColor = " + writeRGBA(txtOverlayCol));
            pw.println("SelectedColor = " + writeRGBA(selectCol));
            pw.println("QMarkColor = " + writeRGBA(qmarkCol));
            pw.println("TextureSize = " + textureSize);
            pw.println("AnimationTicks = " + animationTicks);
            pw.println("KeepAspectRatio = " + keepAspectRatio);
            pw.println("IgnoreErrors = " + ignoreErrors);
        } catch (FileNotFoundException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    public void reset() {
        // set defaults        
        inDir = new File(INPUT_DIR_PATH);
        outDir = new File(OUTPUT_DIR_PATH);

        // read config file if it exists
        readConfigFile();
    }

    public File getInDir() {
        return inDir;
    }

    public void setInDir(File inDir) {
        this.inDir = inDir;
    }

    public File getOutDir() {
        return outDir;
    }

    public void setOutDir(File outDir) {
        this.outDir = outDir;
    }

    public Color getTxtCol() {
        return txtCol;
    }

    public void setTxtCol(Color txtCol) {
        this.txtCol = txtCol;
    }

    public Color getQmarkCol() {
        return qmarkCol;
    }

    public void setQmarkCol(Color qmarkCol) {
        this.qmarkCol = qmarkCol;
    }

    public Color getTxtOverlayCol() {
        return txtOverlayCol;
    }

    public void setTxtOverlayCol(Color txtOverlayCol) {
        this.txtOverlayCol = txtOverlayCol;
    }

    public int getTextureSize() {
        return textureSize;
    }

    public boolean isKeepAspectRatio() {
        return keepAspectRatio;
    }

    public void setKeepAspectRatio(boolean keepAspectRatio) {
        this.keepAspectRatio = keepAspectRatio;
    }

    public String getDefaultIni() {
        return defaultIni;
    }

    public void setDefaultIni(String defaultIni) {
        this.defaultIni = defaultIni;
    }

    public boolean isIgnoreErrors() {
        return ignoreErrors;
    }

    public void setTextureSize(int textureSize) {
        this.textureSize = textureSize;
    }

    public void setIgnoreErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }

    public int getAnimationTicks() {
        return animationTicks;
    }

    public Color getSelectCol() {
        return selectCol;
    }

}
