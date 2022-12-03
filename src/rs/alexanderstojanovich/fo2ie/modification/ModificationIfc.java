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
package rs.alexanderstojanovich.fo2ie.modification;

import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.intrface.Dictionary;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface ModificationIfc {

    /**
     * Get Unique Id of this modification.
     *
     * @return Unique Id
     */
    public String getUniqueId();

    /**
     * *
     * Get Feature Key of this modification. That Feature Key has different
     * original mapping and modified mapping.
     *
     * @return Feature Key
     */
    public FeatureKey getFeatureKey();

    /**
     * Get Original Bindings
     *
     * @return dictionary with original bindings
     */
    public Dictionary getOriginalVersion();

    /**
     * Get Modified Bindings
     *
     * @return dictionary with modified bindings
     */
    public Dictionary getModifiedVersion();

    /**
     * Get Modified Feature Value
     *
     * @return modified feature value
     */
    public FeatureValue getModifiedValue();

    /**
     * Get Original Feature Value
     *
     * @return original feature value
     */
    public FeatureValue getOriginalValue();

    /**
     * Get Inheritance which either in BASE (all resolutions) or DERIVED
     * (specific resolution) domain
     *
     * @return interitance {BASE, DERIVED}
     */
    public GLComponent.Inheritance getInheritance();

    public void undo();

    /**
     * Get Original Value Formatted to return string (with safe against null)
     *
     * @return safe formatted original value
     */
    public String getOriginalValueFormatted();

    /**
     * Get Modified Value Formatted to return string (with safe against null)
     *
     * @return safe formatted modified value
     */
    public String getModifiedValueFormatted();
}
