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
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.intrface.Resolution;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class UniqueIdUtils {

    /**
     * Generate new UniqueId for Action - Modification to the bindings.
     *
     * @param featureKey feature key
     * @param inheritance BASE or DEREIVED
     * @param resolution DEFAULT(800x600) or RESOLUTION(width x height)
     *
     * @return UniqueId
     */
    public static final String GenerateNewUniqueId(FeatureKey featureKey, GLComponent.Inheritance inheritance, Resolution resolution) {
        int width = resolution.getWidth();
        int height = resolution.getHeight();
        String dim = String.valueOf(width) + "x" + String.valueOf(height);
        String string = String.valueOf(featureKey) + inheritance + dim;
        UUID guid = UUID.nameUUIDFromBytes(string.getBytes());
        String guidStr = guid.toString();
        String result = guidStr.substring(0, 8) + guidStr.substring(24, 36);
        return result;
    }

    /**
     * Generate new UniqueId for Action - Modification to the bindings.
     *
     * @param featureKey feature key
     * @param type GLComponent type
     * @param inheritance BASE or DERIVED
     * @param width GLComponent pixel width
     * @param height GLComponent pixel height
     *
     * @return UniqueId
     */
    public static String GenerateNewUniqueId(FeatureKey featureKey, GLComponent.Type type, GLComponent.Inheritance inheritance, int width, int height) {
        String dim = String.valueOf(width) + "x" + String.valueOf(height);
        String string = String.valueOf(featureKey) + type + inheritance + dim;
        UUID guid = UUID.nameUUIDFromBytes(string.getBytes());
        String guidStr = guid.toString();
        String result = guidStr.substring(0, 8) + guidStr.substring(24, 36);
        return result;
    }

}
