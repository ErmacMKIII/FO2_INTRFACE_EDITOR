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
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;

/**
 *
 * @author Coa
 */
public class Texture {

    private final BufferedImage image;
    private int textureID = 0;

    /**
     * Creates a texture based on the image
     *
     * @param gl20 GL20 context
     * @param image provide image to make texture
     */
    public Texture(GL2 gl20, BufferedImage image) {
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
        gl20.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, imageDataBuffer);
        gl20.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    public void bind(GL2 gl20) {
        gl20.glActiveTexture(GL2.GL_TEXTURE0);
        gl20.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
    }

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

    public static ByteBuffer getImageDataBuffer(BufferedImage bufferedImage) {
        ByteBuffer imageBuffer;
        WritableRaster raster;
        BufferedImage texImage;

        ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
                .getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8},
                true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

        raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
        texImage = new BufferedImage(glAlphaColorModel, raster, true,
                new Hashtable());
        // copy the source image into the produced image
        Graphics2D g2d = (Graphics2D) texImage.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        g2d.drawImage(bufferedImage, 0, 0, null);

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

    public static void enable(GL2 gl20) {
        gl20.glEnable(GL2.GL_TEXTURE_2D);
    }

    public static void disable(GL2 gl20) {
        gl20.glDisable(GL2.GL_TEXTURE_2D);
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getTextureID() {
        return textureID;
    }

}
