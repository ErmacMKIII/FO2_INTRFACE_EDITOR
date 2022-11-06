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
package rs.alexanderstojanovich.fo2ie.main;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.util.UniqueIdUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class FeatureAction implements Action {

    protected String uniqueId;
    protected GLComponent.Inheritance inheritance;
    protected Intrface intrface;
    protected Date timestamp;
    protected Type type;
    protected final FeatureKey featureKey;

    public FeatureAction(Intrface intrface, GLComponent.Inheritance inheritance, FeatureKey featureKey) {
        this.intrface = intrface;
        this.inheritance = inheritance;
        this.featureKey = featureKey;
        this.timestamp = Date.from(Instant.now());
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
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public Type getType() {
        return type;
    }  

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.uniqueId);
        hash = 29 * hash + Objects.hashCode(this.inheritance);
        hash = 29 * hash + Objects.hashCode(this.intrface);
        hash = 29 * hash + Objects.hashCode(this.type);
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
        if (this.type != other.type) {
            return false;
        }
        return Objects.equals(this.featureKey, other.featureKey);
    }
    
    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    public static class AddFeature extends FeatureAction {

        public AddFeature(Intrface intrface, GLComponent.Inheritance inheritance, FeatureKey featureKey) {
            super(intrface, inheritance, featureKey);
            this.type = Type.ADD_FEATURE;
            this.uniqueId = UniqueIdUtils.GenerateNewUniqueId(featureKey, type, inheritance, getDescription());
        }

        @Override
        public void undo() {
            if (inheritance == GLComponent.Inheritance.BASE) {
                intrface.getCommonFeatMap().remove(featureKey);
            } else if (inheritance == GLComponent.Inheritance.DERIVED) {
                intrface.getResolutionPragma().getCustomFeatMap().remove(featureKey);
            }
        }

        @Override
        public String getDescription() {
            return featureKey.getStringValue();
        }

        
    }

    public static class EditFeature extends FeatureAction {

        protected FeatureValue prevFeatureValue;

        public EditFeature(Intrface intrface, FeatureValue prevFeatureValue, GLComponent.Inheritance inheritance, FeatureKey featureKey) {
            super(intrface, inheritance, featureKey);
            this.type = Type.EDIT_FEATURE;
            this.prevFeatureValue = prevFeatureValue;
            this.uniqueId = UniqueIdUtils.GenerateNewUniqueId(featureKey, type, inheritance, getDescription());
        }

        @Override
        public void undo() {
            if (inheritance == GLComponent.Inheritance.BASE) {
                intrface.getCommonFeatMap().replace(featureKey, prevFeatureValue);
            } else if (inheritance == GLComponent.Inheritance.DERIVED) {
                intrface.getResolutionPragma().getCustomFeatMap().replace(featureKey, prevFeatureValue);
            }
        }

        private FeatureValue getCurrentFeatureValue() {
            FeatureValue result = null;
            if (inheritance == GLComponent.Inheritance.BASE) {
                result = intrface.getCommonFeatMap().get(featureKey);
            } else if (inheritance == GLComponent.Inheritance.DERIVED) {
                result = intrface.getResolutionPragma().getCustomFeatMap().get(featureKey);
            }
            return result;
        }

        @Override
        public String getDescription() {
            return prevFeatureValue + " => " + getCurrentFeatureValue();
        }

        public FeatureValue getPrevFeatureValue() {
            return prevFeatureValue;
        }

        public void setPrevFeatureValue(FeatureValue prevFeatureValue) {
            this.prevFeatureValue = prevFeatureValue;
        }

    }

    public static class RemoveFeature extends FeatureAction {

        protected FeatureValue prevFeatureValue;

        public RemoveFeature(Intrface intrface, FeatureValue prevFeatureValue, GLComponent.Inheritance inheritance, FeatureKey featureKey) {
            super(intrface, inheritance, featureKey);
            this.type = Type.REMOVE_FEATURE;
            this.prevFeatureValue = prevFeatureValue;
            this.uniqueId = UniqueIdUtils.GenerateNewUniqueId(featureKey, type, inheritance, getDescription());
        }

        @Override
        public void undo() {
            if (inheritance == GLComponent.Inheritance.BASE) {
                intrface.getCommonFeatMap().put(featureKey, prevFeatureValue);
            } else if (inheritance == GLComponent.Inheritance.DERIVED) {
                intrface.getResolutionPragma().getCustomFeatMap().put(featureKey, prevFeatureValue);
            }
        }

        @Override
        public String getDescription() {
            return featureKey.getStringValue();
        }

    }

}
