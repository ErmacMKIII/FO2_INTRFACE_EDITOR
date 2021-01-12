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
public class SingleValue implements FeatureValue {

    private int number;

    /**
     * Returns int value
     *
     * @return int value
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets single value (int)
     *
     * @return single value (int)
     */
    @Override
    public String getStringValue() {
        return String.valueOf(number);
    }

    /**
     * Type is always single value
     *
     * @return single value type
     */
    @Override
    public Type getType() {
        return Type.SINGLE_VALUE;
    }

    /**
     * Set value of this from parsed string
     *
     * @param value
     */
    @Override
    public void setStringValue(String value) {
        this.number = Integer.parseInt(value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.number;
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
        final SingleValue other = (SingleValue) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

}
