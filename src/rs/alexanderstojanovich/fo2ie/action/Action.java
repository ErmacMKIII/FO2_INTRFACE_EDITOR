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

import java.util.Date;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface Action {

    public static enum Type {
        ADD_FEATURE, EDIT_FEATURE, REMOVE_FEATURE;
    }

    public String getUniqueId();

    public FeatureKey getFeatureKey();

    public Type getType();

    public GLComponent.Inheritance getInheritance();

    public void undo();

    public Date getTimestamp();

    public String getDescription();

}
