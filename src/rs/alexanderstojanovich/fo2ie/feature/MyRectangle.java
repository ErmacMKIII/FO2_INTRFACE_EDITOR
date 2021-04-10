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
package rs.alexanderstojanovich.fo2ie.feature;

import org.joml.Rectanglei;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class MyRectangle extends Rectanglei implements FeatureValue {

    /**
     * Constructs new MyRectangle (initialized with zeroes)
     */
    public MyRectangle() {
        super(0, 0, 0, 0);
    }

    /**
     * Constructs new MyRectangle with given values
     *
     * @param minX top left X
     * @param minY top left Y
     * @param maxX bottom right X
     * @param maxY bottom right Y
     */
    public MyRectangle(int minX, int minY, int maxX, int maxY) {
        super(minX, minY, maxX, maxY);
    }

    /**
     * Scale my rectangle so it can be rendered on the screen
     *
     * @param mainPicWidth main picture width
     * @param mainPicHeight main picture height
     * @param screenWidth rendered screen width
     * @param screenHeight rendered screen height
     * @return this rectangle (modified)
     */
    public MyRectangle scalex(int mainPicWidth, int mainPicHeight, int screenWidth, int screenHeight) {
        minX = Math.round(minX * screenWidth / (float) mainPicWidth);
        minY = Math.round(minY * screenHeight / (float) mainPicHeight);
        maxX = Math.round(maxX * screenWidth / (float) mainPicWidth);
        maxY = Math.round(maxY * screenHeight / (float) mainPicHeight);

        return this;
    }

    /**
     * Scale my vector so it can be rendered on the screen
     *
     * @param mainPicWidth main picture width
     * @param mainPicHeight main picture height
     * @param screenWidth rendered screen width
     * @param screenHeight rendered screen height
     * @param temp temporary vector
     * @return temp vector
     */
    public MyRectangle scalex(int mainPicWidth, int mainPicHeight, int screenWidth, int screenHeight, MyRectangle temp) {
        temp.minX = Math.round(minX * screenWidth / (float) mainPicWidth);
        temp.minY = Math.round(minY * screenHeight / (float) mainPicHeight);
        temp.maxX = Math.round(maxX * screenWidth / (float) mainPicWidth);
        temp.maxY = Math.round(maxY * screenHeight / (float) mainPicHeight);

        return temp;
    }

    @Override
    public String getStringValue() {
        return String.valueOf(minX)
                + " " + String.valueOf(minY)
                + " " + String.valueOf(maxX)
                + " " + String.valueOf(maxY);
    }

    @Override
    public Type getType() {
        return Type.RECT4;
    }

    @Override
    public void setStringValue(String value) {
        String[] split = value.split("\\s+");
        if (split.length == 4) {
            minX = Integer.valueOf(split[0]);
            minY = Integer.valueOf(split[1]);
            maxX = Integer.valueOf(split[2]);
            maxY = Integer.valueOf(split[3]);
        }
    }

    @Override
    public String toString() {
        return getStringValue();
    }

}
