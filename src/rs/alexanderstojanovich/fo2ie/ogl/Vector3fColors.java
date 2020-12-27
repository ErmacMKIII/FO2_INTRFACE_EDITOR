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

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Vector3fColors { // some of the defined colors

    public static final Vector3f BLACK = new Vector3f();
    public static final Vector3f WHITE = new Vector3f(1.0f, 1.0f, 1.0f);

    public static final Vector3f RED = new Vector3f(1.0f, 0.0f, 0.0f);
    public static final Vector3f GREEN = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final Vector3f BLUE = new Vector3f(0.0f, 0.0f, 1.0f);

    public static final Vector3f CYAN = new Vector3f(0.0f, 1.0f, 1.0f);
    public static final Vector3f MAGENTA = new Vector3f(1.0f, 0.0f, 1.0f);
    public static final Vector3f YELLOW = new Vector3f(1.0f, 1.0f, 0.0f);

    public static final Vector3f GRAY = new Vector3f(0.5f, 0.5f, 0.5f);

    public static final Vector3f DARK_RED = new Vector3f(0.5f, 0.0f, 0.0f);
    public static final Vector3f DARK_GREEN = new Vector3f(0.0f, 0.5f, 0.0f);
    public static final Vector3f DARK_BLUE = new Vector3f(0.0f, 0.0f, 0.5f);

    public static final Vector3f DARK_CYAN = new Vector3f(0.0f, 0.5f, 0.5f);
    public static final Vector3f DARK_MAGENTA = new Vector3f(0.5f, 0.0f, 0.5f);
    public static final Vector3f DARK_YELLOW = new Vector3f(0.5f, 0.0f, 0.5f);

    public static final Map<Byte, Vector3f> PALETTE = new HashMap<>();
    public static final Map<Vector3f, Byte> COLOR_TO_INDEX = new HashMap<>();

    static {
        PALETTE.put((byte) 0, BLACK);
        PALETTE.put((byte) 1, WHITE);

        PALETTE.put((byte) 2, RED);
        PALETTE.put((byte) 3, GREEN);
        PALETTE.put((byte) 4, BLUE);

        PALETTE.put((byte) 5, CYAN);
        PALETTE.put((byte) 6, MAGENTA);
        PALETTE.put((byte) 7, YELLOW);

        PALETTE.put((byte) 8, GRAY);

        PALETTE.put((byte) 9, DARK_RED);
        PALETTE.put((byte) 10, DARK_GREEN);
        PALETTE.put((byte) 11, DARK_BLUE);

        PALETTE.put((byte) 12, DARK_CYAN);
        PALETTE.put((byte) 13, DARK_MAGENTA);
        PALETTE.put((byte) 14, DARK_YELLOW);

        int i = 0;
        for (Vector3f color : PALETTE.values()) {
            COLOR_TO_INDEX.put(color, (byte) i++);
        }
    }

}
