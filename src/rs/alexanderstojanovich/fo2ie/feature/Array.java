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
public class Array implements FeatureValue {

    private float[] array;

    @Override
    public String getStringValue() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (float f : array) {
            if (index < array.length) {
                sb.append(f).append(" ");
            } else if (index == array.length) {
                sb.append(f);
            }
        }
        return sb.toString();
    }

    /**
     * Type is always array
     *
     * @return array type
     */
    @Override
    public Type getType() {
        return Type.ARRAY;
    }

    /**
     * Sets array to the value of string (numbers separated with blank space)
     *
     * @param value array in the string value
     */
    @Override
    public void setStringValue(String value) {
        String[] split = value.trim().split(" ");
        array = new float[split.length];
    }

    /**
     * Returns float array
     *
     * @return array of floats
     */
    public float[] getArray() {
        return array;
    }

}
