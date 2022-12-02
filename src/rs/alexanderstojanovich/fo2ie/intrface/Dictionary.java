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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import rs.alexanderstojanovich.fo2ie.modification.FeatureModification;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.modification.ModificationIfc;

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
                FeatureValue fv = FeatureValue.valueOf(this.commonFeatMap.get(fk).getStringValue());
                dest.commonFeatMap.replace(fk, fv);
            } else if (!dest.commonFeatMap.containsKey(fk)) {
                FeatureValue fv = FeatureValue.valueOf(this.commonFeatMap.get(fk).getStringValue());
                dest.commonFeatMap.putIfAbsent(fk, fv);
            }
        }
        // copy custom resolutions mapping
        for (ResolutionPragma thisRsPrgm : this.customResolutions) {
            // create empty resoloution pragma
            ResolutionPragma destCustomResolutionPragma = new ResolutionPragma(thisRsPrgm.resolution.getWidth(), thisRsPrgm.resolution.getHeight());
            for (FeatureKey fk : thisRsPrgm.customFeatMap.keySet()) {
                if (replace && destCustomResolutionPragma.customFeatMap.containsKey(fk)) {
                    FeatureValue fv = FeatureValue.valueOf(thisRsPrgm.customFeatMap.get(fk).getStringValue());
                    destCustomResolutionPragma.customFeatMap.replace(fk, fv);
                } else if (!destCustomResolutionPragma.customFeatMap.containsKey(fk)) {
                    FeatureValue fv = FeatureValue.valueOf(thisRsPrgm.customFeatMap.get(fk).getStringValue());
                    destCustomResolutionPragma.customFeatMap.putIfAbsent(fk, fv);
                }
            }
            dest.customResolutions.add(destCustomResolutionPragma);
        }

    }

    /**
     * Get List of Modifications when dictionary modified is compared to
     * original.
     *
     * @param original original dictionary
     * @param modified modified dictionary
     * @return list of modifications (possible to undo)
     */
    public static List<ModificationIfc> difference(Dictionary original, Dictionary modified) {
        List<ModificationIfc> result = new ArrayList<>();

        Set<FeatureKey> oCommonKeyset = original.commonFeatMap.keySet();
        Set<FeatureKey> mCommonKeyset = modified.commonFeatMap.keySet();
        Set<FeatureKey> uCommonKeyset = new LinkedHashSet<>();
        uCommonKeyset.addAll(oCommonKeyset);
        uCommonKeyset.addAll(mCommonKeyset);

        List<FeatureKey> moCommonDifference = uCommonKeyset.stream().filter(u -> !oCommonKeyset.contains(u) ^ !mCommonKeyset.contains(u) || !modified.commonFeatMap.get(u).equals(original.commonFeatMap.get(u))).collect(Collectors.toList());

        for (FeatureKey featureKey : moCommonDifference) {
            ModificationIfc modification = new FeatureModification(original, modified, featureKey, GLComponent.Inheritance.BASE, Resolution.DEFAULT);
            result.add(modification);
        }

        for (ResolutionPragma mCustResolutionPragma : modified.customResolutions) {
            ResolutionPragma oCustResolutionPragma = original.customResolutions.stream().filter(x -> x.resolution.equals(mCustResolutionPragma.resolution)).findFirst().orElse(null);
            if (oCustResolutionPragma != null) {
                Set<FeatureKey> oCustomKeyset = oCustResolutionPragma.customFeatMap.keySet();
                Set<FeatureKey> mCustomKeyset = mCustResolutionPragma.customFeatMap.keySet();
                Set<FeatureKey> uCustomKeyset = new LinkedHashSet<>();
                uCustomKeyset.addAll(oCustomKeyset);
                uCustomKeyset.addAll(mCustomKeyset);

                List<FeatureKey> moCustomDifference = uCustomKeyset.stream().filter(u -> !oCustomKeyset.contains(u) ^ !mCustomKeyset.contains(u) || !mCustResolutionPragma.customFeatMap.get(u).equals(oCustResolutionPragma.customFeatMap.get(u))).collect(Collectors.toList());
                for (FeatureKey featureKey : moCustomDifference) {
                    ModificationIfc modification = new FeatureModification(original, modified, featureKey, GLComponent.Inheritance.DERIVED, mCustResolutionPragma.resolution);
                    result.add(modification);
                }
            }
        }

        return result;
    }

    /**
     * Get List of Modifications when dictionary modified is compared to
     * original.
     *
     * @param original original dictionary
     * @param modified modified dictionary
     * @param outResult out result list of modifications (possible to undo)
     * @return is there any difference (true - if they differ, false - equal)
     */
    public static boolean difference(Dictionary original, Dictionary modified, List<ModificationIfc> outResult) {
        outResult.clear();

        Set<FeatureKey> oCommonKeyset = original.commonFeatMap.keySet();
        Set<FeatureKey> mCommonKeyset = modified.commonFeatMap.keySet();
        Set<FeatureKey> uCommonKeyset = new LinkedHashSet<>();
        uCommonKeyset.addAll(oCommonKeyset);
        uCommonKeyset.addAll(mCommonKeyset);

        List<FeatureKey> moCommonDifference = uCommonKeyset.stream().filter(u -> !oCommonKeyset.contains(u) ^ !mCommonKeyset.contains(u) || !Objects.equals(modified.commonFeatMap.get(u), original.commonFeatMap.get(u))).collect(Collectors.toList());

        for (FeatureKey featureKey : moCommonDifference) {
            ModificationIfc modification = new FeatureModification(original, modified, featureKey, GLComponent.Inheritance.BASE, Resolution.DEFAULT);
            outResult.add(modification);
        }

        for (ResolutionPragma mCustResolutionPragma : modified.customResolutions) {
            ResolutionPragma oCustResolutionPragma = original.customResolutions.stream().filter(x -> x.resolution.equals(mCustResolutionPragma.resolution)).findFirst().orElse(null);
            if (oCustResolutionPragma != null) {
                Set<FeatureKey> oCustomKeyset = oCustResolutionPragma.customFeatMap.keySet();
                Set<FeatureKey> mCustomKeyset = mCustResolutionPragma.customFeatMap.keySet();
                Set<FeatureKey> uCustomKeyset = new LinkedHashSet<>();
                uCustomKeyset.addAll(oCustomKeyset);
                uCustomKeyset.addAll(mCustomKeyset);

                List<FeatureKey> moCustomDifference = uCustomKeyset.stream().filter(u -> !oCustomKeyset.contains(u) ^ !mCustomKeyset.contains(u) || !Objects.equals(mCustResolutionPragma.customFeatMap.get(u), oCustResolutionPragma.customFeatMap.get(u))).collect(Collectors.toList());
                for (FeatureKey featureKey : moCustomDifference) {
                    ModificationIfc modification = new FeatureModification(original, modified, featureKey, GLComponent.Inheritance.DERIVED, mCustResolutionPragma.resolution);
                    outResult.add(modification);
                }
            }
        }

        return !outResult.isEmpty();
    }

    public Map<FeatureKey, FeatureValue> getCommonFeatMap() {
        return commonFeatMap;
    }

    public List<ResolutionPragma> getCustomResolutions() {
        return customResolutions;
    }

}
