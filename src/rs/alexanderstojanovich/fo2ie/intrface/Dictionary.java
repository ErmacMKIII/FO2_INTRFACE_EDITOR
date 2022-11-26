/*
 * Copyright (C) 2022 coas9
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Dictionary {

    public final Map<FeatureKey, FeatureValue> commonFeatMap = new LinkedHashMap<>();
    public final List<ResolutionPragma> customResolutions = new ArrayList<>();

    /**
     * Clear common and custom resolutions bindings.
     */
    public void clear() {
        commonFeatMap.clear();
        customResolutions.clear();
    }

    /**
     * Copy bindings to destination dictionary.
     *
     * @param dest destination dictionary (with bindings).
     * @param replace replace bindings
     */
    public void copyTo(Dictionary dest, boolean replace) {
        // copy common feat mappings 
        for (FeatureKey fk : this.commonFeatMap.keySet()) {
            if (replace && dest.commonFeatMap.containsKey(fk)) {
                dest.commonFeatMap.replace(fk, this.commonFeatMap.get(fk));
            } else if (!dest.commonFeatMap.containsKey(fk)) {
                dest.commonFeatMap.putIfAbsent(fk, this.commonFeatMap.get(fk));
            }
        }
        // copy custom resolutions mapping
        for (ResolutionPragma thisRsPrgm : this.customResolutions) {
            // create empty resoloution pragma
            ResolutionPragma destCustomResolutionPragma = new ResolutionPragma(thisRsPrgm.resolution.getWidth(), thisRsPrgm.resolution.getHeight());
            for (FeatureKey fk : thisRsPrgm.customFeatMap.keySet()) {
                if (replace && destCustomResolutionPragma.customFeatMap.containsKey(fk)) {
                    destCustomResolutionPragma.customFeatMap.replace(fk, thisRsPrgm.customFeatMap.get((fk)));
                } else if (!destCustomResolutionPragma.customFeatMap.containsKey(fk)) {
                    destCustomResolutionPragma.customFeatMap.putIfAbsent(fk, thisRsPrgm.customFeatMap.get((fk)));
                }
            }
            dest.customResolutions.add(destCustomResolutionPragma);
        }

    }

    public Map<FeatureKey, FeatureValue> getCommonFeatMap() {
        return commonFeatMap;
    }

    public List<ResolutionPragma> getCustomResolutions() {
        return customResolutions;
    }

}
