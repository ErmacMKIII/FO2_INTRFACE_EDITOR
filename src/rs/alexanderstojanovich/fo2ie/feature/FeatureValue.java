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

import java.util.regex.Pattern;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface FeatureValue {

    public static final String IMG_IO_REGEX = ".*(\\.(bmp|jpe?g|png))$";
    public static final String IMG_FRM_REGEX = ".*(\\.(frm?))$";
    public static final String IMG_FOFRM_REGEX = ".*(\\.(fofrm?))$";
    public static final String IMG_EXT_REGEX = ".*(\\.(bmp|jpe?g|png|fofrm|frm?))$";
    public static final String NUMBER_REGEX = "-?\\d+(\\.\\d+)?";
    public static final String NUMBER_ARRAY_REGEX = "^(\\s*-?\\d+(\\.\\d+)?)(\\s*\\s\\s*-?\\d+(\\.\\d+)?)*$";

    public enum Type {
        IMAGE, RECT4, SINGLE_VALUE, ARRAY, UNKNOWN
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
        if (string.isEmpty() || Pattern.matches(IMG_EXT_REGEX, string.toLowerCase())) {
            result = new ImageWrapper(string);
        } else if (Pattern.matches(NUMBER_ARRAY_REGEX, string.toLowerCase())) {
            String[] split = string.split("\\s+");
            switch (split.length) {
                case 1:
                    result = new SingleValue();
                    break;
                case 4:
                    result = new MyRectangle();
                    break;
                default:
                    result = new MyArray();
                    break;
            }
        }
        // this essetnial thing is always overriden
        if (result != null && !string.isEmpty()) {
            result.setStringValue(string);
        }
        return result;
    }
}
