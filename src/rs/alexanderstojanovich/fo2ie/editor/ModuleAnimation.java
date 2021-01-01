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
package rs.alexanderstojanovich.fo2ie.editor;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import java.io.IOException;
import org.joml.Matrix4f;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.ogl.Shader;
import rs.alexanderstojanovich.fo2ie.ogl.ShaderProgram;
import rs.alexanderstojanovich.fo2ie.ogl.Texture;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ModuleAnimation implements GLEventListener {

    private final Configuration config = Configuration.getInstance();

    public static final int DEF_WIDTH = 800;
    public static final int DEF_HEIGHT = 600;

    protected final Module module = new Module();
    protected final Intrface intrface;

    private ShaderProgram primSProgram;
    private ShaderProgram imgSProgram;
    private ShaderProgram fntSProgram;

    protected Texture fntTexture;
    protected Texture qmarkTexture;
    private final Matrix4f projMat4 = new Matrix4f().identity();

    public static enum State {
        INIT, BUILD, RENDER;
    }

    protected State state = State.INIT;

    public ModuleAnimation(Intrface intrface) {
        this.intrface = intrface;
    }

    /**
     * Sets the prespective according to the Interface pragma. Call this if
     * resizing occured
     */
    protected void setPerspective() {
        ResolutionPragma pragma = intrface.getResolutionPragma();
        final float width = (pragma == null) ? DEF_WIDTH : pragma.getWidth();
        final float height = (pragma == null) ? DEF_HEIGHT : pragma.getHeight();
        final float aspect = (float) width / (float) height;
        projMat4.identity().setOrtho2D(-aspect, aspect, -1.0f, 1.0f);
    }

    /**
     * Initializes Module animator (essentially init stuff)
     *
     * @param glad drawable object from interface
     */
    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl20 = glad.getGL().getGL2();
        gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        gl20.glEnable(GL2.GL_DEPTH_TEST);
        gl20.glDepthFunc(GL2.GL_LEQUAL); // this one is important for overlapping

        gl20.glEnable(GL2.GL_BLEND);
        gl20.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        gl20.glEnable(GL2.GL_CULL_FACE);
        gl20.glCullFace(GL2.GL_BACK);

        Shader primVS = new Shader(gl20, GUI.PRIM_VERTEX_SHADER, Shader.VERTEX_SHADER);
        Shader primFS = new Shader(gl20, GUI.PRIM_FRAGMENT_SHADER, Shader.FRAGMENT_SHADER);
        primSProgram = new ShaderProgram(gl20, primVS, primFS);

        Shader imgVS = new Shader(gl20, GUI.IMG_VERTEX_SHADER, Shader.VERTEX_SHADER);
        Shader imgFS = new Shader(gl20, GUI.IMG_FRAGMENT_SHADER, Shader.FRAGMENT_SHADER);
        imgSProgram = new ShaderProgram(gl20, imgVS, imgFS);

        Shader fntVS = new Shader(gl20, GUI.FNT_VERTEX_SHADER, Shader.VERTEX_SHADER);
        Shader fntFS = new Shader(gl20, GUI.FNT_FRAGMENT_SHADER, Shader.FRAGMENT_SHADER);
        fntSProgram = new ShaderProgram(gl20, fntVS, fntFS);

        fntTexture = Texture.loadLocalTexture(gl20, GUI.FNT_PIC);
        qmarkTexture = Texture.loadLocalTexture(gl20, GUI.QMARK_PIC);

        if (config.isKeepAspectRatio()) {
            setPerspective();
        }

        state = State.BUILD;
    }

    /**
     * When about to get rid of this module animation
     *
     * @param glad drawable object from interface
     */
    @Override
    public void dispose(GLAutoDrawable glad) {
        // cancel unbuffer timer
        module.timerTask.cancel();
    }

    /**
     * When it come to resizing
     *
     * @param glad drawable object from interface
     * @param i x offset from top left corner
     * @param i1 y offset from top left corner
     * @param i2 width
     * @param i3 height
     */
    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        GL2 gl20 = glad.getGL().getGL2();
        gl20.glViewport(0, 0, i2, i3);
        if (config.isKeepAspectRatio()) {
            setPerspective();
        }
        state = State.BUILD;
    }

    /**
     * Regular loop method (called by the FPS Animator)
     *
     * @param glad drawable object from interface
     */
    @Override
    public void display(GLAutoDrawable glad) {
        try {
            GL2 gl20 = glad.getGL().getGL2();
            gl20.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            switch (state) {
                // invoked when user desired to build the module
                case BUILD:
                    Texture.TEXTURE_MAP.clear();
                    module.components.clear();
                    module.components.addAll(intrface.build(gl20, fntTexture, qmarkTexture));
                    state = State.RENDER;
                    break;
                // otherwise render the module
                case RENDER:
                    module.render(gl20, projMat4, primSProgram, imgSProgram, fntSProgram);
                    break;
            }
        } catch (IOException ex) {
            FO2IELogger.reportError("Error occurred during during module build", null);
            FO2IELogger.reportError(ex.getMessage(), ex);
        }

    }

    public Module getModule() {
        return module;
    }

    public ShaderProgram getImgSProgram() {
        return imgSProgram;
    }

    public void setImgSProgram(ShaderProgram imgSProgram) {
        this.imgSProgram = imgSProgram;
    }

    public ShaderProgram getFntSProgram() {
        return fntSProgram;
    }

    public void setFntSProgram(ShaderProgram fntSProgram) {
        this.fntSProgram = fntSProgram;
    }

    public Intrface getIntrface() {
        return intrface;
    }

    public ShaderProgram getPrimSProgram() {
        return primSProgram;
    }

    public Texture getFntTexture() {
        return fntTexture;
    }

    public Texture getQmarkTexture() {
        return qmarkTexture;
    }

    public Matrix4f getProjMat4() {
        return projMat4;
    }

    public State getState() {
        return state;
    }

}
