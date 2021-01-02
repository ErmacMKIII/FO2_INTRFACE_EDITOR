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
import java.util.List;
import javax.imageio.ImageIO;
import rs.alexanderstojanovich.fo2ie.frm.FOFRM;
import rs.alexanderstojanovich.fo2ie.frm.FRM;
import rs.alexanderstojanovich.fo2ie.frm.ImageData;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ImageWrapper implements FeatureValue {

    private int fps = 0;
    private int offsetX = 0;
    private int offsetY = 0;

    private String value; // is actually filename
    private BufferedImage[] images;

    public ImageWrapper(String value) {
        this.value = value;
    }

    public void loadImage() throws IOException {
        Configuration instance = Configuration.getInstance();
        File inDir = instance.getInDir();
        final File imgFile = new File(inDir.getPath() + File.separator + value);

        if (imgFile.exists()
                && value.toLowerCase().matches(IMG_EXT_REGEX)) {
            if (value.toLowerCase().matches(IMG_IO_REGEX)) {
                images = new BufferedImage[1];
                images[0] = ImageIO.read(imgFile);
            } else if (value.toLowerCase().matches(IMG_FRM_REGEX)) {
                FRM frm = new FRM(imgFile);
                fps = frm.getFps();

                List<ImageData> frames = frm.getFrames();
                images = new BufferedImage[frames.size()];
                offsetX = (frames.size() > 0) ? frames.get(0).getOffsetX() : 0;
                offsetY = (frames.size() > 0) ? frames.get(0).getOffsetY() : 0;
                int index = 0;
                for (ImageData frame : frames) {
                    images[index++] = frame.toBufferedImage();
                }
            } else if (value.toLowerCase().matches(IMG_FOFRM_REGEX)) {
                FOFRM fofrm = new FOFRM(imgFile);
                fps = fofrm.getFps();
                images = fofrm.getImages();

                offsetX = fofrm.getOffsetX();
                offsetY = fofrm.getOffsetY();

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

        if (value.toLowerCase().matches(IMG_IO_REGEX)) {
            ImageIO.write(images[0], "png", imgFile);
        } else if (value.toLowerCase().matches(IMG_FRM_REGEX)) {
            FRM frm = new FRM(fps, images, offsetX, offsetY);
            frm.write(imgFile);
        } else if (value.toLowerCase().matches(IMG_FOFRM_REGEX)) {
            FOFRM fofrm = new FOFRM(fps, images.length, images, offsetX, offsetY);
            fofrm.write(value, imgFile);
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

    public int getFps() {
        return fps;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public String getValue() {
        return value;
    }

}
