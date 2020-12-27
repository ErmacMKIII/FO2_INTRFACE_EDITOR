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
package rs.alexanderstojanovich.fo2ie.feature;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import rs.alexanderstojanovich.fo2ie.frm.FRM;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ImageWrapper implements FeatureValue {

    private String value; // is actually filename
    private BufferedImage[] images;
    private FRM frm; // null if image is not FRM

    public ImageWrapper(String value) {
        this.value = value;
    }

    public void loadImage() throws IOException {
        Configuration instance = Configuration.getInstance();
        File inDir = instance.getInDir();
        final File imgFile = new File(inDir.getPath() + File.separator + value);
        if (imgFile.exists()) {
            if (value.matches(IMG_EXT_REGEX)) {
                images = new BufferedImage[1];
                images[0] = ImageIO.read(imgFile);
            } else if (value.endsWith(".frm") | value.endsWith(".FRM")) {
                frm = new FRM(imgFile);
                images = (BufferedImage[]) frm.getFrames().toArray();
            }
        }
    }

    /**
     * Write image to file system
     *
     * @throws IOException if writing image fails
     */
    public void writeImage() throws IOException {
        Configuration instance = Configuration.getInstance();
        File outDir = instance.getOutDir();
        final File imgFile = new File(outDir.getPath() + File.separator + value);

        if (value.matches(IMG_EXT_REGEX)) {
            ImageIO.write(images[0], "png", imgFile);
        } else if (value.endsWith(".frm") | value.endsWith(".FRM")) {
            frm.write(imgFile);
        }
    }

    /**
     * Get the image
     *
     * @return featured images (more images if animation)
     */
    public BufferedImage[] getImages() {
        return images;
    }

    /**
     * Value of this image wrapper (image filename)
     *
     * @return image filename
     */
    @Override
    public String getStringValue() {
        return value;
    }

    /**
     * Type is always image
     *
     * @return image type
     */
    @Override
    public Type getType() {
        return Type.IMAGE;
    }

    /**
     * Sets image filename to value
     *
     * @param value new image filename
     */
    @Override
    public void setStringValue(String value) {
        this.value = value;
    }

}
