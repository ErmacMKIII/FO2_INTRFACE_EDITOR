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
import javax.imageio.ImageIO;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.Shader;
import rs.alexanderstojanovich.fo2ie.ogl.ShaderProgram;
import rs.alexanderstojanovich.fo2ie.ogl.Texture;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.Tree;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ModuleAnimation implements GLEventListener {

    protected final Module module = new Module();
    protected final Intrface intrface;

    private ShaderProgram primSProgram;
    private ShaderProgram imgSProgram;
    private ShaderProgram fntSProgram;

    protected Texture fntTexture;

    public ModuleAnimation(Intrface intrface) {
        this.intrface = intrface;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl20 = glad.getGL().getGL2();
        gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        gl20.glEnable(GL2.GL_DEPTH_TEST);
        gl20.glDepthFunc(GL2.GL_LEQUAL);

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

        try {
            fntTexture = new Texture(gl20, ImageIO.read(this.getClass().getResourceAsStream(GUI.RESOURCES_DIR + GUI.FNT_PIC)));
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        }
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        GL2 gl20 = glad.getGL().getGL2();
        gl20.glViewport(0, 0, i2, i3);

        try {
            Tree<GLComponent> tree = intrface.buildTree(gl20, fntTexture);
            if (tree != null) {
                module.build(tree);
            }
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        }

    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl20 = glad.getGL().getGL2();
        gl20.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        module.render(gl20, primSProgram, imgSProgram, fntSProgram);
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

}
