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

import org.joml.Vector4i;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class MyVector4 extends Vector4i implements FeatureValue {

    public MyVector4() {

    }

    @Override
    public String getStringValue() {
        return String.valueOf(x)
                + " " + String.valueOf(y)
                + " " + String.valueOf(z)
                + " " + String.valueOf(w);
    }

    @Override
    public Type getType() {
        return Type.VECTOR4;
    }

    @Override
    public void setStringValue(String value) {
        String[] split = value.split(" ");
        if (split.length == 4) {
            x = Integer.valueOf(split[0]);
            y = Integer.valueOf(split[1]);
            z = Integer.valueOf(split[2]);
            w = Integer.valueOf(split[3]);
        }
    }

}
