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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import org.joml.Vector2f;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.MyRectangle;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.Resolution;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.PrimitiveQuad;
import rs.alexanderstojanovich.fo2ie.ogl.Shader;
import rs.alexanderstojanovich.fo2ie.ogl.ShaderProgram;
import rs.alexanderstojanovich.fo2ie.ogl.Text;
import rs.alexanderstojanovich.fo2ie.ogl.Texture;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.GLColor;
import rs.alexanderstojanovich.fo2ie.util.Pair;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class ModuleRenderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

    public static final Object OBJ_MUTEX = new Object();
    public static final Object OBJ_SYNC = new Object();

    private final Configuration config = Configuration.getInstance();

    public static final int DEF_WIDTH = 800;
    public static final int DEF_HEIGHT = 600;

    // essentials
    protected final Module module;
    protected final Intrface intrface;

    // shader programs {primitive, image and font}
    private ShaderProgram primSProgram;
    private ShaderProgram imgSProgram;
    private ShaderProgram fntSProgram;
    private ShaderProgram cntSProgram;

    // textures
    protected Texture fntTexture;
    protected Texture qmarkTexture;
    // projection matrix
    private final Matrix4f projMat4 = new Matrix4f().identity();

    // animator used for rendering in the loop
    private final FPSAnimator animator;

    protected GLComponent selected;
    protected GLComponent outline;

    protected GLComponent hintComponent; // key component for showing hint text (default orange)        
    protected Text textHint; // hint text

    protected boolean dragging = false;
//    protected Vector4f savedColor = new Vector4f();
    private Vector2f scrnMouseCoords = new Vector2f();

    private int selectedIndex = -1;

    private boolean hasFocus = false;

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

    protected Resolution guiResolution = Resolution.DEFAULT;
    protected SectionName guiSectionName = SectionName.Aim;

    protected BuildMode buildMode = BuildMode.TARGET_RES;

    /**
     * Create JOGL Animator of the built module
     *
     * @param animator parsed FPS animator
     * @param module GL module with components
     * @param intrface FOnline interface
     * @param guiResolution GUI resolution field
     * @param guiSectionName GUI section name
     */
    public ModuleRenderer(FPSAnimator animator, Module module, Intrface intrface, Resolution guiResolution, SectionName guiSectionName) {
        this.animator = animator;
        this.module = module;
        this.intrface = intrface;
        this.guiResolution = guiResolution;
        this.guiSectionName = guiSectionName;
    }

    /**
     * Sets the prespective according to the Interface pragma. Call this if
     * resizing occured
     *
     * @param resolution Resolution
     */
    protected void setPerspective(Resolution resolution) {
        final float aspect = (float) resolution.getWidth() / (float) resolution.getHeight();
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

        Shader cntVS = new Shader(gl20, GUI.OUTLINE_VERTEX_SHADER, Shader.VERTEX_SHADER);
        Shader cntFS = new Shader(gl20, GUI.OUTLINE_FRAGMENT_SHADER, Shader.FRAGMENT_SHADER);
        cntSProgram = new ShaderProgram(gl20, cntVS, cntFS);

        fntTexture = Texture.loadLocalTexture(gl20, GUI.FNT_PIC);
        qmarkTexture = Texture.loadLocalTexture(gl20, GUI.QMARK_PIC);

        if (config.isKeepAspectRatio()) {
            setPerspective(guiResolution);
        }

        textHint = new Text(null, null, fntTexture, "", GLColor.awtColorToVec4(config.getHintCol()), null);

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
        //gl20.glViewport(0, 0, i2, i3);
        if (config.isKeepAspectRatio()) {
            setPerspective(guiResolution);
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
                // move across the OpenGL render space
                if (selected != null && dragging) {
                    selected.setPos(scrnMouseCoords);
                    endMovingSelected();
                }
                module.render(gl20, projMat4, primSProgram, imgSProgram, fntSProgram);
                if (selected != null) {
                    selected.render(gl20, projMat4, cntSProgram);
                }
                if (!textHint.isBuffered()) {
                    textHint.buffer(gl20);
                }
                textHint.render(gl20, projMat4, fntSProgram);
                break;
            case BUILD:
                // suspend the loop until all components are built
                animator.pause();
                textHint.unbuffer();
                synchronized (OBJ_MUTEX) {
                    buildComponents(gl20);
                }
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
        ModuleBuildTask task = new ModuleBuildTask(guiSectionName, intrface, module, buildMode, gl20, fntTexture, qmarkTexture) {
            @Override
            protected void done() {
                afterModuleBuild();
                window.dispose();
                state = State.INIT;
                // resume the animating loop
                animator.resume();
                selectedIndex = -1;
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
        task.buildResolution = guiResolution;
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

        final int width = (GUI.GL_WINDOW.isVisible()) ? GUI.GL_WINDOW.getWidth() : GUI.GL_CANVAS.getWidth();
        final int height = (GUI.GL_WINDOW.isVisible()) ? GUI.GL_WINDOW.getHeight() : GUI.GL_CANVAS.getHeight();

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
    // selects one component (CTRL + A)
    public void select() {
        deselect();

        if (hintComponent != null && hintComponent.getInheritance() != GLComponent.Inheritance.CANVAS) {// canvas cannot be selected!
            selected = hintComponent;
            selected.setOutlineColor(GLColor.awtColorToVec4(config.getSelectCol()));
        }
        afterSelection();
    }

    // selects one component by feature key
    public void select(String uniqueId) {
        deselect();

        for (GLComponent glc : module.components) {
            if (glc.isEnabled() && glc.getUniqueId().equals(uniqueId) && glc.getInheritance() != GLComponent.Inheritance.CANVAS) {
                selected = glc;
                selected.setOutlineColor(GLColor.awtColorToVec4(config.getSelectCol()));
                break;
            }
        }

        afterSelection();
    }

    // select previous index of (selected components)
    public void selectPrev() {

        if (selectedIndex > 0) {
            deselect();
            GLComponent glc = module.components.get(Math.max(--selectedIndex, 0));
            selected = glc;
            selected.setOutlineColor(GLColor.awtColorToVec4(config.getSelectCol()));
        }

        afterSelection();
    }

    // select next index of (selected components)
    public void selectNext() {
        final int size = module.components.size();

        if (selectedIndex < size - 1) {
            deselect();
            GLComponent glc = module.components.get(Math.min(++selectedIndex, size - 1));
            selected = glc;
            selected.setOutlineColor(GLColor.awtColorToVec4(config.getSelectCol()));
        }

        afterSelection();
    }

    // deselects all (CTRL + D)
    public void deselect() {
        endMovingSelected();
        afterSelection();

        selected = null;
        outline = null;
    }

    public void selectToggleEnabled() {
        if (selected != null) {
            selected.setEnabled(!selected.isEnabled());
            afterSelection();
        } else if (hintComponent != null) {
            hintComponent.setEnabled(!hintComponent.isEnabled());
            afterSelection();
        }
    }

    public abstract void editFeature(FeatureKey fk, FeatureValue fv, GLComponent.Inheritance inh, Intrface intr);
//    protected FeatureValue getSelectedOriginalFeatureValue() {
//        if (selected == null) {
//            return null;
//        }
//
//        FeatureValue featureValue = null;
//        if (buildMode == BuildMode.ALL_RES) {
//            featureValue = intrface.getOriginalBinds().commonFeatMap.get(selected.getPosFeatureKey());
//        } else if (buildMode == BuildMode.TARGET_RES) {
//            ResolutionPragma resolutionPragma = intrface.getOriginalBinds().customResolutions.stream().filter(x -> x.getResolution().equals(guiResolution)).findFirst().orElse(null);
//            featureValue = resolutionPragma.getCustomFeatMap().get(selected.getPosFeatureKey());
//            if (featureValue == null) {
//                featureValue = intrface.getOriginalBinds().commonFeatMap.get(selected.getPosFeatureKey());
//            }
//        }
//
//        return featureValue;
//    }
//
//    protected FeatureValue getSelectedModifiedFeatureValue() {
//        if (selected == null) {
//            return null;
//        }
//
//        FeatureValue featureValue = null;
//        if (buildMode == BuildMode.ALL_RES) {
//            featureValue = intrface.getModifiedBinds().commonFeatMap.get(selected.getPosFeatureKey());
//        } else if (buildMode == BuildMode.TARGET_RES) {
//            ResolutionPragma resolutionPragma = intrface.getModifiedBinds().customResolutions.stream().filter(x -> x.getResolution().equals(guiResolution)).findFirst().orElse(null);
//            featureValue = resolutionPragma.getCustomFeatMap().get(selected.getPosFeatureKey());
//            if (featureValue == null) {
//                featureValue = intrface.getModifiedBinds().commonFeatMap.get(selected.getPosFeatureKey());
//            }
//        }
//
//        return featureValue;
//    }

//    protected void updateSelectedModifiedFeatureValue(FeatureValue featureValue) {
//        if (selected.getInheritance() == GLComponent.Inheritance.BASE) {
//            intrface.getModifiedBinds().commonFeatMap.replace(selected.getPosFeatureKey(), featureValue);
//        } else if (selected.getInheritance() == GLComponent.Inheritance.DERIVED) {
//            ResolutionPragma resolutionPragma = intrface.getModifiedBinds().customResolutions.stream().filter(x -> x.getResolution().equals(guiResolution)).findFirst().orElse(null);
//            if (resolutionPragma != null) {
//                resolutionPragma.getCustomFeatMap().replace(selected.getPosFeatureKey(), featureValue);
//            }
//        }
//    }
    // finalize moving selected
    private void endMovingSelected() {
        // process mouse release
        if (selected != null) {
            // try to find corrseponding feature value
            // based on module build mode do something.. 
            // try to set feature value with corresponding glMouseCoords
            Pair<Float, Float> skvp = ModuleBuildTask.modeScaleXYFactor;
            MyRectangle mr = new MyRectangle();
            if (selected instanceof Text) {
                Text selectedText = (Text) selected;
                // this is important
                PrimitiveQuad overlay = selectedText.getOverlay();
                mr.minX = Math.round(((overlay.getPos().x - overlay.getWidth() / 2.0f) - (ModuleBuildTask.root.getPos().x - ModuleBuildTask.root.getWidth() / 2.0f)) / skvp.getKey());
                mr.maxX = Math.round(((overlay.getPos().x + overlay.getWidth() / 2.0f) - (ModuleBuildTask.root.getPos().x - ModuleBuildTask.root.getWidth() / 2.0f)) / skvp.getKey());
                mr.minY = Math.round(((overlay.getPos().y - overlay.getHeight() / 2.0f) - (ModuleBuildTask.root.getPos().y - ModuleBuildTask.root.getHeight() / 2.0f)) / skvp.getValue());
                mr.maxY = Math.round(((overlay.getPos().y + overlay.getHeight() / 2.0f) - (ModuleBuildTask.root.getPos().y - ModuleBuildTask.root.getHeight() / 2.0f)) / skvp.getValue());
            } else {
                mr.minX = Math.round(((selected.getPos().x - selected.getWidth() / 2.0f) - (ModuleBuildTask.root.getPos().x - ModuleBuildTask.root.getWidth() / 2.0f)) / skvp.getKey());
                mr.maxX = Math.round(((selected.getPos().x + selected.getWidth() / 2.0f) - (ModuleBuildTask.root.getPos().x - ModuleBuildTask.root.getWidth() / 2.0f)) / skvp.getKey());
                mr.minY = Math.round(((selected.getPos().y - selected.getHeight() / 2.0f) - (ModuleBuildTask.root.getPos().y - ModuleBuildTask.root.getHeight() / 2.0f)) / skvp.getValue());
                mr.maxY = Math.round(((selected.getPos().y + selected.getHeight() / 2.0f) - (ModuleBuildTask.root.getPos().y - ModuleBuildTask.root.getHeight() / 2.0f)) / skvp.getValue());
            }

            if (guiSectionName == SectionName.GlobalMap) {
                mr.translate(Math.round(-ModuleBuildTask.xOffset / skvp.getKey()), Math.round(-ModuleBuildTask.yOffset / skvp.getValue()));
            }

            intrface.updateFeatureValue(selected.getPosFeatureKey(), selected.getInheritance(), mr, guiResolution);

            afterSelection();
        }
    }

    private void moveSelected(float x, float y) {
        scrnMouseCoords = new Vector2f(x, y);

        // move across the OpenGL render space
        if (selected != null) {
            selected.setPos(scrnMouseCoords);
            endMovingSelected();
        }
    }

    private void moveSelectedLeft(float amount) {
        float x = scrnMouseCoords.x;
        float y = scrnMouseCoords.y;

        moveSelected(x - amount, y);
    }

    private void moveSelectedRight(float amount) {
        float x = scrnMouseCoords.x;
        float y = scrnMouseCoords.y;

        moveSelected(x + amount, y);
    }

    private void moveSelectedDown(float amount) {
        float x = scrnMouseCoords.x;
        float y = scrnMouseCoords.y;

        moveSelected(x, y + amount);
    }

    private void moveSelectedUp(float amount) {
        float x = scrnMouseCoords.x;
        float y = scrnMouseCoords.y;

        moveSelected(x, y - amount);
    }

    private void showShortHandHintText() {
        if (hasFocus) {
            if (textHint != null && hintComponent != null) {
                textHint.setPos(scrnMouseCoords);
                textHint.setContent(String.valueOf(hintComponent.getPosFeatureKey()));
            }
        }
    }

    private void showVerboseHintText() {
        if (hasFocus) {
            if (textHint != null && hintComponent != null) {
                textHint.setPos(scrnMouseCoords);

                FeatureValue featVal = intrface.selectFeatureValue(hintComponent.getPosFeatureKey(), hintComponent.getInheritance(), guiResolution);
                StringBuilder sb = new StringBuilder();
                sb.append(hintComponent.getPosFeatureKey()).append("\n").append(hintComponent.getInheritance()).append("\n").append(featVal == null ? "" : featVal.getStringValue());
                textHint.setContent(sb.toString());
            }
        }
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

        endMovingSelected();
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
        hasFocus = true;
        if (textHint != null) {
            textHint.setEnabled(true);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hasFocus = false;
        if (textHint != null) {
            textHint.setEnabled(false);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!dragging) {
            return;
        }

        moveSelected(e.getX(), e.getY());

        dragging = true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.getX() != scrnMouseCoords.x && e.getY() != scrnMouseCoords.y) {
            for (GLComponent glc : module.components) {
                if (glc.isEnabled() && glc.getPixelArea().containsPoint(scrnMouseCoords)) {
                    hintComponent = glc;
                }
            }
        }
        scrnMouseCoords = new Vector2f(e.getX(), e.getY());
        showShortHandHintText();
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_F12) {
            state = ModuleRenderer.State.SCREENSHOT;
        }

        if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_D) {
            deselect();
        }

        if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_E) {
            if (selected != null) {
                FeatureValue featureValue = intrface.selectFeatureValue(selected.getPosFeatureKey(), selected.getInheritance(), guiResolution);
                editFeature(selected.getPosFeatureKey(), featureValue, selected.getInheritance(), intrface);
            } else if (hintComponent != null) {
                FeatureValue featureValue = intrface.selectFeatureValue(hintComponent.getPosFeatureKey(), hintComponent.getInheritance(), guiResolution);
                editFeature(hintComponent.getPosFeatureKey(), featureValue, hintComponent.getInheritance(), intrface);
            }
        }

        if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_A) {
            select();
        }

        if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_V) {
            selectToggleEnabled();
        }

        if (ke.isControlDown() && ke.getKeyCode() != KeyEvent.VK_A && ke.getKeyCode() != KeyEvent.VK_E && ke.getKeyCode() != KeyEvent.VK_D && ke.getKeyCode() != KeyEvent.VK_V) {
            showVerboseHintText();
        }

        if (ke.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) {
            selectPrev();
        }

        if (ke.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
            selectNext();
        }

        if (ke.getKeyCode() == KeyEvent.VK_LEFT && ke.isShiftDown()) {
            moveSelectedLeft(5.0f);
        }

        if (ke.getKeyCode() == KeyEvent.VK_LEFT && !ke.isShiftDown()) {
            moveSelectedLeft(1.0f);
        }

        if (ke.getKeyCode() == KeyEvent.VK_RIGHT && ke.isShiftDown()) {
            moveSelectedRight(5.0f);
        }

        if (ke.getKeyCode() == KeyEvent.VK_RIGHT && !ke.isShiftDown()) {
            moveSelectedRight(1.0f);
        }

        if (ke.getKeyCode() == KeyEvent.VK_DOWN && ke.isShiftDown()) {
            moveSelectedDown(5.0f);
        }

        if (ke.getKeyCode() == KeyEvent.VK_DOWN && !ke.isShiftDown()) {
            moveSelectedDown(1.0f);
        }

        if (ke.getKeyCode() == KeyEvent.VK_UP && ke.isShiftDown()) {
            moveSelectedUp(5.0f);
        }

        if (ke.getKeyCode() == KeyEvent.VK_UP && !ke.isShiftDown()) {
            moveSelectedUp(1.0f);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        //..IGNORED
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //..IGNORED
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

    public ShaderProgram getCntSProgram() {
        return cntSProgram;
    }

    public void setCntSProgram(ShaderProgram cntSProgram) {
        this.cntSProgram = cntSProgram;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
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

    public BuildMode getBuildMode() {
        return buildMode;
    }

    public GLComponent getOutline() {
        return outline;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public Resolution getGuiResolution() {
        return guiResolution;
    }

    public SectionName getGuiSectionName() {
        return guiSectionName;
    }

    public Text getTextHint() {
        return textHint;
    }

}
