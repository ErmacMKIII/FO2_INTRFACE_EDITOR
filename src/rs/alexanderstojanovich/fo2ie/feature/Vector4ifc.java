/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.feature;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface Vector4ifc extends FeatureValue {

    /**
     * Gets X component value
     *
     * @return X component value
     */
    public int getX();

    /**
     * Gets X component value
     *
     * @return X component value
     */
    public int getY();

    /**
     * Gets Z component value
     *
     * @return Z component value
     */
    public int getZ();

    /**
     * Gets W component value
     *
     * @return W component value
     */
    public int getW();

    /**
     * Adds vector to this vector
     *
     * @param vec4 vector to add
     */
    public void add(Vector4ifc vec4);

    /**
     * Subtract vector from this vector
     *
     * @param vec4 vector to sub
     */
    public void sub(Vector4ifc vec4);

    /**
     * Multiply this vector with real number
     *
     * @param scalar real number to multiply with
     */
    public void mul(int scalar);

    /**
     * Divide this vector with real number (other than zero)
     *
     * @param scalar real number to divide this vector with
     */
    public void div(int scalar);
}
