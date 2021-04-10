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

import java.util.Arrays;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class MyArray implements FeatureValue {

    private int[] array;

    @Override
    public String getStringValue() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (int f : array) {
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
        String[] split = value.trim().split("\\s+");
        array = new int[split.length];
        int index = 0;
        for (String str : split) {
            array[index++] = Integer.parseInt(str);
        }
    }

    /**
     * Returns int array
     *
     * @return array of ints
     */
    public int[] getArray() {
        return array;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.hashCode(this.array);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MyArray other = (MyArray) obj;
        if (!Arrays.equals(this.array, other.array)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getStringValue();
    }

}
