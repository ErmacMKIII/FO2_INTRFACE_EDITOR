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
package rs.alexanderstojanovich.fo2ie.main;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.joml.Matrix4f;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.MyRectangle;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.Shader;
import rs.alexanderstojanovich.fo2ie.ogl.ShaderProgram;
import rs.alexanderstojanovich.fo2ie.ogl.Text;
import rs.alexanderstojanovich.fo2ie.ogl.Texture;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.Pair;
import rs.alexanderstojanovich.fo2ie.util.ScalingUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class ModuleRenderer implements GLEventListener, MouseListener, MouseMotionListener {

    public static final Object OBJ_MUTEX = new Object();
    public static final Object OBJ_SYNC = new Object();

    private final Configuration config = Configuration.getInstance();

    public static final int DEF_WIDTH = 800;
    public static final int DEF_HEIGHT = 600;

    // essentials
    protected final Module module = new Module();
    protected final Intrface intrface;

    // shader programs {primitive, image and font}
    private ShaderProgram primSProgram;
    private ShaderProgram imgSProgram;
    private ShaderProgram fntSProgram;

    // textures
    protected Texture fntTexture;
    protected Texture qmarkTexture;
    // projection matrix
    private final Matrix4f projMat4 = new Matrix4f().identity();

    // animator used for rendering in the loop
    private final FPSAnimator animator;

    protected GLComponent selected;
    protected boolean dragging = false;
    protected Vector4f savedColor = new Vector4f();
    private Vector2f scrnMouseCoords;

    /**
     * State of the machine
     */
    public static enum State {
        INIT, RENDER, BUILD, SCREENSHOT, SUSPEND
    }

    protected State state = State.INIT;

    /**
     * Build mode {ALL_RES, TARGET_RES}
     */
    public static enum BuildMode {
        ALL_RES, TARGET_RES;
    };

    protected BuildMode buildMode = BuildMode.TARGET_RES;

    /**
     * Create JOGL Animator of the built module
     *
     * @param animator parsed FPS animator
     * @param intrface FOnline interface
     */
    public ModuleRenderer(FPSAnimator animator, Intrface intrface) {
        this.animator = animator;
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

        this.animator.start();
        state = State.INIT;
    }

    /**
     * When about to get rid of this module animation
     *
     * @param glad drawable object from interface
     */
    @Override
    public void dispose(GLAutoDrawable glad) {
        glad.getGL().getContext().destroy();
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
    }

    /**
     * Regular loop method (called by the FPS Animator)
     *
     * @param glad drawable object from interface
     */
    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl20 = glad.getGL().getGL2();
        switch (state) {
            case INIT:
                state = State.RENDER;
                break;
            case RENDER:
                gl20.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
                module.render(gl20, projMat4, primSProgram, imgSProgram, fntSProgram);
                break;
            case BUILD:
                // suspend the loop until all components are built
                animator.pause();
                buildComponents(gl20);
                state = State.SUSPEND;
                break;
            case SCREENSHOT:
                BufferedImage screenshot = createScreenshot(gl20);
                boolean ok = saveScreenshot(screenshot);
                if (ok) {
                    JOptionPane.showMessageDialog(GUI.GL_CANVAS.getParent().getParent(), "Screenshot saved.", "Module Screenshot", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(GUI.GL_CANVAS.getParent().getParent(), "Screenshot failed!", "Module Screenshot", JOptionPane.ERROR_MESSAGE);
                }
                state = State.RENDER;
                break;
            case SUSPEND:
            default:
                break;
        }
    }

    //--------------------------------------------------------------------------
    private void buildComponents(GL2 gl20) {
        final ProgressWindow window = new ProgressWindow();
        ModuleBuildTask task = new ModuleBuildTask(intrface.getSectionName(), intrface, module, buildMode, gl20, fntTexture, qmarkTexture) {
            @Override
            protected void done() {
                afterModuleBuild();
                window.dispose();
                state = State.INIT;
                // resume the animating loop
                animator.resume();
            }
        };
        task.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    window.progressBar.setValue(Math.round((float) evt.getNewValue()));
                    window.progressBar.validate();
                }
            }
        });
        task.execute();
    }

    //--------------------------------------------------------------------------
    /**
     * Read pixels and create screenshot as an image.
     *
     * @param gl20 GL20
     * @return screenshot as buffered image
     */
    public static BufferedImage createScreenshot(GL2 gl20) {
        final int rgba = 4;

        final int width = GUI.GL_CANVAS.getWidth();
        final int height = GUI.GL_CANVAS.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        ByteBuffer buffer = GLBuffers.newDirectByteBuffer(width * height * rgba);

        gl20.glReadBuffer(GL2.GL_FRONT);
        gl20.glReadPixels(0, 0, width, height, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, buffer);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = (x + (width * y)) * rgba;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;
                image.setRGB(x, height - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return image;
    }

    /**
     * Save screenshot image to the external image file.Image will be stored in
     * screenshot directory with in PNG format.
     *
     * @param buffImg screenshot
     * @return operation success.
     */
    public static boolean saveScreenshot(BufferedImage buffImg) {
        boolean ok = false;

        File screenDir = new File(GUI.SCREENSHOT_DIR);
        if (!screenDir.isDirectory() && !screenDir.exists()) {
            screenDir.mkdir();
        }
        LocalDateTime now = LocalDateTime.now();
        File screenshot = new File(GUI.SCREENSHOT_DIR + File.separator
                + "fo2_intrfaceditor-" + now.getYear()
                + "-" + now.getMonthValue()
                + "-" + now.getDayOfMonth()
                + "_" + now.getHour()
                + "-" + now.getMinute()
                + "-" + now.getSecond()
                + "-" + now.getNano() / 1E6 // one million
                + ".png");
        if (screenshot.exists()) {
            screenshot.delete();
        }

        try {
            ImageIO.write(buffImg, "PNG", screenshot);
            ok = true;
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        }

        return ok;
    }

    //--------------------------------------------------------------------------
    @Override
    public void mouseClicked(MouseEvent e) {
        // ..IGNORE
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (dragging) {
            return;
        }
        dragging = true;

        scrnMouseCoords = new Vector2f(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!dragging) {
            return;
        }
        dragging = false;

        // process mouse release
        if (selected != null) {
            // try to find corrseponding feature value
            FeatureValue featureValue = null;
            // based on module build mode do something..
            if (buildMode == BuildMode.ALL_RES) {
                featureValue = intrface.getCommonFeatMap().get(selected.getFeatureKey());
            } else if (buildMode == BuildMode.TARGET_RES) {
                ResolutionPragma resolutionPragma = intrface.getResolutionPragma();
                if (resolutionPragma != null && resolutionPragma.getCustomFeatMap().containsKey(selected.getFeatureKey())) {
                    featureValue = resolutionPragma.getCustomFeatMap().get(selected.getFeatureKey());
                } else {
                    featureValue = intrface.getCommonFeatMap().get(selected.getFeatureKey());
                }
            }

            // try to set feature value with corresponding glMouseCoords
            if (featureValue != null && featureValue.getType() == FeatureValue.Type.RECT4) {
                MyRectangle mr = (MyRectangle) featureValue;
                Rectanglef xr;
                if (selected instanceof Text) {
                    Text selectedText = (Text) selected;
                    // this is important
                    xr = selectedText.getOverlay().getPixelArea();
                } else {
                    xr = selected.getPixelArea();
                }

                Pair<Float, Float> skvp = ScalingUtils.scaleXYFactor(intrface.getModeWidth(), intrface.getModeHeight(), intrface.getMainPicWidth(), intrface.getMainPicHeight());

                mr.minX = Math.round(xr.minX / skvp.getKey());
                mr.maxX = Math.round(xr.maxX / skvp.getKey());
                mr.minY = Math.round(xr.minY / skvp.getValue());
                mr.maxY = Math.round(xr.maxY / skvp.getValue());

                afterSelection();
            }

        }
    }

    /**
     * Action which takes place after module build
     */
    public abstract void afterModuleBuild();

    /**
     * Action which takes place after selection
     */
    public abstract void afterSelection();

    @Override
    public void mouseEntered(MouseEvent e) {
        // ..IGNORE
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // same as deselect from the GUI
        if (selected != null) {
            selected.setColor(savedColor);
        }
        selected = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!dragging) {
            return;
        }

        scrnMouseCoords = new Vector2f(e.getX(), e.getY());

        // move across the OpenGL render space
        if (selected != null) {
            selected.setPos(scrnMouseCoords);
            afterSelection();
        }

        dragging = true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        scrnMouseCoords = new Vector2f(e.getX(), e.getY());
    }

    //--------------------------------------------------------------------------
    public Vector4f getSavedColor() {
        return savedColor;
    }

    public void setSavedColor(Vector4f savedColor) {
        this.savedColor = savedColor;
    }

    //--------------------------------------------------------------------------
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

    public Configuration getConfig() {
        return config;
    }

    public FPSAnimator getAnimator() {
        return animator;
    }

    public GLComponent getSelected() {
        return selected;
    }

    public Vector2f getScrnMouseCoords() {
        return scrnMouseCoords;
    }

    public boolean isDragging() {
        return dragging;
    }

}
