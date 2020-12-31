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
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface GLComponent {

    /**
     * Type of this GLComponent
     */
    public enum Type {
        PRIM, PIC, TXT
    }

    /**
     * Gets color of this component
     *
     * @return
     */
    public Vector4f getColor();

    /**
     * Gets OpenGL position of this component
     *
     * @return
     */
    public Vector2f getPos();

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
}
