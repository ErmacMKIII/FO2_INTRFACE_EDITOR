/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.intrface;

import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.Vector4;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface Component {

    /**
     * Gets position of this component
     *
     * @return component position
     */
    public Vector4 getPosition();

    /**
     * Gets feat key of this component
     *
     * @return feature key
     */
    public FeatureKey getFeatureKey();

    /**
     * Gets feat value of this component
     *
     * @return feat value
     */
    public FeatureValue getFeatureValue();

}
