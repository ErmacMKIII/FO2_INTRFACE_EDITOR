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

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ScalingUtils {

    /**
     * Get scale XY factor for my rectangle and fonts.
     *
     * @param width actual width
     * @param height actual height
     * @param newWidth new width
     * @param newHeight new height
     * @return Pair[SX, SY] is scaling for X-axis and Y-axis
     */
    public static Pair<Float, Float> scaleXYFactor(int width, int height, int newWidth, int newHeight) {
        final float key = MathUtils.getScaled(1.0f, 0.0f, (float) width, 0.0f, (float) newWidth);
        final float value = MathUtils.getScaled(1.0f, 0.0f, (float) height, 0.0f, (float) newHeight);

        return new Pair<>(key, value);
    }

}
