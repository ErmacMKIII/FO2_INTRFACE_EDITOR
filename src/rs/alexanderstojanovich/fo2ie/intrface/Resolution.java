/*
 * Copyright (C) 2022 coas9
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
package rs.alexanderstojanovich.fo2ie.intrface;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Resolution {

    protected final int width;
    protected final int height;

    public static Resolution DEFAULT = new Resolution(800, 600);

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.width;
        hash = 71 * hash + this.height;
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
        final Resolution other = (Resolution) obj;
        if (this.width != other.width) {
            return false;
        }
        return this.height == other.height;
    }

    @Override
    public String toString() {
        return "Resolution{" + "width=" + width + ", height=" + height + '}';
    }

}
