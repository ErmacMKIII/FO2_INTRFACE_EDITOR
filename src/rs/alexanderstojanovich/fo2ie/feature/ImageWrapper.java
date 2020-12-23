/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.feature;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ImageWrapper implements FeatureValue {

    private String value; // is actually filename
    private BufferedImage image;

    public ImageWrapper(String value) throws IOException {
        this.value = value;
        Configuration instance = Configuration.getInstance();
        File inDir = instance.getInDir();
        final File imgFile = new File(inDir.getPath() + File.separator + value);
        if (imgFile.exists()) {
            this.image = ImageIO.read(imgFile);
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
        ImageIO.write(image, "png", imgFile);
    }

    /**
     * Get the image
     *
     * @return featured image
     */
    public BufferedImage getImage() {
        return image;
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
