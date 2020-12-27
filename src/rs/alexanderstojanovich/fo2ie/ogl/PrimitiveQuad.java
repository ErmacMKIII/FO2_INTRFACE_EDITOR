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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.editor.GUI;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class PrimitiveQuad implements GLComponent {

    private int width;
    private int height;

    private Vector4f color = new Vector4f(Vector3fColors.WHITE, 1.0f);
    private float scale = 1.0f;

    private Vector2f pos = new Vector2f();
    private boolean enabled = true;

    private static final Vector2f[] VERTICES = new Vector2f[4];
    private final FloatBuffer fb = GLBuffers.newDirectFloatBuffer(4 * VERTEX_SIZE);

    private static final int[] INDICES = {0, 1, 2, 2, 3, 0};
    private static final IntBuffer CONST_INT_BUFFER = GLBuffers.newDirectIntBuffer(INDICES.length);
    private int vbo = 0;
    private static int ibo = 0;

    public static final int VERTEX_SIZE = 2;
    public static final int VERTEX_COUNT = 4;

    private boolean buffered = false;

    static {
        VERTICES[0] = new Vector2f(-1.0f, -1.0f);
        VERTICES[1] = new Vector2f(1.0f, -1.0f);
        VERTICES[2] = new Vector2f(1.0f, 1.0f);
        VERTICES[3] = new Vector2f(-1.0f, 1.0f);

        for (int i : INDICES) {
            CONST_INT_BUFFER.put(i);
        }
        CONST_INT_BUFFER.flip();
    }

    /**
     * Create new quad with resize factor
     *
     * @param width quad width
     * @param height quad height
     */
    public PrimitiveQuad(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Create new quad with resize factor
     *
     * @param width quad width
     * @param height quad height
     * @param pos position of the quad center
     */
    public PrimitiveQuad(int width, int height, Vector2f pos) {
        this.width = width;
        this.height = height;
        this.pos = pos;
    }

    /**
     * Buffer this quad with status green for rendering
     *
     * @param gl20 GL20 binding
     */
    @Override
    public void buffer(GL2 gl20) {
        fb.clear();
        for (int i = 0; i < VERTEX_COUNT; i++) {
            fb.put(VERTICES[i].x);
            fb.put(VERTICES[i].y);
        }
        fb.flip();
        if (vbo == 0) {
            IntBuffer intBuffer = GLBuffers.newDirectIntBuffer(1);
            gl20.glGenBuffers(1, intBuffer);
            vbo = intBuffer.get(0);
        }
        gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);

        gl20.glBufferData(GL2.GL_ARRAY_BUFFER, VERTEX_COUNT * VERTEX_SIZE * Float.BYTES, fb, GL2.GL_STATIC_DRAW);
        gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        //----------------------------------------------------------------------
        if (ibo == 0) {
            IntBuffer intBuffer = GLBuffers.newDirectIntBuffer(1);
            gl20.glGenBuffers(1, intBuffer);
            ibo = intBuffer.get(0);

            gl20.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo);

            gl20.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, INDICES.length * Integer.BYTES, CONST_INT_BUFFER, GL2.GL_STATIC_DRAW);
            gl20.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
        //----------------------------------------------------------------------
        buffered = true;
    }

    /**
     * Render image
     *
     * @param gl20 GL2 binding
     * @param program shader program for images
     */
    @Override
    public void render(GL2 gl20, ShaderProgram program) {
        if (enabled && buffered) {
            float relWidth = giveRelativeWidth();
            float relHeight = giveRelativeHeight();

            program.bind(gl20);
            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);
            gl20.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo);

            gl20.glEnableVertexAttribArray(0);
            gl20.glVertexAttribPointer(0, 2, GL2.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0); // this is for font pos
            program.bindAttribute(gl20, 0, "pos");

            program.updateUniform(gl20, pos, "trans");
            program.updateUniform(gl20, relWidth, "width");
            program.updateUniform(gl20, relHeight, "height");
            program.updateUniform(gl20, scale, "scale");
            program.updateUniform(gl20, color, "color");
            gl20.glDrawElements(GL2.GL_TRIANGLES, INDICES.length, GL2.GL_UNSIGNED_INT, 0);

            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
            gl20.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
            gl20.glDisableVertexAttribArray(0);

            ShaderProgram.unbind(gl20);
        }
    }

    @Override
    public Type getType() {
        return Type.PRIM;
    }

    public float giveRelativeWidth() {
        return width / (float) GUI.GL_CANVAS.getWidth();
    }

    public float giveRelativeHeight() {
        return height / (float) GUI.GL_CANVAS.getHeight();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector2f getPos() {
        return pos;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getVbo() {
        return vbo;
    }

    public static Vector2f[] getVERTICES() {
        return VERTICES;
    }

    @Override
    public boolean isBuffered() {
        return buffered;
    }

    public void setPos(Vector2f pos) {
        this.pos = pos;
    }

    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public static int getIbo() {
        return ibo;
    }

    public static void setIbo(int ibo) {
        PrimitiveQuad.ibo = ibo;
    }

}