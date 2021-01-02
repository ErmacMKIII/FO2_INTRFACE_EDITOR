/*
 * Copyright (C) 2021 Alexander Stojanovich <coas91@rocketmail.com>
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class FOFRM {

    private int fps;
    private int imageCount;
    private BufferedImage[] images;
    private int offsetX;
    private int offsetY;

    public static enum Mode {
        STD_RD, IMGS_RD
    }

    private Mode mode = Mode.STD_RD;

    /**
     * Create FOFRM by reading it from the file
     *
     * @param fofrm fofrm plaintext file
     */
    public FOFRM(File fofrm) {
        read(fofrm);
    }

    /**
     * Create FOFRM
     *
     * @param fps frames per second
     * @param imageCount image count
     * @param images images (must be equal to image array length)
     * @param offsetX
     * @param offsetY
     */
    public FOFRM(int fps, int imageCount, BufferedImage[] images, int offsetX, int offsetY) {
        this.fps = fps;
        this.imageCount = imageCount;
        this.images = images;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    private void read(File file) {
        if (!file.exists()) {
            return;
        }
        int index = 0;
        mode = Mode.STD_RD;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("[dir_0]")) {
                    mode = Mode.IMGS_RD;
                } else {
                    String[] words = line.trim().split("=");
                    if (words.length == 2) {
                        switch (mode) {
                            case STD_RD:
                                switch (words[0]) {
                                    case "fps":
                                        fps = Integer.parseInt(words[1]);
                                        break;
                                    case "count":
                                        imageCount = Integer.parseInt(words[1]);
                                        images = new BufferedImage[imageCount];
                                        break;
                                    case "offs_x":
                                        offsetX = Integer.parseInt(words[1]);
                                        break;
                                    case "offs_y":
                                        offsetY = Integer.parseInt(words[1]);
                                        break;
                                }
                                break;
                            case IMGS_RD:
                                if (words[0].toLowerCase().equalsIgnoreCase("frm_" + index)) {
                                    File imgFile = new File(File.separator + file.getParentFile().getPath() + File.separator + words[1]);
                                    images[index++] = ImageIO.read(imgFile);
                                }
                                break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        }
    }

    /**
     * Write to plaintext file
     *
     * @param dir directory name
     * @param file to write plaintext FOFRM content to
     */
    public void write(String dir, File file) {
        if (file.exists()) {
            file.delete();
        }
        File dirFile = new File(file.getParent() + File.separator + dir);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
            pw.println("fps = " + fps);
            pw.println("fps = " + imageCount);
            pw.println();
            pw.println("offs_x = " + offsetX);
            pw.println("offs_y = " + offsetY);
            pw.println();
            pw.println("[dir_0]");
            for (int i = 0; i < imageCount; i++) {
                pw.println("frm_" + i + "=" + dir + "\\" + (i + 1) + ".png");
                ImageIO.write(images[i], "png", new File(String.valueOf(i + 1) + ".png"));
            }
        } catch (FileNotFoundException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    public int getFps() {
        return fps;
    }

    public int getImageCount() {
        return imageCount;
    }

    public BufferedImage[] getImages() {
        return images;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public Mode getMode() {
        return mode;
    }

}
