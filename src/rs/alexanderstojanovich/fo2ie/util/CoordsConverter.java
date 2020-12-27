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
package rs.alexanderstojanovich.fo2ie.util;

import org.joml.Vector2f;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class CoordsConverter {

    /**
     * Gets OpenGL coordinates of this screen coordinates
     *
     * @param scrnCoords vector containing screen coordinates
     * @param scrnWidth screen width
     * @param scrnHeight screen height
     * @return OpenGL coordinates
     */
    public static Vector2f getOpenGLCoordinates(Vector2f scrnCoords, int scrnWidth, int scrnHeight) {
        float xposGL = (float) (scrnCoords.x / scrnWidth - 0.5f) * 2.0f;
        float yposGL = (float) (0.5f - scrnCoords.y / scrnHeight) * 2.0f;

        return new Vector2f(xposGL, yposGL);
    }

    /**
     * Gets Screen coordinates of this OpenGL coordinates
     *
     * @param glCoords vector containing OpenGL coordinates
     * @param scrnWidth screen width
     * @param scrnHeight screen height
     * @return screen coordinates
     */
    public static Vector2f getScreenCoordinates(Vector2f glCoords, int scrnWidth, int scrnHeight) {
        float xpos = (glCoords.x / 2.0f + 0.5f) * scrnWidth;
        float ypos = (-glCoords.y / 2.0f + 0.5f) * scrnHeight;

        return new Vector2f(xpos, ypos);
    }
}
