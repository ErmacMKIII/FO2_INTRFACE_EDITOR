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
package rs.alexanderstojanovich.fo2ie.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import rs.alexanderstojanovich.fo2ie.main.GUI;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class PlainTextReader {

    public static String readFromFile(String fileName) {
        StringBuilder text = new StringBuilder();
        InputStream in = PlainTextReader.class.getResourceAsStream(GUI.RESOURCES_DIR + fileName);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
            br.close();
        } catch (FileNotFoundException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        }
        return text.toString();
    }

}
