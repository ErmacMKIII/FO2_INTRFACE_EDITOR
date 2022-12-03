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

import java.util.Objects;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.intrface.Dictionary;
import rs.alexanderstojanovich.fo2ie.intrface.Resolution;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.util.UniqueIdUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class FeatureModification implements ModificationIfc {

    protected String uniqueId;
    protected GLComponent.Inheritance inheritance;
    protected final FeatureKey featureKey;
    protected final Resolution resolution;
    private final Dictionary original;
    private final Dictionary modified;

    public FeatureModification(Dictionary original, Dictionary modified, FeatureKey featureKey, GLComponent.Inheritance inheritance, Resolution resolution) {
        this.original = original;
        this.modified = modified;
        this.featureKey = featureKey;
        this.inheritance = inheritance;
        this.resolution = resolution;
        this.uniqueId = UniqueIdUtils.GenerateNewUniqueId(featureKey, inheritance, resolution);
    }

    @Override
    public GLComponent.Inheritance getInheritance() {
        return inheritance;
    }

    @Override
    public FeatureKey getFeatureKey() {
        return featureKey;
    }

    @Override
    public FeatureValue getModifiedValue() {
        FeatureValue result = null;
        switch (inheritance) {
            case BASE:
                result = modified.commonFeatMap.get(featureKey);
                break;
            case DERIVED:
                ResolutionPragma resPragma = modified.customResolutions.stream().filter(x -> x.getResolution().equals(resolution)).findFirst().orElse(null);
                if (resPragma != null) {
                    result = resPragma.getCustomFeatMap().get(featureKey);
                }
                break;
        }

        return result;
    }

    @Override
    public String getOriginalValueFormatted() {
        FeatureValue fv = getOriginalValue();
        if (fv == null) {
            return "";
        } else {
            return fv.getStringValue();
        }
    }

    @Override
    public String getModifiedValueFormatted() {
        FeatureValue fv = getModifiedValue();
        if (fv == null) {
            return "";
        } else {
            return fv.getStringValue();
        }
    }

    @Override
    public FeatureValue getOriginalValue() {
        FeatureValue result = null;
        switch (inheritance) {
            case BASE:
                result = original.commonFeatMap.get(featureKey);
                break;
            case DERIVED:
                ResolutionPragma resPragma = original.customResolutions.stream().filter(x -> x.getResolution().equals(resolution)).findFirst().orElse(null);
                if (resPragma != null) {
                    result = resPragma.getCustomFeatMap().get(featureKey);
                }
                break;
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.uniqueId);
        hash = 23 * hash + Objects.hashCode(this.inheritance);
        hash = 23 * hash + Objects.hashCode(this.featureKey);
        hash = 23 * hash + Objects.hashCode(this.resolution);
        hash = 23 * hash + Objects.hashCode(this.original);
        hash = 23 * hash + Objects.hashCode(this.modified);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FeatureModification other = (FeatureModification) obj;
        if (!Objects.equals(this.uniqueId, other.uniqueId)) {
            return false;
        }
        if (this.inheritance != other.inheritance) {
            return false;
        }
        if (!Objects.equals(this.featureKey, other.featureKey)) {
            return false;
        }
        if (!Objects.equals(this.resolution, other.resolution)) {
            return false;
        }
        if (!Objects.equals(this.original, other.original)) {
            return false;
        }
        return Objects.equals(this.modified, other.modified);
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void undo() {
        FeatureValue originalValue = getOriginalValue();
        FeatureValue modifiedValue = getModifiedValue();
        switch (inheritance) {
            case BASE:
                if (originalValue == null) {
                    modified.commonFeatMap.remove(featureKey);
                } else if (modifiedValue != null) {
                    modified.commonFeatMap.replace(featureKey, modifiedValue, originalValue);
                } else {
                    modified.commonFeatMap.put(featureKey, originalValue);
                }
                break;

            case DERIVED:
                ResolutionPragma resPragma = modified.customResolutions.stream().filter(x -> x.getResolution().equals(resolution)).findFirst().orElse(null);
                if (originalValue == null) {
                    resPragma.getCustomFeatMap().remove(featureKey);
                } else if (modifiedValue != null) {
                    resPragma.getCustomFeatMap().replace(featureKey, modifiedValue, originalValue);
                } else {
                    resPragma.getCustomFeatMap().put(featureKey, originalValue);
                }
                break;
        }
    }

    @Override
    public Dictionary getOriginalVersion() {
        return original;
    }

    @Override
    public Dictionary getModifiedVersion() {
        return modified;
    }

}
