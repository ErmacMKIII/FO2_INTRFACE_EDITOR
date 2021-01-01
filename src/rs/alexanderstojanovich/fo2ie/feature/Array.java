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

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Array implements FeatureValue {

    private float[] array;

    @Override
    public String getStringValue() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (float f : array) {
            if (index < array.length) {
                sb.append(f).append(" ");
            } else if (index == array.length) {
                sb.append(f);
            }
        }
        return sb.toString();
    }

    /**
     * Type is always array
     *
     * @return array type
     */
    @Override
    public Type getType() {
        return Type.ARRAY;
    }

    /**
     * Sets array to the value of string (numbers separated with blank space)
     *
     * @param value array in the string value
     */
    @Override
    public void setStringValue(String value) {
        String[] split = value.trim().split(" ");
        array = new float[split.length];
        int index = 0;
        for (String str : split) {
            array[index++] = Float.parseFloat(str);
        }
    }

    /**
     * Returns float array
     *
     * @return array of floats
     */
    public float[] getArray() {
        return array;
    }

}
