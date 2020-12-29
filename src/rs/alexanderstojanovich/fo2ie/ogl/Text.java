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
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.editor.GUI;
import rs.alexanderstojanovich.fo2ie.util.Pair;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Text implements GLComponent {

    public static final float ALIGNMENT_LEFT = 0.0f;
    public static final float ALIGNMENT_CENTER = 0.5f;
    public static final float ALIGNMENT_RIGHT = 1.0f;

    protected Texture texture;
    protected String content;

    protected static final int GRID_SIZE = 16;
    protected static final float CELL_SIZE = 1.0f / GRID_SIZE;
    public static final float LINE_SPACING = 1.5f;

    private final List<Quad> quadList = new ArrayList<>();
    private final List<Pair<Float, Float>> pairList = new ArrayList<>();

    protected boolean enabled;

    public static final int STD_FONT_WIDTH = 24;
    public static final int STD_FONT_HEIGHT = 24;

    protected float alignment = ALIGNMENT_LEFT; // per character alignment

    protected boolean buffered = false;

    protected Vector2f pos = new Vector2f();
    protected float scale = 1.0f;
    protected Vector4f color = new Vector4f(Vector3fColors.WHITE, 1.0f);

    protected int charWidth = STD_FONT_WIDTH;
    protected int charHeight = STD_FONT_HEIGHT;

    protected boolean ignoreFactor = false;

    public Text(Texture texture, String content) {
        this.texture = texture;
        this.content = content;
        this.enabled = true;
    }

    public Text(Texture texture, String content, Vector4f color, Vector2f pos) {
        this.texture = texture;
        this.content = content;
        this.color = color;
        this.pos = pos;
        this.enabled = true;
    }

    public Text(Texture texture, String content, Vector2f pos, int charWidth, int charHeight) {
        this.texture = texture;
        this.content = content;
        this.enabled = true;
        this.charWidth = charWidth;
        this.charHeight = charHeight;
    }

    @Override
    public void unbuffer() {
        buffered = false;
    }

    private void init(GL2 gl20) {
        quadList.clear();
        pairList.clear();
        String[] lines = content.split("\n");
        for (int l = 0; l < lines.length; l++) {
            for (int i = 0; i < lines[l].length(); i++) {
                int j = i % 64;
                int k = i / 64;
                int asciiCode = (int) (lines[l].charAt(i));

                float cellU = (asciiCode % GRID_SIZE) * CELL_SIZE;
                float cellV = (asciiCode / GRID_SIZE) * CELL_SIZE;

                float xinc = (j - content.length() * alignment) * giveRelativeCharWidth();
                float ydec = (k + l * LINE_SPACING) * giveRelativeCharHeight();

                pairList.add(new Pair<>(xinc, ydec));

                Quad quad = new Quad(charWidth, charHeight, texture);
                quad.setColor(color);
                quad.setPos(pos);
                quad.setScale(scale);

                quad.getUvs()[0].x = cellU;
                quad.getUvs()[0].y = cellV + CELL_SIZE;

                quad.getUvs()[1].x = cellU + CELL_SIZE;
                quad.getUvs()[1].y = cellV + CELL_SIZE;

                quad.getUvs()[2].x = cellU + CELL_SIZE;
                quad.getUvs()[2].y = cellV;

                quad.getUvs()[3].x = cellU;
                quad.getUvs()[3].y = cellV;

                quad.buffer(gl20);

                quadList.add(quad);
            }
        }
    }

    /**
     * Buffer this text with green status for rendering
     *
     * @param gl20 GL2 binding
     */
    @Override
    public void buffer(GL2 gl20) {
        init(gl20);
        buffered = true;
    }

    /**
     * Render this text with given shader program
     *
     * @param gl20 GL2 binding
     * @param program shader program for binding
     */
    @Override
    public void render(GL2 gl20, Matrix4f projMat4, ShaderProgram program) {
        if (enabled && buffered) {
            int index = 0;
            for (Quad quad : quadList) {
                Pair<Float, Float> pair = pairList.get(index);
                float xinc = pair.getKey();
                float ydec = pair.getValue();
                quad.render(gl20, xinc, ydec, projMat4, program);
                index++;
            }
        }
    }

    @Override
    public int getWidth() {
        return charWidth;
    }

    @Override
    public int getHeight() {
        return charHeight;
    }

    public float getRelativeWidth() {
        return content.length() * getCharWidth();
    }

    public float giveRelativeCharWidth() {
        return scale * charWidth / GUI.GL_CANVAS.getWidth();
    }

    public float giveRelativeCharHeight() {
        return scale * charHeight / GUI.GL_CANVAS.getHeight();
    }

    /**
     * It aligns position to next char position (useful if characters are cut
     * out or so) Notice - call this method only once!
     */
    public void alignToNextChar() {
        float srw = scale * giveRelativeCharWidth(); // scaled relative width
        float srh = scale * giveRelativeCharHeight(); // scaled relative height                                                                 

        float xrem = pos.x % srw;
        pos.x -= (pos.x < 0.0f) ? xrem : (xrem - srw);

        float yrem = pos.y % srh;
        pos.y -= yrem;
    }

    @Override
    public Type getType() {
        return Type.TXT;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        buffered = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Quad> getQuadList() {
        return quadList;
    }

    public List<Pair<Float, Float>> getPairList() {
        return pairList;
    }

    public float getAlignment() {
        return alignment;
    }

    public void setAlignment(float alignment) {
        this.alignment = alignment;
    }

    @Override
    public boolean isBuffered() {
        return buffered;
    }

    public int getCharWidth() {
        return charWidth;
    }

    public int getCharHeight() {
        return charHeight;
    }

    public boolean isIgnoreFactor() {
        return ignoreFactor;
    }

    public void setIgnoreFactor(boolean ignoreFactor) {
        this.ignoreFactor = ignoreFactor;
    }

    public Vector2f getPos() {
        return pos;
    }

    public float getScale() {
        return scale;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public void setPos(Vector2f pos) {
        this.pos = pos;
        buffered = false;
    }

    public void setScale(float scale) {
        this.scale = scale;
        buffered = false;
    }

}
