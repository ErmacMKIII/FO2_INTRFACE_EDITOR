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

import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.util.MathUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class MyVector4 extends Vector4f implements FeatureValue {

    public void setScaled(int screenWidth, int screenHeight) {
        x /= 1.0f + MathUtils.absRelativeChange(x, screenWidth);
        y /= 1.0f + MathUtils.absRelativeChange(y, screenHeight);
        z /= 1.0f + MathUtils.absRelativeChange(z, screenWidth);
        w /= 1.0f + MathUtils.absRelativeChange(w, screenHeight);
    }

    public MyVector4 setScaled(int screenWidth, int screenHeight, MyVector4 temp) {
        temp.x = x / (1.0f + MathUtils.absRelativeChange(x, screenWidth));
        temp.y = y / (1.0f + MathUtils.absRelativeChange(y, screenHeight));
        temp.z = z / (1.0f + MathUtils.absRelativeChange(z, screenWidth));
        temp.w = w / (1.0f + MathUtils.absRelativeChange(w, screenHeight));

        return temp;
    }

    @Override
    public String getStringValue() {
        return String.valueOf(Math.round(x))
                + " " + String.valueOf(Math.round(y))
                + " " + String.valueOf(Math.round(z))
                + " " + String.valueOf(Math.round(w));
    }

    @Override
    public Type getType() {
        return Type.VECTOR4;
    }

    @Override
    public void setStringValue(String value) {
        String[] split = value.split(" ");
        if (split.length == 4) {
            x = Float.valueOf(split[0]);
            y = Float.valueOf(split[1]);
            z = Float.valueOf(split[2]);
            w = Float.valueOf(split[3]);
        }
    }

}
