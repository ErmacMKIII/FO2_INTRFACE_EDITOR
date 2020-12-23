/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.intrface;

import java.util.Objects;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.ImageWrapper;
import rs.alexanderstojanovich.fo2ie.feature.Vector4;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ImageComponent implements Component {

    private final FeatureKey featureKey; // example "LogExitPicDn"
    private Vector4 position = new Vector4(0, 0, 0, 0);
    private ImageWrapper image;
    private int offsetX = 0;
    private int offsetY = 0;

    /**
     * Constructs interface image component with no position
     *
     * @param imageKey left side image key
     * @param image image holder
     */
    public ImageComponent(FeatureKey imageKey, ImageWrapper image) {
        this.featureKey = imageKey;
        this.image = image;
    }

    /**
     * Constructs interface image component
     *
     * @param imageKey left side image key
     * @param image image holder
     * @param position image position
     *
     */
    public ImageComponent(FeatureKey imageKey, ImageWrapper image, Vector4 position) {
        this.featureKey = imageKey;
        this.image = image;
        this.position = position;
    }

    /**
     * Gets position of the image
     *
     * @return image position
     */
    @Override
    public Vector4 getPosition() {
        return position;
    }

    /**
     * Gets image wrapper which holds the image
     *
     * @return image holder
     */
    public ImageWrapper getImage() {
        return image;
    }

    /**
     * Same as get image
     *
     * @return
     */
    @Override
    public FeatureValue getFeatureValue() {
        return image;
    }

    /**
     * Gets X-axis offset
     *
     * @return
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Gets Y-axis offset
     *
     * @return
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Gets image key on the left side of equality sign in ini file
     *
     * @return image key
     */
    @Override
    public FeatureKey getFeatureKey() {
        return featureKey;
    }

    public void setPosition(Vector4 position) {
        this.position = position;
    }

    public void setImage(ImageWrapper image) {
        this.image = image;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.featureKey);
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
        final ImageComponent other = (ImageComponent) obj;
        if (this.featureKey != other.featureKey) {
            return false;
        }
        return true;
    }

}
