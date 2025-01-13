/*
 * Copyright (C) 2025 Alexander Stojanovich <coas91@rocketmail.com>
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
package rs.alexanderstojanovich.fo2ie.util;

import java.util.regex.Pattern;

import java.util.regex.Pattern;

/**
 * Utility class for file operations
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class FileUtils {

    /**
     * Convert a single wildcard pattern to regex
     *
     * @param pattern the wildcard pattern to convert (* for any characters, ?
     * for a single character)
     * @return compiled regex pattern for the given wildcard
     * @throws IllegalArgumentException if pattern is null or empty
     */
    public static Pattern wildcardToRegex(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }

        // Escape regex characters manually, then replace wildcards
        String regexPattern = "^" + pattern
                .replace(".", "\\.") // Escape '.' for literal match
                .replace("*", ".*") // Convert '*' to match zero or more characters
                .replace("?", ".") // Convert '?' to match exactly one character
                + "$";

        return Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
    }
}
