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
import rs.alexanderstojanovich.fo2ie.util.Pair;
import rs.alexanderstojanovich.fo2ie.util.ScalingUtils;

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
     * @param sx X-axis scaling
     * @param sy Y-axis scaling
     * @return this rectangle (modified)
     */
    public MyRectangle scaleXY(float sx, float sy) {
        minX = Math.round(minX * sx);
        minY = Math.round(minY * sy);
        maxX = Math.round(maxX * sx);
        maxY = Math.round(maxY * sy);

        return this;
    }

    /**
     * Scale my rectangle so it can be rendered on the screen
     *
     * @param sx X-axis scaling
     * @param sy Y-axis scaling
     * @param temp temporary rectangle
     * @return temp rectangle
     */
    public MyRectangle scaleXY(float sx, float sy, MyRectangle temp) {
        temp.minX = Math.round(minX * sx);
        temp.minY = Math.round(minY * sy);
        temp.maxX = Math.round(maxX * sx);
        temp.maxY = Math.round(maxY * sy);

        return temp;
    }

    /**
     * Scale my rectangle so it can be rendered on the screen
     *
     * @param mainPicWidth main picture width
     * @param mainPicHeight main picture height
     * @param modeWidth rendered screen width
     * @param modeHeight rendered screen height
     * @return this rectangle (modified)
     */
    public MyRectangle scaleXY(int mainPicWidth, int mainPicHeight, int modeWidth, int modeHeight) {

        Pair<Float, Float> skvp = ScalingUtils.scaleXYFactor(modeWidth, modeHeight, mainPicWidth, mainPicHeight);

        minX = Math.round(minX * skvp.getKey());
        minY = Math.round(minY * skvp.getValue());
        maxX = Math.round(maxX * skvp.getKey());
        maxY = Math.round(maxY * skvp.getValue());

        return this;
    }

    /**
     * Scale my vector so it can be rendered on the screen
     *
     * @param mainPicWidth main picture width
     * @param mainPicHeight main picture height
     * @param modeWidth rendered screen width
     * @param modeHeight rendered screen height
     * @param temp temporary rectangle
     * @return temp rectangle
     */
    public MyRectangle scaleXY(int mainPicWidth, int mainPicHeight, int modeWidth, int modeHeight, MyRectangle temp) {

        Pair<Float, Float> skvp = ScalingUtils.scaleXYFactor(modeWidth, modeHeight, mainPicWidth, mainPicHeight);

        temp.minX = Math.round(minX * skvp.getKey());
        temp.minY = Math.round(minY * skvp.getValue());
        temp.maxX = Math.round(maxX * skvp.getKey());
        temp.maxY = Math.round(maxY * skvp.getValue());

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
            minX = Integer.parseInt(split[0]);
            minY = Integer.parseInt(split[1]);
            maxX = Integer.parseInt(split[2]);
            maxY = Integer.parseInt(split[3]);
        }
    }

    @Override
    public String toString() {
        return getStringValue();
    }

}
