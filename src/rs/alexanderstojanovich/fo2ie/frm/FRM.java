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
package rs.alexanderstojanovich.fo2ie.frm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class FRM {

    private int version; //  2-byte unsigned (0x0000)
    private int fps; // 2-byte unsigned (0x0004)
    private int actionFrame; // 2-byte unsigned (0x0006)
    private int framesPerDirection; // 2-byte unsigned (0x0008)

    private int[] shiftX = new int[6]; // signed 
    private int[] shiftY = new int[6]; // signed   

    private final int[] offset = new int[6]; // unsigned

    // image composed of frames (but frame 0 is primarily used)
    private final List<ImageData> frames = new ArrayList<>();

    private int frameSize;

    // 16 MB Buffer
    private final byte buffer[] = new byte[0x1000000];
    private int pos = 0x0000;

    /**
     * Create FRM by reading it from the file
     *
     * @param frm frm binary file
     */
    public FRM(File frm) {
        read(frm);
    }

    /**
     * Create FRM with given attributes and series of images (short)
     *
     * @param fps frames per second rate of the animation
     * @param images array of images
     * @param offsetX offset array of X direction for image array
     * @param offsetY offset array of Y direction for image array
     */
    public FRM(int fps, BufferedImage[] images, int offsetX, int offsetY) {
        this.version = 0x04;
        this.fps = fps;
        this.actionFrame = 0x00;
        this.frameSize = 0;
        int direction = 0;
        int index = 0;
        for (BufferedImage image : images) {
            if (index == direction * framesPerDirection) {
                this.offset[direction++] = frameSize;
            }
            frameSize += image.getWidth() * image.getHeight() + 12;
            ImageData imgData = new ImageData(image, offsetX, offsetY);
            frames.add(imgData);
            index++;
        }
    }

    /**
     * Create FRM with given attributes and series of images (long)
     *
     * @param version version number of the FRM file format
     * @param fps frames per second rate of the animation
     * @param actionFrame frame of the animation on which actions occur (shot,
     * open doors, etc.)
     * @param framesPerDirection number of frames for a particular orientation
     * @param shiftX required X shift array
     * @param shiftY required Y shift array
     * @param offset frame offset array (lesser important)
     * @param images array of images
     * @param offsetsX offset array of X direction for image array
     * @param offsetsY offset array of Y direction for image array
     */
    public FRM(int version, int fps, int actionFrame, int framesPerDirection,
            int[] shiftX, int[] shiftY, int[] offset, BufferedImage[] images, int[] offsetsX, int[] offsetsY) {
        this.version = version;
        this.fps = fps;
        this.actionFrame = actionFrame;
        this.framesPerDirection = framesPerDirection;
        this.frameSize = 0;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        int direction = 0;
        int index = 0;
        for (BufferedImage image : images) {
            if (index == direction * framesPerDirection) {
                this.offset[direction++] = frameSize;
            }
            frameSize += image.getWidth() * image.getHeight() + 12;
            ImageData imgData = new ImageData(image, offsetsX[index], offsetsY[index]);
            frames.add(imgData);
            index++;
        }
    }

    private void loadFromFile(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(buffer);
        } catch (FileNotFoundException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    FO2IELogger.reportError(ex.getMessage(), ex);
                }
            }
        }
    }

    private void storeToFile(File file) {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(buffer, 0, pos);
        } catch (FileNotFoundException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    FO2IELogger.reportError(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Read binary data from the specified FRM file
     *
     * @param file is specified FRM file to read from
     */
    private void read(File file) {
        Arrays.fill(buffer, (byte) 0x00);
        frames.clear();
        if (file.exists()) {
            loadFromFile(file);
        } else {
            return;
        }
        //----------------------------------------------------------------------
        pos = 0x0000;
        // big endian motorola
        version = ((buffer[pos] & 0xFF) << 24) | ((buffer[pos + 1] << 16) & 0xFF) | ((buffer[pos + 2] << 8) & 0xFF) | ((buffer[pos + 3]) & 0xFF);
        pos += 4;
        fps = ((buffer[pos] & 0xFF) << 8) | (buffer[pos + 1] & 0xFF);
        pos += 2;
        actionFrame = ((buffer[pos] & 0xFF) << 8) | (buffer[pos + 1] & 0xFF);
        pos += 2;
        framesPerDirection = ((buffer[pos] & 0xFF) << 8) | (buffer[pos + 1] & 0xFF);
        pos += 2;
        //----------------------------------------------------------------------                
        for (int i = 0; i < 6; i++) {
            shiftX[i] = (buffer[pos] << 8) | buffer[pos + 1];
            pos += 2;
        }

        for (int i = 0; i < 6; i++) {
            shiftY[i] = (buffer[pos] << 8) | buffer[pos + 1];
            pos += 2;
        }
        //----------------------------------------------------------------------        
        for (int i = 0; i < 6; i++) {
            offset[i] = ((buffer[pos] & 0xFF) << 24) | ((buffer[pos + 1] & 0xFF) << 16) | ((buffer[pos + 2] & 0xFF) << 8) | (buffer[pos + 3] & 0xFF);
            pos += 4;
        }
        //----------------------------------------------------------------------
        frameSize = ((buffer[pos] & 0xFF) << 24) | ((buffer[pos + 1] & 0xFF) << 16) | ((buffer[pos + 2] & 0xFF) << 8) | (buffer[pos + 3] & 0xFF);
        pos += 4;
        //----------------------------------------------------------------------
        int total = 0;
        while (total < frameSize) {
            for (int j = 0; j < framesPerDirection; j++) {
                final int width = ((buffer[pos] & 0xFF) << 8) | (buffer[pos + 1] & 0xFF);
                pos += 2;
                final int height = ((buffer[pos] & 0xFF) << 8) | (buffer[pos + 1] & 0xFF);
                pos += 2;
                //--------------------------------------------------------------
                pos += 4;
                //--------------------------------------------------------------
                final int offsetX = ((buffer[pos] & 0xFF) << 8) | (buffer[pos + 1] & 0xFF);
                pos += 2;
                final int offsetY = ((buffer[pos] & 0xFF) << 8) | (buffer[pos + 1] & 0xFF);
                pos += 2;
                //--------------------------------------------------------------
                ImageData imgData = new ImageData(width, height, offsetX, offsetY);
                for (int py = 0; py < imgData.getHeight(); py++) {
                    for (int px = 0; px < imgData.getWidth(); px++) {
                        byte index = buffer[pos++];
                        imgData.setPixel(px, py, index);
                    }
                }
                frames.add(imgData);
                total += 12 + width * height;
            }
        }
    }

    /**
     * Write to binary file
     *
     * @param file to write binary FRM content to
     */
    public void write(File file) {
        pos = 0x0000;
        // big endian motorola
        buffer[pos] = (byte) (version >> 24);
        buffer[pos + 1] = (byte) (version >> 16);
        buffer[pos + 2] = (byte) (version >> 8);
        buffer[pos + 3] = (byte) (version);
        pos += 4;
        buffer[pos] = (byte) (fps >> 8);
        buffer[pos + 1] = (byte) (fps);
        pos += 2;
        buffer[pos] = (byte) (actionFrame >> 8);
        buffer[pos + 1] = (byte) (actionFrame);
        pos += 2;
        buffer[pos] = (byte) (framesPerDirection >> 8);
        buffer[pos + 1] = (byte) (framesPerDirection);
        pos += 2;
        //----------------------------------------------------------------------                
        for (int i = 0; i < 6; i++) {
            buffer[pos] = (byte) (shiftX[i] >> 8);
            buffer[pos + 1] = (byte) (shiftX[i]);
            pos += 2;
        }

        for (int i = 0; i < 6; i++) {
            buffer[pos] = (byte) (shiftY[i] >> 8);
            buffer[pos + 1] = (byte) (shiftY[i]);
            pos += 2;
        }
        //----------------------------------------------------------------------        
        for (int i = 0; i < 6; i++) {
            buffer[pos] = (byte) (offset[i] >> 24);
            buffer[pos + 1] = (byte) (offset[i] >> 16);
            buffer[pos + 2] = (byte) (offset[i] >> 8);
            buffer[pos + 3] = (byte) (offset[i]);
            pos += 4;
        }
        //----------------------------------------------------------------------
        buffer[pos] = (byte) (frameSize >> 24);
        buffer[pos + 1] = (byte) (frameSize >> 16);
        buffer[pos + 2] = (byte) (frameSize >> 8);
        buffer[pos + 3] = (byte) (frameSize);
        pos += 4;
        //----------------------------------------------------------------------
        for (ImageData frame : frames) {
            int width = frame.getWidth();
            buffer[pos] = (byte) (width >> 8);
            buffer[pos + 1] = (byte) (width);
            pos += 2;
            int height = frame.getHeight();
            buffer[pos] = (byte) (height >> 8);
            buffer[pos + 1] = (byte) (height);
            pos += 2;
            //--------------------------------------------------------------
            final int area = frame.getWidth() * frame.getHeight();
            buffer[pos] = (byte) (area >> 24);
            buffer[pos + 1] = (byte) (area >> 16);
            buffer[pos + 2] = (byte) (area >> 8);
            buffer[pos + 3] = (byte) (area);
            pos += 4;
            final int offsetX = frame.getOffsetX();
            buffer[pos] = (byte) (offsetX >> 8);
            buffer[pos + 1] = (byte) (offsetX);
            pos += 2;
            final int offsetY = frame.getOffsetY();
            buffer[pos] = (byte) (offsetY >> 8);
            buffer[pos + 1] = (byte) (offsetY);
            pos += 2;
            //--------------------------------------------------------------                
            for (int py = 0; py < frame.getHeight(); py++) {
                for (int px = 0; px < frame.getWidth(); px++) {
                    buffer[pos++] = frame.getPixel(px, py);
                }
            }
        }

        storeToFile(file);
    }

    public int getVersion() {
        return version;
    }

    public int getFps() {
        return fps;
    }

    public int getActionFrame() {
        return actionFrame;
    }

    public int getFramesPerDirection() {
        return framesPerDirection;
    }

    public int[] getShiftX() {
        return shiftX;
    }

    public int[] getShiftY() {
        return shiftY;
    }

    public int[] getOffset() {
        return offset;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public List<ImageData> getFrames() {
        return frames;
    }

}
