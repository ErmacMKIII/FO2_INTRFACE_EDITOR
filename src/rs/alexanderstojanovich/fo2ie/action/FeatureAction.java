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
package rs.alexanderstojanovich.fo2ie.action;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.Resolution;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.util.UniqueIdUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class FeatureAction implements Action {

    protected String uniqueId;
    protected GLComponent.Inheritance inheritance;
    protected Intrface intrface;
    protected final FeatureKey featureKey;
    protected final Resolution resolution;

    public FeatureAction(Intrface intrface, FeatureKey featureKey, GLComponent.Inheritance inheritance, Resolution resolution) {
        this.intrface = intrface;
        this.featureKey = featureKey;
        this.inheritance = inheritance;
        this.resolution = resolution;
        this.uniqueId = UniqueIdUtils.GenerateNewUniqueId(featureKey, getOriginalValue(), getWorkingValue(), inheritance, resolution);
    }

    @Override
    public GLComponent.Inheritance getInheritance() {
        return inheritance;
    }

    public Intrface getIntrface() {
        return intrface;
    }

    @Override
    public FeatureKey getFeatureKey() {
        return featureKey;
    }

    @Override
    public FeatureValue getWorkingValue() {
        FeatureValue result = null;
        switch (inheritance) {
            case BASE:
                result = intrface.getWorkingBinds().commonFeatMap.get(featureKey);
                break;
            case DERIVED:
                ResolutionPragma resPragma = intrface.getWorkingBinds().customResolutions.stream().filter(x -> x.getResolution().equals(resolution)).findFirst().orElse(null);
                if (resPragma != null) {
                    result = resPragma.getCustomFeatMap().get(featureKey);
                }
                break;
        }

        return result;
    }

    @Override
    public FeatureValue getOriginalValue() {
        FeatureValue result = null;
        switch (inheritance) {
            case BASE:
                result = intrface.getOriginalBinds().commonFeatMap.get(featureKey);
                break;
            case DERIVED:
                ResolutionPragma resPragma = intrface.getOriginalBinds().customResolutions.stream().filter(x -> x.getResolution().equals(resolution)).findFirst().orElse(null);
                if (resPragma != null) {
                    result = resPragma.getCustomFeatMap().get(featureKey);
                }
                break;
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.uniqueId);
        hash = 29 * hash + Objects.hashCode(this.inheritance);
        hash = 29 * hash + Objects.hashCode(this.intrface);
        hash = 29 * hash + Objects.hashCode(this.featureKey);
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
        final FeatureAction other = (FeatureAction) obj;
        if (!Objects.equals(this.uniqueId, other.uniqueId)) {
            return false;
        }
        if (this.inheritance != other.inheritance) {
            return false;
        }
        if (!Objects.equals(this.intrface, other.intrface)) {
            return false;
        }

        return Objects.equals(this.featureKey, other.featureKey);
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void undo() {
        FeatureValue originalValue = getOriginalValue();
        switch (inheritance) {
            case BASE:
                if (originalValue == null) {
                    intrface.getWorkingBinds().commonFeatMap.remove(featureKey);
                } else {
                    intrface.getWorkingBinds().commonFeatMap.replace(featureKey, getWorkingValue(), originalValue);
                }
                break;

            case DERIVED:
                ResolutionPragma resPragma = intrface.getOriginalBinds().customResolutions.stream().filter(x -> x.getResolution().equals(resolution)).findFirst().orElse(null);
                if (originalValue == null) {
                    resPragma.getCustomFeatMap().remove(featureKey);
                } else {
                    resPragma.getCustomFeatMap().replace(featureKey, getWorkingValue(), getOriginalValue());
                }
                break;
        }
    }

}
