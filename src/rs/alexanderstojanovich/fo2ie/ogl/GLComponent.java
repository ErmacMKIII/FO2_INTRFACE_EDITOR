/* 
 * Copyright (C) 2020 Alexander Stojanovich <coas91@rocketmail.com>
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
package rs.alexanderstojanovich.fo2ie.ogl;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface GLComponent {

    /**
     * Type of this GLComponent
     */
    public enum Type {
        PIC, ANIM, TXT, ADDR
    }

    /**
     * Inheritance {CANVAS - cannot be changed, BASE - for base components,
     * DERIVED - for derived components }
     */
    public enum Inheritance {
        CANVAS, BASE, DERIVED
    }

    /**
     * Gets UniqueId bound to this component.
     *
     * @return unique id.
     */
    public String getUniqueId();

    /**
     * Gets Feature Key (Position) bound to this component.
     *
     * @return bound feature key.
     */
    public FeatureKey getPosFeatureKey();

    /**
     * Gets Feature Key linked (Picture or Animation) to this component.
     *
     * @return bound feature key.
     */
    public FeatureKey getLinkFeatureKey();

    /**
     * Is component enabled
     *
     * @return enabled flag
     */
    public boolean isEnabled();

    /**
     * Set enable/disable depending on passed boolean
     *
     * @param enabled GLComponent enabled flag
     */
    public void setEnabled(boolean enabled);

    /**
     * Gets color of this component
     *
     * @return
     */
    public Vector4f getColor();

    /**
     * Sets color for this component
     *
     * @param color chosen color
     */
    public void setColor(Vector4f color);

    /**
     * Gets color of this component
     *
     * @return
     */
    public Vector4f getOutlineColor();

    /**
     * Sets color for this component
     *
     * @param outlineColor chosen outline color
     */
    public void setOutlineColor(Vector4f outlineColor);

    /**
     * Gets OpenGL position of this component
     *
     * @return
     */
    public Vector2f getPos();

    /**
     * Sets OpenGL postion of this component to parsed.
     *
     * @param pos
     */
    public void setPos(Vector2f pos);

    /**
     * Get real width of this component
     *
     * @return real component width
     */
    public int getWidth();

    /**
     * Get real height of this component
     *
     * @return real component height
     */
    public int getHeight();

    /**
     * Gives a hint to the renderer that this component needs buffering
     */
    public void unbuffer();

    /**
     * Gets buffered status of this component. If it's not buffered, requires to
     * be buffered prior to any rendering
     *
     * @return buffered green flag status
     */
    public boolean isBuffered();

    /**
     * Buffer this component
     *
     * @param gl20 GL2 binding
     */
    public void buffer(GL2 gl20);

    /**
     * Render this component
     *
     * @param gl20 GL2 binding
     * @param projMat4 projection matrix
     * @param program shader program for rendering
     */
    public void render(GL2 gl20, Matrix4f projMat4, ShaderProgram program);

    /**
     * Gets Type of the GLComponent
     *
     * @return GLComponent type {PIC, TXT}
     */
    public Type getType();

    /**
     * Gets component relative width to the OpenGL canvas.
     *
     * @return GL width.
     */
    public float getRelativeWidth();

    /**
     * Gets component relative width to the OpenGL canvas.
     *
     * @return GL height.
     */
    public float getRelativeHeight();

    /**
     * Gets rectangular pixel surface area of component
     *
     * @return pixel rectangular surface
     */
    public Rectanglef getPixelArea();

    /**
     * Gets rectangular pixel surface GL area of component
     *
     * @return pixel rectangular GL surface
     */
    public Rectanglef getGLArea();

    /**
     * Gets Inheritance of the GLComponent
     *
     * @return GLComponent Inheritance {BASE, DERIVED}
     */
    public Inheritance getInheritance();
}
