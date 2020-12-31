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

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class MathUtils {

    /**
     * Gets relative change
     *
     * @param a first argument
     * @param b second argument, reference
     * @return relative change in interval [0, 1]
     */
    public static float relativeChange(float a, float b) {
        return (a - b) / (float) (b);
    }

    /**
     * Gets absolute relative change
     *
     * @param a first argument
     * @param b second argument, reference
     * @return relative change in interval [0, 1]
     */
    public static float absRelativeChange(float a, float b) {
        return Math.abs(a - b) / (float) (b);
    }

    /**
     * Scaling argument with changing their range where they reside
     *
     * @param x argument to scale
     * @param min left bound of interval [min, max] where x is
     * @param max right bound of interval [min, max] where x is
     * @param minNew left bound of the new interval [minNew, maxNew] where x
     * should be
     * @param maxNew right bound of the new interval [minNew, maxNew] where x
     * should be
     * @return scaled argument
     */
    public static float getScaled(float x, float min, float max, float minNew, float maxNew) {
        return ((maxNew - minNew) * (x - min) / (max - min)) + minNew;
    }

}
