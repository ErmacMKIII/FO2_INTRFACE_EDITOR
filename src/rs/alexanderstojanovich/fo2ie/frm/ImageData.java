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
package rs.alexanderstojanovich.fo2ie.frm;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ImageData {

    private final int width;
    private final int height;

    private final int offsetX;
    private final int offsetY;

    // Image as raw pixels in row-major format
    // Data says on which pixel is which entry in the palette
    private final byte[] data;

    /**
     * Creates new image data with given dimension
     *
     * @param width width of the pixel area
     * @param height height of the pixel area
     * @param offsetX frame offset in X direction
     * @param offsetY frame offset in Y direction
     */
    public ImageData(int width, int height, int offsetX, int offsetY) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.data = new byte[width * height];
    }

    /**
     * Creates new image data from image (converts image to indexed then writes
     * to the data)
     *
     * @param image original image to get the data from
     * @param offsetX frame offset in X direction
     * @param offsetY frame offset in Y direction
     */
    public ImageData(BufferedImage image, int offsetX, int offsetY) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.data = new byte[width * height];
        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                int e = width * py + px;
                int rgb = image.getRGB(px, py);
                byte index = 0;
                Color col = new Color(rgb, true);
                if (col.getAlpha() != 0) {
                    int minDeviation = 255 * 1000;
                    int minIndex = -1;
                    for (int rgbi : Palette.getColors()) {
                        Color coli = new Color(rgbi);
                        int deviation = 299 * Math.abs(col.getRed() - coli.getRed())
                                + 587 * Math.abs(col.getGreen() - coli.getGreen())
                                + 114 * Math.abs(col.getBlue() - coli.getBlue());
                        if (deviation < minDeviation) {
                            minDeviation = deviation;
                            minIndex = index;
                            if (deviation == 0) {
                                break;
                            }
                        }
                        index++;
                    }

                    data[e] = (byte) minIndex;
                }
            }
        }
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Set pixel value to the palette entry
     *
     * @param px pixel x coordinate
     * @param py pixel y coordinate
     * @param val palette entry
     */
    public void setPixel(int px, int py, byte val) {
        final int e = width * py + px;
        data[e] = val;
    }

    /**
     * Gets pixel value (which is entry of the palette)
     *
     * @param px pixel x coordinate
     * @param py pixel y coordinate
     * @return palette entry
     */
    public byte getPixel(int px, int py) {
        final int e = width * py + px;
        return data[e];
    }

    /**
     * Makes Buffered Image based on preloaded Palette and pixel data
     *
     * @return Buffered Image with this pixel data.
     */
    public BufferedImage toBufferedImage() {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                int e = width * py + px;
                result.setRGB(px, py, Palette.getColors()[data[e] & 0xFF]);
            }
        }
        return result;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public byte[] getData() {
        return data;
    }

}
