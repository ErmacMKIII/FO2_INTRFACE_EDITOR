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
import java.util.Objects;
import org.joml.Matrix4f;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.main.GUI;
import rs.alexanderstojanovich.fo2ie.util.Pair;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Text implements GLComponent {

    private final FeatureKey featureKey;

    private final Type type = Type.TXT;

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

    protected final PrimitiveQuad overlay;

    /**
     * Constructs OpenGL text component
     *
     * @param featureKey bound feature key
     * @param texture bound texture
     * @param content display text
     */
    public Text(FeatureKey featureKey, Texture texture, String content) {
        this.featureKey = featureKey;
        this.texture = texture;
        this.content = content;
        this.overlay = new PrimitiveQuad(charWidth * content.length(), charHeight, pos);
        this.enabled = true;
    }

    /**
     * Constructs OpenGL text component
     *
     * @param featureKey bound feature key
     * @param texture bound texture
     * @param content display text
     * @param color text color
     * @param pos text pos
     */
    public Text(FeatureKey featureKey, Texture texture, String content, Vector4f color, Vector2f pos) {
        this.featureKey = featureKey;
        this.texture = texture;
        this.content = content;
        this.color = color;
        this.pos = pos;
        this.overlay = new PrimitiveQuad(charWidth * content.length(), charHeight, pos);
        this.enabled = true;
    }

    /**
     * Constructs OpenGL text component
     *
     * @param featureKey bound feature key
     * @param texture bound texture
     * @param content display text
     * @param pos text pos
     * @param charWidth char width in pixels
     * @param charHeight char height in pixels
     */
    public Text(FeatureKey featureKey, Texture texture, String content, Vector2f pos, int charWidth, int charHeight) {
        this.featureKey = featureKey;
        this.texture = texture;
        this.content = content;
        this.pos = pos;
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        this.overlay = new PrimitiveQuad(charWidth * content.length(), charHeight, pos);
        this.enabled = true;
    }

    @Override
    public void unbuffer() {
        this.overlay.unbuffer();
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

                float xinc = (j - content.length() * alignment) * getRelativeCharWidth();
                float ydec = (k + l * LINE_SPACING) * getRelativeCharHeight();

                pairList.add(new Pair<>(xinc, ydec));

                // pass null feat key as it is only used for rendering charactters
                Quad quad = new Quad(null, charWidth, charHeight, texture);

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
        overlay.buffer(gl20);
        buffered = true;
    }

    /**
     * Render this text with given shader program
     *
     * @param gl20 GL2 binding
     * @param projMat4 projection matrix
     * @param fntProgram font shader program
     */
    @Override
    public void render(GL2 gl20, Matrix4f projMat4, ShaderProgram fntProgram) {
        if (enabled && buffered) {
            int index = 0;
            for (Quad quad : quadList) {
                Pair<Float, Float> pair = pairList.get(index);
                float xinc = pair.getKey();
                float ydec = pair.getValue();
                quad.render(gl20, xinc, ydec, projMat4, fntProgram);
                index++;
            }
        }
    }

    /**
     * Render this text with given shader program
     *
     * @param gl20 GL2 binding
     * @param projMat4 projection matrix
     * @param fntProgram font shader program
     * @param prmProgram primitive (overlay) shader
     */
    public void render(GL2 gl20, Matrix4f projMat4, ShaderProgram fntProgram, ShaderProgram prmProgram) {
        if (overlay.isEnabled() && overlay.isBuffered()) {
            overlay.render(gl20, projMat4, prmProgram);
        }

        if (enabled && buffered) {
            int index = 0;
            for (Quad quad : quadList) {
                Pair<Float, Float> pair = pairList.get(index);
                float xinc = pair.getKey();
                float ydec = pair.getValue();
                quad.render(gl20, xinc, ydec, projMat4, fntProgram);
                index++;
            }
        }
    }

    @Override
    public int getWidth() {
        return charWidth * content.length();
    }

    @Override
    public int getHeight() {
        return charHeight;
    }

    @Override
    public float getRelativeWidth() {
        return content.length() * getRelativeCharWidth();
    }

    public float getRelativeCharWidth() {
        return scale * charWidth / (float) GUI.GL_CANVAS.getWidth();
    }

    public float getRelativeCharHeight() {
        return scale * charHeight / (float) GUI.GL_CANVAS.getHeight();
    }

    @Override
    public float getRelativeHeight() {
        return getRelativeCharHeight();
    }

    @Override
    public Rectanglef getArea() {
        float rw = getRelativeWidth();
        float rh = getRelativeHeight();
        Rectanglef rect = new Rectanglef(
                pos.x - rw,
                pos.y - rh,
                pos.x + rw,
                pos.y + rh
        );
        return rect;
    }

    /**
     * It aligns position to next char position (useful if characters are cut
     * out or so) Notice - call this method only once!
     */
    public void alignToNextChar() {
        float rcw = getRelativeCharWidth(); // scaled relative width
        float rch = getRelativeCharHeight(); // scaled relative height                                                                 

        float xrem = pos.x % rcw;
        pos.x -= (pos.x < 0.0f) ? xrem : (xrem - rcw);

        float yrem = pos.y % rch;
        pos.y -= yrem;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.type);
        hash = 37 * hash + Objects.hashCode(this.texture);
        hash = 37 * hash + Objects.hashCode(this.content);
        hash = 37 * hash + Float.floatToIntBits(this.alignment);
        hash = 37 * hash + Objects.hashCode(this.pos);
        hash = 37 * hash + Float.floatToIntBits(this.scale);
        hash = 37 * hash + Objects.hashCode(this.color);
        hash = 37 * hash + this.charWidth;
        hash = 37 * hash + this.charHeight;
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
        final Text other = (Text) obj;
        if (Float.floatToIntBits(this.alignment) != Float.floatToIntBits(other.alignment)) {
            return false;
        }
        if (Float.floatToIntBits(this.scale) != Float.floatToIntBits(other.scale)) {
            return false;
        }
        if (this.charWidth != other.charWidth) {
            return false;
        }
        if (this.charHeight != other.charHeight) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.texture, other.texture)) {
            return false;
        }
        if (!Objects.equals(this.pos, other.pos)) {
            return false;
        }
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        return true;
    }

    public PrimitiveQuad getOverlay() {
        return overlay;
    }

    @Override
    public FeatureKey getFeatureKey() {
        return featureKey;
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

    @Override
    public Vector2f getPos() {
        return pos;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public Vector4f getColor() {
        return color;
    }

    @Override
    public void setColor(Vector4f color) {
        this.color = color;
    }

    @Override
    public void setPos(Vector2f pos) {
        this.pos = pos;
        this.overlay.setPos(pos);
        buffered = false;
    }

    public void setScale(float scale) {
        this.scale = scale;
        buffered = false;
    }

}
