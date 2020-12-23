/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
