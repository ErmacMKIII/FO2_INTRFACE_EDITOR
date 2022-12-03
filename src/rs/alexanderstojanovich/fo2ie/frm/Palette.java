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
package rs.alexanderstojanovich.fo2ie.frm;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import rs.alexanderstojanovich.fo2ie.main.GUI;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Palette {

    // All colors in the palette, required for an indexed model
    private static int colors[];
    // Color buffer aka color map
    private static byte colBuff[];
    // whether or not palette is loaded or not
    private static boolean loaded = false;

    //--------------------------------------------------------------------------
    // A - STATIC METHODS
    //--------------------------------------------------------------------------     
    /**
     * Load palette with given file name, index 0 - transparent
     *
     * @param fileName palette filename
     */
    public static void load(String fileName) {
        loaded = false;
        InputStream in = Palette.class.getResourceAsStream(GUI.RESOURCES_DIR + fileName);
        byte[] buff = null;
        if (in != null) {
            buff = new byte[768];
            try {
                in.read(buff);
                in.close();
            } catch (IOException ex) {
                FO2IELogger.reportError(ex.getMessage(), ex);
            }
        }

        if (buff != null) {
            int index = 0;
            colors = new int[256];
            colBuff = new byte[1024];
            for (int i = 0; i < buff.length / 3; i++) {
                Color col = new Color(buff[i * 3] & 0xFF, buff[i * 3 + 1] & 0xFF, buff[i * 3 + 2] & 0xFF, (i == 0) ? 0 : 0xFF);
                colors[index] = col.getRGB();
                colBuff[4 * index] = (byte) col.getRed();
                colBuff[4 * index + 1] = (byte) col.getGreen();
                colBuff[4 * index + 2] = (byte) col.getBlue();
                colBuff[4 * index + 3] = (byte) col.getAlpha();
                index++;
            }
            loaded = true;
        }
    }

    /**
     * asynch reset - returns palette into initial state
     */
    public static void reset() {
        loaded = false;
        colors = null;
        colBuff = null;
    }

    //--------------------------------------------------------------------------
    // B - STATIC GETTERS 
    //-------------------------------------------------------------------------- 
    /**
     * Gets Colors of the palette
     *
     * @return palette itself
     */
    public static int[] getColors() {
        return colors;
    }

    public static byte[] getColBuff() {
        return colBuff;
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
