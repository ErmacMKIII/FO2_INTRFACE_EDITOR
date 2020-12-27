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

    private static Configuration instance;

    private Configuration() {

    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
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
            pw.println();
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

}
