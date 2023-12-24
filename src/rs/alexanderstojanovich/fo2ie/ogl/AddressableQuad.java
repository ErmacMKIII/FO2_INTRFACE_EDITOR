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
import java.util.Objects;
import java.util.UUID;
import org.joml.Matrix4f;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;
import rs.alexanderstojanovich.fo2ie.main.GUI;
import rs.alexanderstojanovich.fo2ie.main.GameTime;
import rs.alexanderstojanovich.fo2ie.util.GLColor;
import rs.alexanderstojanovich.fo2ie.util.GLCoords;
import rs.alexanderstojanovich.fo2ie.util.UniqueIdUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class AddressableQuad implements GLComponent {

    private final FeatureKey featureKey;
    private final Inheritance inheritance;
    private final Type type = Type.ADDR;

    private int width;
    private int height;
    private Texture texture;

    private Vector4f color = new Vector4f(Vector3fColors.WHITE, 1.0f);
    private float scale = 1.0f;

    private final Configuration config = Configuration.getInstance();
    private Vector4f outlineColor = new Vector4f(GLColor.awtColorToVec4(config.getSelectCol()));

    private Vector2f pos = new Vector2f();
    private boolean enabled = true;

    private static final Vector2f[] VERTICES = new Vector2f[4];
    private final FloatBuffer fb = GLBuffers.newDirectFloatBuffer(4 * VERTEX_SIZE);

    private final Vector2f[] uvs = new Vector2f[4];
    private static final int[] INDICES = {0, 1, 2, 2, 3, 0};
    private static final IntBuffer CONST_INT_BUFFER = GLBuffers.newDirectIntBuffer(INDICES.length);
    private int vbo = 0;
    private static int ibo = 0;

    public static final int VERTEX_SIZE = 4;
    public static final int VERTEX_COUNT = 4;

    private boolean buffered = false;

    private final GameTime gameTime = GameTime.getInstance();

    private final String uniqueId;

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
    private final float stepX;
    private final float stepY;
    private final Vector2f posMax;

    /**
     * Create addressable new quad with resize factor
     *
     * @param featureKey bound feature key
     * @param inheritance {BASE = ALL_RES, DERIVED = TARGET_RES}
     * @param width quad width
     * @param height quad height
     * @param texture parsed texture
     * @param pos starting position
     * @param stepX step X-coord for position
     * @param stepY step Y-coord for positon
     * @param posMax coordinates of max position (the furthest)
     */
    public AddressableQuad(FeatureKey featureKey, Inheritance inheritance, int width, int height, Texture texture, Vector2f pos, float stepX, float stepY, Vector2f posMax) {
        this.featureKey = featureKey;
        this.inheritance = inheritance;
        this.width = width;
        this.height = height;
        this.uniqueId = UniqueIdUtils.GenerateNewUniqueId(featureKey, type, inheritance);
        this.texture = texture;
        this.pos = pos;
        this.stepX = stepX;
        this.stepY = stepY;
        this.posMax = posMax;
        initUVs();
    }

    private Vector2f nextPos() {
        Vector2f res = new Vector2f(pos);

        final int countX = (stepX == 0) ? 0 : Math.round(Math.abs(posMax.x - pos.x) / stepX);
        final int countY = (stepY == 0) ? 0 : Math.round(Math.abs(posMax.y - pos.y) / stepY);

        double q = (GameTime.TPS - gameTime.getGameTicks()) / (double) GameTime.TPS;
        int i = (countX == 0) ? 0 : (int) Math.floorMod(Math.round((1.0 - q) * countX), countX);
        int j = (countY == 0) ? 0 : (int) Math.floorMod(Math.round((1.0 - q) * countY), countY);

        res.x = pos.x + i * stepX;
        res.y = pos.y + j * stepY;

        return res;
    }

    private void initUVs() {
        uvs[0] = new Vector2f(0.0f, 1.0f); // (-1.0f, -1.0f)
        uvs[1] = new Vector2f(1.0f, 1.0f); // (1.0f, -1.0f)
        uvs[2] = new Vector2f(1.0f, 0.0f); // (1.0f, 1.0f)
        uvs[3] = new Vector2f(0.0f, 0.0f); // (-1.0f, 1.0f)
    }

    @Override
    public void unbuffer() {
        buffered = false;
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
            fb.put(uvs[i].x);
            fb.put(uvs[i].y);
        }
        fb.flip();
        if (vbo == 0) {
            IntBuffer intBuffer = GLBuffers.newDirectIntBuffer(1);
            gl20.glGenBuffers(1, intBuffer);
            vbo = intBuffer.get(0);

            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);

            gl20.glBufferData(GL2.GL_ARRAY_BUFFER, VERTEX_COUNT * VERTEX_SIZE * Float.BYTES, fb, GL2.GL_STATIC_DRAW);
            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        } else {
            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);

            gl20.glBufferSubData(GL2.GL_ARRAY_BUFFER, VERTEX_COUNT * VERTEX_SIZE * Float.BYTES, 0, fb);
            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        }
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

    private Matrix4f calcModelMatrix(Vector2f pos) {
        Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, GUI.GL_CANVAS.getWidth(), GUI.GL_CANVAS.getHeight());
        Matrix4f translationMatrix = new Matrix4f().setTranslation(posGL.x, posGL.y, 0.0f);
        Matrix4f rotationMatrix = new Matrix4f().identity();

        float sx = giveRelativeWidth();
        float sy = giveRelativeHeight();
        Matrix4f scaleMatrix = new Matrix4f().scaleXY(sx, sy);

        Matrix4f temp = new Matrix4f();
        Matrix4f modelMatrix = translationMatrix.mul(rotationMatrix.mul(scaleMatrix, temp), temp);
        return modelMatrix;
    }

    /**
     * Render image
     *
     * @param gl20 GL2 binding
     * @param projMat4 projection matrix
     * @param program shader program for images
     */
    @Override
    public void render(GL2 gl20, Matrix4f projMat4, ShaderProgram program) {
        if (enabled && buffered) {
            program.bind(gl20);
            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);
            gl20.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo);

            gl20.glEnableVertexAttribArray(0);
            gl20.glEnableVertexAttribArray(1);
            gl20.glVertexAttribPointer(0, 2, GL2.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0); // this is for font pos
            gl20.glVertexAttribPointer(1, 2, GL2.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 8); // this is for font uv                                     
            program.bindAttribute(gl20, 0, "pos");
            program.bindAttribute(gl20, 1, "uv");

            Matrix4f modelMat4 = calcModelMatrix(nextPos());
            program.updateUniform(gl20, projMat4, "projectionMatrix");
            program.updateUniform(gl20, modelMat4, "modelMatrix");
            program.updateUniform(gl20, color, "color");
            texture.bind(gl20, 0, program, "colorMap");
            gl20.glDrawElements(GL2.GL_TRIANGLES, INDICES.length, GL2.GL_UNSIGNED_INT, 0);

            Texture.unbind(gl20, 0);

            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
            gl20.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
            gl20.glDisableVertexAttribArray(0);
            gl20.glDisableVertexAttribArray(1);

            ShaderProgram.unbind(gl20);
        }
    }

    private Matrix4f calcModelMatrix(Vector2f pos, float xinc, float ydec) {
        Matrix4f translationMatrix = new Matrix4f().setTranslation(pos.x + xinc, pos.y + ydec, 0.0f);
        Matrix4f rotationMatrix = new Matrix4f().identity();

        float sx = giveRelativeWidth();
        float sy = giveRelativeHeight();
        Matrix4f scaleMatrix = new Matrix4f().scaleXY(sx, sy);

        Matrix4f temp = new Matrix4f();
        Matrix4f modelMatrix = translationMatrix.mul(rotationMatrix.mul(scaleMatrix, temp), temp);
        return modelMatrix;
    }

    /**
     * Render font
     *
     * @param gl20 GL2 binding
     * @param xinc x-advance
     * @param ydec y-drop (for multi-line text)
     * @param projMat4 projection matrix
     * @param fntProgram shader program for fonts
     */
    public void render(GL2 gl20, float xinc, float ydec, Matrix4f projMat4, ShaderProgram fntProgram) { // used for fonts
        if (enabled && buffered) {
            fntProgram.bind(gl20);
            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);
            gl20.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo);

            gl20.glEnableVertexAttribArray(0);
            gl20.glEnableVertexAttribArray(1);
            gl20.glVertexAttribPointer(0, 2, GL2.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0); // this is for font pos
            gl20.glVertexAttribPointer(1, 2, GL2.GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 8); // this is for font uv                                     
            fntProgram.bindAttribute(gl20, 0, "pos");
            fntProgram.bindAttribute(gl20, 1, "uv");

            Matrix4f modelMat4 = calcModelMatrix(nextPos(), xinc, ydec);
            fntProgram.updateUniform(gl20, projMat4, "projectionMatrix");
            fntProgram.updateUniform(gl20, modelMat4, "modelMatrix");
            fntProgram.updateUniform(gl20, color, "color");
            texture.bind(gl20, 0, fntProgram, "colorMap");
            gl20.glDrawElements(GL2.GL_TRIANGLES, INDICES.length, GL2.GL_UNSIGNED_INT, 0);

            Texture.unbind(gl20, 0);

            gl20.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
            gl20.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
            gl20.glDisableVertexAttribArray(0);
            gl20.glDisableVertexAttribArray(1);

            ShaderProgram.unbind(gl20);
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    public float giveRelativeWidth() {
        return scale * width / GUI.GL_CANVAS.getWidth();
    }

    public float giveRelativeHeight() {
        return scale * height / GUI.GL_CANVAS.getHeight();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.type);
        hash = 41 * hash + this.width;
        hash = 41 * hash + this.height;
        hash = 41 * hash + Objects.hashCode(this.texture);
        hash = 41 * hash + Objects.hashCode(this.color);
        hash = 41 * hash + Float.floatToIntBits(this.scale);
        hash = 41 * hash + Objects.hashCode(this.pos);
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
        final AddressableQuad other = (AddressableQuad) obj;
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (Float.floatToIntBits(this.scale) != Float.floatToIntBits(other.scale)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.texture, other.texture)) {
            return false;
        }
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        if (!Objects.equals(this.pos, other.pos)) {
            return false;
        }
        return true;
    }

    @Override
    public float getRelativeWidth() {
        return scale * width / (float) GUI.GL_CANVAS.getWidth();
    }

    @Override
    public float getRelativeHeight() {
        return scale * height / (float) GUI.GL_CANVAS.getHeight();
    }

    @Override
    public Rectanglef getGLArea() {
        Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, GUI.GL_CANVAS.getWidth(), GUI.GL_CANVAS.getHeight());
        float rw = getRelativeWidth();
        float rh = getRelativeHeight();
        Rectanglef rect = new Rectanglef(posGL.x - rw, posGL.y - rh, posGL.x + rw, posGL.y + rh);
        return rect;
    }

    @Override
    public Rectanglef getPixelArea() {
        Rectanglef rect = new Rectanglef(pos.x - width / 2.0f, pos.y - height / 2.0f, pos.x + width / 2.0f, pos.y + height / 2.0f);
        return rect;
    }

    @Override
    public FeatureKey getFeatureKey() {
        return featureKey;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public Vector2f getPos() {
        return pos;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
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

    public Vector2f[] getUvs() {
        return uvs;
    }

    @Override
    public void setPos(Vector2f pos) {
        this.pos = pos;
    }

    @Override
    public Vector4f getColor() {
        return color;
    }

    @Override
    public void setColor(Vector4f color) {
        this.color = color;
    }

    public static int getIbo() {
        return ibo;
    }

    public static void setIbo(int ibo) {
        AddressableQuad.ibo = ibo;
    }

    public GameTime getGameTime() {
        return gameTime;
    }

    public float getStepX() {
        return stepX;
    }

    public float getStepY() {
        return stepY;
    }

    public Vector2f getPosMax() {
        return posMax;
    }

    @Override
    public Vector4f getOutlineColor() {
        return outlineColor;
    }

    @Override
    public void setOutlineColor(Vector4f outlineColor) {
        this.outlineColor = outlineColor;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public Inheritance getInheritance() {
        return inheritance;
    }

}
