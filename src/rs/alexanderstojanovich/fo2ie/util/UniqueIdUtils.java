/*
 * Copyright (C) 2022 Alexander Stojanovich <coas91@rocketmail.com>
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

import java.util.UUID;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent.Inheritance;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class UniqueIdUtils {

    public static final String GenerateNewUniqueId(FeatureKey featureKey, GLComponent.Type type, Inheritance inheritance, int width, int height) {
        String dim = String.valueOf(width) + "x" + String.valueOf(height);
        String string = String.valueOf(featureKey) + type + inheritance + dim;
        UUID guid = UUID.nameUUIDFromBytes(string.getBytes());
        String guidStr = guid.toString();
        String result = guidStr.substring(0, 8) + guidStr.substring(24, 36);
        return result;
    }
}
