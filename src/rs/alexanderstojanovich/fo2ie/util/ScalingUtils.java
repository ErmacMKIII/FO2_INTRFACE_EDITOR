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
package rs.alexanderstojanovich.fo2ie.util;

import rs.alexanderstojanovich.fo2ie.main.GUI;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ScalingUtils {

    /**
     * Get scale XY factor for my rectangle and fonts.
     *
     * @param modeWidth resolution width
     * @param modeHeight resolution height
     * @param mainPicWidth referent main picture width
     * @param mainPicHeight refererent main picture height
     * @return Pair[SX, SY] is scaling for X-axis and Y-axis
     */
    public static Pair<Float, Float> scaleXYFactor(int modeWidth, int modeHeight, int mainPicWidth, int mainPicHeight) {
        final float sx = (GUI.GL_CANVAS.getWidth() * GUI.GL_CANVAS.getWidth()) / (float) (modeWidth * mainPicWidth);
        final float sy = (GUI.GL_CANVAS.getHeight() * GUI.GL_CANVAS.getHeight()) / (float) (modeHeight * mainPicHeight);

        final float mx = modeWidth / (float) GUI.GL_CANVAS.getWidth();
        final float my = modeHeight / (float) GUI.GL_CANVAS.getHeight();

        float key = sx * mx;
        float value = sy * my;

        return new Pair<>(key, value);
    }

}
