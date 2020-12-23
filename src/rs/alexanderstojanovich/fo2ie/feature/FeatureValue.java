/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.feature;

import java.io.IOException;
import java.util.regex.Pattern;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface FeatureValue {

    public static final String IMG_EXT_REGEX = ".*(\\.(bmp|gif|jpe?g|png|FOFRM|FRM?))$";
    public static final String NUMBER_REGEX = "-?\\d+(\\.\\d+)?";

    public enum Type {
        IMAGE, VECTOR4, SINGLE_VALUE, ARRAY
    };

    public void setStringValue(String value);

    /**
     * Gets value of this feature
     *
     * @return value of this feature as a string
     */
    public String getStringValue();

    /**
     * Type of the this feature (image, vector, single value or array)
     *
     * @return feature type
     */
    public Type getType();

    /**
     * Gets the value based on string
     *
     * @param string string which contains value (image, vector, single value or
     * array)
     * @return feature value based
     */
    public static FeatureValue valueOf(String string) {
        FeatureValue result = null;
        if (Pattern.matches(IMG_EXT_REGEX, string)) {
            try {
                result = new ImageWrapper(string);
            } catch (IOException ex) {
                FO2IELogger.reportError(ex.getMessage(), ex);
            }
        } else {
            String[] split = string.split(" ");
            switch (split.length) {
                case 1:
                    result = new SingleValue();
                    break;
                case 4:
                    result = new Vector4();
                    break;
                default:
                    result = new Array();
                    break;
            }
        }
        // this essetnial thing is always overriden
        if (result != null) {
            result.setStringValue(string);
        }
        return result;
    }
}
