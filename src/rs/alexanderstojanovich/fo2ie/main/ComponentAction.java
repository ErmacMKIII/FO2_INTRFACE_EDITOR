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
import org.joml.Vector2f;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.util.UniqueIdUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class ComponentAction implements Action {

    protected final GLComponent glComponent;
    protected GLComponent.Inheritance inheritance;
    protected Date timestamp;
    protected Type type;
    protected Module module;
    protected String uniqueId;

    public ComponentAction(GLComponent component, GLComponent.Inheritance inheritance, Module module) {
        this.glComponent = component;
        this.inheritance = inheritance;
        this.module = module;
        this.timestamp = Date.from(Instant.now());
    }

    public GLComponent getGlComponent() {
        return glComponent;
    }

    public GLComponent.Inheritance getInheritance() {
        return inheritance;
    }

    public Module getModule() {
        return module;
    }

    @Override
    public abstract void undo();

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public abstract String getDescription();

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    public static class EnableComponent extends ComponentAction {

        private final boolean prevEnabled;

        public EnableComponent(boolean prevEnabled, GLComponent component, GLComponent.Inheritance inheritance, Module module) {
            super(component, inheritance, module);
            this.type = Type.ENABLE_COMPONENT;
            this.prevEnabled = prevEnabled;
            this.uniqueId = UniqueIdUtils.GenerateNewUniqueId(getDescription());
        }

        @Override
        public void undo() {
            glComponent.setEnabled(prevEnabled);
        }

        @Override
        public String getDescription() {
            return type + ">" + inheritance + ":" + glComponent.getFeatureKey() + "(" + prevEnabled + "=>" + glComponent.isEnabled() + ")" + "@" + timestamp.toString();
        }

    }

    public static class EditComponent extends ComponentAction {

        private final Vector2f prevPos;

        public EditComponent(Vector2f prevPos, GLComponent component, GLComponent.Inheritance inheritance, Module module) {
            super(component, inheritance, module);
            this.type = Type.EDIT_COMPONENT;
            this.prevPos = prevPos;
            this.uniqueId = UniqueIdUtils.GenerateNewUniqueId(getDescription());
        }

        @Override
        public void undo() {
            glComponent.setPos(prevPos);
        }

        @Override
        public String getDescription() {
            return type + ">" + inheritance + ":" + glComponent.getFeatureKey() + "(" + prevPos.toString() + "=>" + glComponent.toString() + ")" + "@" + timestamp.toString();
        }

    }
}
