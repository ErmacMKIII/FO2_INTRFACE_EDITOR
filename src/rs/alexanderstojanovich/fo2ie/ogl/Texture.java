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
package rs.alexanderstojanovich.fo2ie.ogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;
import rs.alexanderstojanovich.fo2ie.main.GUI;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Coa
 */
public class Texture {

    public static final Map<String, Texture> TEXTURE_MAP = new HashMap<>();

    private final String filename;
    private final BufferedImage image;
    private int textureID = 0;

    public static final int TEX_SIZE = Configuration.getInstance().getTextureSize();

    /**
     * Creates a texture based on the image
     *
     * @param filename filename to identify this texture
     * @param gl20 GL20 context
     * @param image provide image to make texture
     */
    public Texture(String filename, GL2 gl20, BufferedImage image) {
        this.filename = filename;
        this.image = image;
        loadToGraphicCard(gl20);
    }

    private void loadToGraphicCard(GL2 gl20) {
        IntBuffer intBuffer = GLBuffers.newDirectIntBuffer(1);
        gl20.glGenTextures(1, intBuffer);
        textureID = intBuffer.get(0);

        gl20.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
        // Set the texture wrapping parameters
        gl20.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);// Set texture wrapping to GL_REPEAT (usually basic wrapping method)
        gl20.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        // Set texture filtering parameters
        gl20.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl20.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

        // get the content as ByteBuffer
        ByteBuffer imageDataBuffer = getImageDataBuffer(image);

        // glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels)        
        gl20.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA8, TEX_SIZE, TEX_SIZE, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, imageDataBuffer);
        gl20.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    /**
     * Binds this texture as active for use
     *
     * @param gl20 GL2.0 binding
     */
    public void bind(GL2 gl20) {
        gl20.glActiveTexture(GL2.GL_TEXTURE0);
        gl20.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
    }

    /**
     * Binds this texture as active for use and specifying texture unit for it
     * (from 0 to 7)
     *
     * @param gl20 GL2.0 binding
     * @param textureUnitNum texture unit number
     * @param shaderProgram provided shader program
     * @param textureUniformName texture uniform name in the fragment shader
     */
    public void bind(GL2 gl20, int textureUnitNum, ShaderProgram shaderProgram, String textureUniformName) {
        if (textureUnitNum >= 0 && textureUnitNum <= 7) {
            gl20.glActiveTexture(GL2.GL_TEXTURE0 + textureUnitNum);
            gl20.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
            int uniformLocation = gl20.glGetUniformLocation(shaderProgram.getProgramId(), textureUniformName);
            gl20.glUniform1i(uniformLocation, textureUnitNum);
        }
    }

    public static void unbind(GL2 gl20) {
        gl20.glActiveTexture(GL2.GL_TEXTURE0);
        gl20.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    public static void unbind(GL2 gl20, int textureUnitNum) {
        if (textureUnitNum >= 0 && textureUnitNum <= 7) {
            gl20.glActiveTexture(GL2.GL_TEXTURE0 + textureUnitNum);
            gl20.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        }
    }

    /**
     * Gets content of this image as Byte Buffer (for textures)
     *
     * @param srcImg source image
     * @return content as byte buffer for creating texture
     */
    public static ByteBuffer getImageDataBuffer(BufferedImage srcImg) {
        ByteBuffer imageBuffer;
        WritableRaster raster;
        BufferedImage texImage;

        ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
                .getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8},
                true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

        raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                TEX_SIZE, TEX_SIZE, 4, null);
        texImage = new BufferedImage(glAlphaColorModel, raster, false,
                new Hashtable());

        int width = srcImg.getWidth();
        int height = srcImg.getHeight();
        double sx = 1.0 / (1.0 + (width - TEX_SIZE) / (double) TEX_SIZE);
        double sy = 1.0 / (1.0 + (height - TEX_SIZE) / (double) TEX_SIZE);

        AffineTransform xform = new AffineTransform();
        xform.scale(sx, sy);
        AffineTransformOp atOp = new AffineTransformOp(xform, null);
        final BufferedImage dstImg = atOp.filter(srcImg, null);

        // copy the source image into the produced image
        Graphics2D g2d = (Graphics2D) texImage.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        g2d.drawImage(dstImg, 0, 0, null);

        // blue color removal
        for (int px = 0; px < texImage.getWidth(); px++) {
            for (int py = 0; py < texImage.getHeight(); py++) {
                Color pixCol = new Color(texImage.getRGB(px, py), true);
                if (pixCol.equals(Color.BLUE)) {
                    texImage.setRGB(px, py, 0);
                }
            }
        }

        // build a byte buffer from the temporary image
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
                .getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        return imageBuffer;
    }

    /**
     * Loads Texture from the resource folder
     *
     * @param gl20
     * @param filename
     * @return loaded Texture
     */
    public static Texture loadLocalTexture(GL2 gl20, String filename) {
        if (TEXTURE_MAP.containsKey(filename)) {
            return TEXTURE_MAP.get(filename);
        } else {
            InputStream is = Texture.class.getResourceAsStream(GUI.RESOURCES_DIR + filename);
            if (is != null) {
                try {
                    BufferedImage rdImg = ImageIO.read(is);
                    Texture tex = new Texture(filename, gl20, rdImg);
                    TEXTURE_MAP.put(tex.filename, tex);
                    return tex;
                } catch (IOException ex) {
                    FO2IELogger.reportError("Error while loading image " + filename + "!", null);
                    FO2IELogger.reportError(ex.getMessage(), ex);
                }
            } else {
                FO2IELogger.reportError("Cannot load texture " + filename + "!", null);
            }

        }

        return null;
    }

    /**
     * Loads texture from the filesystem
     *
     * @param gl20 provided GL2.0 binding
     * @param file image file {PNG, BMP or JPG}
     * @return loaded Texture
     */
    public static Texture loadTexture(GL2 gl20, File file) {
        if (TEXTURE_MAP.containsKey(file.getName())) {
            return TEXTURE_MAP.get(file.getName());
        } else {
            if (file.exists()) {
                try {
                    InputStream is = new FileInputStream(file);
                    BufferedImage rdImg = ImageIO.read(is);
                    Texture tex = new Texture(file.getName(), gl20, rdImg);
                    TEXTURE_MAP.put(tex.filename, tex);
                    return tex;
                } catch (IOException ex) {
                    FO2IELogger.reportError("Error while loading image " + file.getName() + "!", null);
                    FO2IELogger.reportError(ex.getMessage(), ex);
                }
            } else {
                FO2IELogger.reportError("Cannot load texture " + file.getName() + "!", null);
            }
        }
        return null;
    }

    /**
     * Loads texture from the filesystem
     *
     * @param filename name for this texture (or from this image)
     * @param gl20 provided GL2.0 binding
     * @param image provided image for texture
     * @return loaded Texture
     */
    public static Texture loadTexture(String filename, GL2 gl20, BufferedImage image) {
        if (TEXTURE_MAP.containsKey(filename)) {
            return TEXTURE_MAP.get(filename);
        } else {
            return new Texture(filename, gl20, image);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.filename);
        hash = 53 * hash + Objects.hashCode(this.image);
        hash = 53 * hash + this.textureID;
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
        final Texture other = (Texture) obj;
        if (this.textureID != other.textureID) {
            return false;
        }
        if (!Objects.equals(this.filename, other.filename)) {
            return false;
        }
        if (!Objects.equals(this.image, other.image)) {
            return false;
        }
        return true;
    }

    /**
     * Enable globally all textures
     *
     * @param gl20 GL2.0 binding
     */
    public static void enable(GL2 gl20) {
        gl20.glEnable(GL2.GL_TEXTURE_2D);
    }

    /**
     * Disable globally all textures
     *
     * @param gl20 GL2.0 binding
     */
    public static void disable(GL2 gl20) {
        gl20.glDisable(GL2.GL_TEXTURE_2D);
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getTextureID() {
        return textureID;
    }

    public String getFilename() {
        return filename;
    }

}
