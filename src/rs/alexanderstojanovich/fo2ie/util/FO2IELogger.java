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
package rs.alexanderstojanovich.fo2ie.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class FO2IELogger {

    private static final Logger MY_LOGGER = Logger.getLogger(FO2IELogger.class.getName());

    private static String generateLogFileName() { // such as "dsynergy_2020-20-10_15-20-42.log"
        final LocalDateTime dateTime = LocalDateTime.now();
        StringBuilder sb = new StringBuilder();
        sb.append("fo2_highlighter_");
        sb.append(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
        sb.append(".log");
        return sb.toString();
    }

    /**
     * Initializes the logger for the whole program
     *
     * @param debug do you want to generate log files or not
     */
    public static void init(boolean debug) { // adding console and log file to the logger
        ConsoleAppender conAppender = new ConsoleAppender(new TTCCLayout(), "System.err");
        MY_LOGGER.addAppender(conAppender);
        //----------------------------------------------------------------------
        if (debug) { // if debug it's gonna store messages in both console and the file otherwise just in the file
            FileAppender fileAppender = null;
            try {
                final String logFileName = generateLogFileName();
                fileAppender = new FileAppender(new TTCCLayout(), logFileName);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(FO2IELogger.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (fileAppender != null) {
                MY_LOGGER.addAppender(fileAppender);
            }
        }
    }

    // used when catchin exceptions
    // as they would cause application to crash
    // replacing NetBeans default logger ones
    public static void reportFatalError(String msg, Throwable t) {
        MY_LOGGER.fatal(msg, t);
    }

    public static void reportError(String msg, Throwable t) {
        MY_LOGGER.error(msg, t);
    }

    public static void reportWarning(String msg, Throwable t) {
        MY_LOGGER.warn(msg, t);
    }

    public static void reportInfo(String msg, Throwable t) {
        MY_LOGGER.info(msg, t);
    }

    public static Logger getMY_LOGGER() {
        return MY_LOGGER;
    }

}
