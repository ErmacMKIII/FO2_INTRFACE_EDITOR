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
package rs.alexanderstojanovich.fo2ie.intrface;

import java.util.HashMap;
import java.util.Map;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ResolutionPragma {

    private final int width;
    private final int height;

    protected Map<FeatureKey, FeatureValue> customFeatMap = new HashMap<>();

    /**
     *
     * @param inheritedFeats features inherited from the common map
     * @param width resolution width
     * @param height resolution height
     */
    public ResolutionPragma(Map<FeatureKey, FeatureValue> inheritedFeats, int width, int height) {
        this.width = width;
        this.height = height;
        this.customFeatMap.putAll(inheritedFeats);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<FeatureKey, FeatureValue> getCustomFeatMap() {
        return customFeatMap;
    }

}
