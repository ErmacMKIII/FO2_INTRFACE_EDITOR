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
public class SingleValue implements FeatureValue {

    private int number;

    /**
     * Returns int value
     *
     * @return int value
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets single value (int)
     *
     * @return single value (int)
     */
    @Override
    public String getStringValue() {
        return String.valueOf(number);
    }

    /**
     * Type is always single value
     *
     * @return single value type
     */
    @Override
    public Type getType() {
        return Type.SINGLE_VALUE;
    }

    /**
     * Set value of this from parsed string
     *
     * @param value
     */
    @Override
    public void setStringValue(String value) {
        this.number = Integer.parseInt(value);
    }

}
