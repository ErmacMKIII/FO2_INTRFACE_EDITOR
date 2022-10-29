/*
 * Copyright (C) 2021 Alexander Stojanovich <coas91@rocketmail.com>
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingWorker;
import org.joml.Vector2f;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.ImageWrapper;
import rs.alexanderstojanovich.fo2ie.feature.MyRectangle;
import rs.alexanderstojanovich.fo2ie.feature.SingleValue;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.intrface.Section;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.ogl.AddressableQuad;
import rs.alexanderstojanovich.fo2ie.ogl.Animation;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.PrimitiveQuad;
import rs.alexanderstojanovich.fo2ie.ogl.Quad;
import rs.alexanderstojanovich.fo2ie.ogl.Text;
import rs.alexanderstojanovich.fo2ie.ogl.Texture;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.MathUtils;
import rs.alexanderstojanovich.fo2ie.util.Pair;
import rs.alexanderstojanovich.fo2ie.util.ScalingUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ModuleBuildTask extends SwingWorker<Object, Object> {

    protected final Intrface intrface;
    protected final Module module;
    protected final Texture fntTexture;
    protected final Texture unusedTexture;
    protected final GL2 gl20;
    protected final SectionName sectionName;
    protected final ModuleRenderer.BuildMode buildMode;

    /**
     * Create new task to build the module
     *
     * @param sectionName section name (required for building the module)
     * @param intrface interface from which this module is taken
     * @param module module container with GL Components to build
     * @param buildMode build mode {ALL_RES, TARGET_RES}
     * @param gl20 gl20 drawable binding
     * @param fntTexture font texture
     * @param unusedTexture question mark for missing texture
     */
    public ModuleBuildTask(SectionName sectionName, Intrface intrface, Module module, ModuleRenderer.BuildMode buildMode, GL2 gl20, Texture fntTexture, Texture unusedTexture) {
        this.sectionName = sectionName;
        this.intrface = intrface;
        this.module = module;
        this.buildMode = buildMode;
        this.gl20 = gl20;
        this.fntTexture = fntTexture;
        this.unusedTexture = unusedTexture;
    }

    /**
     * Builds components list from common section based on all resolutions
     *
     * @throws java.io.IOException if building the module fails due to missing
     * image
     */
    public void buildAllRes() throws IOException {
        float oldProgress = 0.0f, progress = 0.0f;

        module.components.clear();

        intrface.modeWidth = 800;
        intrface.modeHeight = 600;

        intrface.mainPicWidth = 800;
        intrface.mainPicHeight = 600;

        // final result is array list of components
        final Section section = intrface.getNameToSectionMap().get(sectionName);
        if (section != null) {
            FeatureKey mainPicKey = section.getRoot().getMainPic();
            MyRectangle mainPicPosVal = null;

            // it is displayed on the small panel under some resolution, scaling is required
            Pair<Float, Float> scaleXYFactor = ScalingUtils.scaleXYFactor(intrface.modeWidth, intrface.modeHeight, intrface.mainPicWidth, intrface.mainPicHeight);

            // if main picture exists (and in most cases it does apart from LMenu (known as pop-up menu)
            if (mainPicKey != null && intrface.getCommonFeatMap().containsKey(mainPicKey)) {
                ImageWrapper mainPicVal = (ImageWrapper) intrface.getCommonFeatMap().get(mainPicKey);
                mainPicVal.loadImages();

                // texture for main picture
                Texture rootTex;
                // if main picture holds the image load the texture
                if (mainPicVal.getImages() != null && mainPicVal.getImages().length == 1) {
                    intrface.mainPicWidth = mainPicVal.getImages()[0].getWidth();
                    intrface.mainPicHeight = mainPicVal.getImages()[0].getHeight();
                    rootTex = Texture.loadTexture(mainPicVal.getStringValue(), gl20, mainPicVal.getImages()[0]);
                    // otherwise load missing question mark texture
                } else {
                    rootTex = Texture.loadLocalTexture(gl20, GUI.QMARK_PIC);
                }

                scaleXYFactor = ScalingUtils.scaleXYFactor(intrface.modeWidth, intrface.modeHeight, intrface.mainPicWidth, intrface.mainPicHeight);
                FeatureKey mainPicPosKey = mainPicKey.getMainPicPos();
                if (mainPicPosKey != null && intrface.getCommonFeatMap().containsKey(mainPicPosKey)) {
                    mainPicPosVal = (MyRectangle) intrface.getCommonFeatMap().get(mainPicPosKey);
                    MyRectangle vtemp = new MyRectangle();
                    mainPicPosVal = mainPicPosVal.scaleXY(intrface.mainPicWidth, intrface.mainPicHeight, intrface.modeWidth, intrface.modeHeight, vtemp);
                }

                Vector2f rootPos = new Vector2f(intrface.mainPicWidth * scaleXYFactor.getKey() / 2.0f, intrface.mainPicHeight * scaleXYFactor.getValue() / 2.0f);
                Quad root = new Quad(mainPicPosKey == null ? mainPicKey : mainPicPosKey, GLComponent.Inheritance.BASE, Math.round(intrface.mainPicWidth * scaleXYFactor.getKey()), Math.round(intrface.mainPicHeight * scaleXYFactor.getValue()), rootTex, rootPos);
                module.components.add(root);
            }

            // defining mutually exclusive Lists for pictures, primitives (overlays) and text
            final List<GLComponent> picComps = new ArrayList<>();
            final List<GLComponent> txtComps = new ArrayList<>();

            FeatureKey[] picPosValues = section.getRoot().getPicPosValues();
            if (picPosValues != null) {
                for (FeatureKey picPosKey : picPosValues) {
                    if (picPosKey != section.getRoot().getMainPicPos()) {
                        FeatureValue picPosVal = intrface.getCommonFeatMap().get(picPosKey);
                        if (picPosVal instanceof MyRectangle) {
                            MyRectangle picPosRect = (MyRectangle) picPosVal;
                            MyRectangle temp = new MyRectangle();

                            // scale rectangle to the drawing surface
                            picPosRect = picPosRect.scaleXY(scaleXYFactor.getKey(), scaleXYFactor.getValue(), temp);

                            Pair<FeatureKey, FeatureKey> splitValues = FeatureKey.getSplitValues(picPosKey);
                            float splitW = 0.0f, splitH = 0.0f;
                            if (splitValues != null) {
                                FeatureKey keyW = splitValues.getKey();
                                if (keyW != null) {
                                    FeatureValue valW = intrface.getCommonFeatMap().get(keyW);
                                    if (valW instanceof SingleValue) {
                                        splitW = ((SingleValue) valW).getNumber();
                                    }
                                }
                                FeatureKey keyH = splitValues.getValue();
                                if (keyH != null) {
                                    FeatureValue valH = intrface.getCommonFeatMap().get(keyH);
                                    if (valH instanceof SingleValue) {
                                        splitH = ((SingleValue) valH).getNumber();
                                    }
                                }
                            }

                            List<FeatureKey> pics = picPosKey.getPics();
                            for (FeatureKey picKey : pics) {
                                FeatureValue picVal = intrface.getCommonFeatMap().get(picKey);
                                if (picVal instanceof ImageWrapper) {
                                    ImageWrapper iw = (ImageWrapper) picVal;
                                    iw.loadImages();
                                    BufferedImage[] images = iw.getImages();
                                    if (images != null && images.length > 0) {
                                        // dimension of picture/animation in pixels
                                        int width, height;
                                        // pixel (screen) coordinates
                                        Vector2f pos = new Vector2f();
                                        if (images.length == 1 && splitW == 0.0f && splitH == 0.0f) {
                                            // pixel dimension
                                            width = Math.round(scaleXYFactor.getKey() * images[0].getWidth());
                                            height = Math.round(scaleXYFactor.getValue() * images[0].getHeight());

                                            if (width > 0 && height > 0) {
                                                // pixel position
                                                pos.x = picPosRect.minX + width / 2.0f;
                                                pos.y = picPosRect.minY + height / 2.0f;

                                                // Global Map special case
                                                if (mainPicPosVal != null && section.getSectionName() == Section.SectionName.GlobalMap) {
                                                    pos.x += mainPicPosVal.minX;
                                                    pos.y += mainPicPosVal.minY;
                                                }

                                                // texture from loaded image
                                                Texture tex = Texture.loadTexture(iw.getStringValue(), gl20, images[0]);
                                                Quad imgComp = new Quad(picPosKey, GLComponent.Inheritance.BASE, width, height, tex, pos);
                                                picComps.add(imgComp);
                                            }
                                        } else if (splitW != 0.0f || splitH != 0.0f) {
                                            // pixel dimension
                                            width = Math.round(scaleXYFactor.getKey() * images[0].getWidth());
                                            height = Math.round(scaleXYFactor.getValue() * images[0].getHeight());
                                            // pixel position (picPosRect is already scaled)

                                            pos.x = picPosRect.minX + width / 2.0f;
                                            pos.y = picPosRect.minY + height / 2.0f;

                                            Vector2f posMax = new Vector2f(
                                                    picPosRect.maxX - width / 2.0f, picPosRect.maxY - height / 2.0f
                                            );

                                            // texture from loaded image
                                            Texture aqtex = Texture.loadTexture(iw.getStringValue(), gl20, images[0]);
                                            AddressableQuad aq = new AddressableQuad(picPosKey, GLComponent.Inheritance.BASE, width, height, aqtex, pos, splitW, splitH, posMax);
                                            picComps.add(aq);
                                        } else {
                                            // pixel dimension                                            
                                            width = picPosRect.lengthX();
                                            height = picPosRect.lengthY();
                                            if (width > 0 && height > 0) {
                                                // pixel position (picPosRect is already scaled)
                                                pos.x = (picPosRect.minX + picPosRect.maxX) / 2.0f;
                                                pos.y = (picPosRect.minY + picPosRect.maxY) / 2.0f;

                                                // Global Map special case
                                                if (mainPicPosVal != null && section.getSectionName() == Section.SectionName.GlobalMap) {
                                                    pos.x += mainPicPosVal.minX;
                                                    pos.y += mainPicPosVal.minY;
                                                }

                                                int index = 0;
                                                // array of textures for an animation
                                                final Texture[] texas = new Texture[images.length];
                                                for (BufferedImage image : images) {
                                                    texas[index] = Texture.loadTexture(iw.getStringValue() + index, gl20, image);
                                                    index++;
                                                }
                                                Animation anim = new Animation(picPosKey, GLComponent.Inheritance.BASE, iw.getFps(), width, height, texas, pos);
                                                picComps.add(anim);
                                            }
                                        }
                                    }
                                } else if (picVal != null) {
                                    FO2IELogger.reportWarning("Unexisting cast for ("
                                            + picKey.getStringValue() + ", " + picVal.getStringValue() + ")", null);
                                }
                            }

                            // missing picture -> question mark for missing..
                            if (pics.isEmpty()) {
                                if (splitW != 0.0f || splitH != 0.0f) {
                                    // pixel dimension
                                    int width = (splitW != 0.0f) ? Math.round(splitW) : picPosRect.lengthX();
                                    int height = (splitH != 0.0f) ? Math.round(splitH) : picPosRect.lengthY();
                                    // pixel position (picPosRect is already scaled)

                                    Vector2f pos = new Vector2f(
                                            picPosRect.minX + width / 2.0f,
                                            picPosRect.minY + height / 2.0f
                                    );

                                    Vector2f posMax = new Vector2f(
                                            picPosRect.maxX - width / 2.0f,
                                            picPosRect.maxY - height / 2.0f
                                    );

                                    // texture is missing texture
                                    Texture aqtex = Texture.loadLocalTexture(gl20, GUI.QMARK_PIC);
                                    AddressableQuad aq = new AddressableQuad(picPosKey, GLComponent.Inheritance.BASE, width, height, aqtex, pos, splitW, splitH, posMax);
                                    aq.setColor(intrface.getQmarkColor());
                                    picComps.add(aq);
                                } else {
                                    // position is always center (half sum of coords)
                                    Vector2f pos = new Vector2f(
                                            (picPosRect.minX + picPosRect.maxX) / 2.0f,
                                            (picPosRect.minY + picPosRect.maxY) / 2.0f
                                    );
                                    // Global Map special case
                                    if (mainPicPosVal != null && section.getSectionName() == Section.SectionName.GlobalMap) {
                                        pos.x += mainPicPosVal.minX;
                                        pos.y += mainPicPosVal.minY;
                                    }
                                    Quad qmark = new Quad(
                                            picPosKey, GLComponent.Inheritance.BASE, picPosRect.lengthX(), picPosRect.lengthY(),
                                            Texture.loadLocalTexture(gl20, GUI.QMARK_PIC), pos
                                    );
                                    qmark.setColor(intrface.getQmarkColor());
                                    picComps.add(qmark);
                                }

                            }

                        } else if (picPosVal != null) {
                            FO2IELogger.reportWarning("Unexisting cast for ("
                                    + picPosKey.getStringValue() + ", " + picPosVal.getStringValue() + ")", null);
                        }
                    }

                    oldProgress = progress;
                    progress += 50.0f / picPosValues.length;

                    firePropertyChange("progress", oldProgress, progress);
                }
            }

            FeatureKey[] textValues = section.getRoot().getTextValues();
            if (textValues != null) {
                for (FeatureKey txtKey : textValues) {
                    FeatureValue txtVal = intrface.getCommonFeatMap().get(txtKey);
                    if (txtVal instanceof MyRectangle) {
                        MyRectangle textRect = (MyRectangle) txtVal;
                        MyRectangle temp = new MyRectangle();

                        // scale rectangle to the drawing surface
                        textRect = textRect.scaleXY(scaleXYFactor.getKey(), scaleXYFactor.getValue(), temp);

                        int width = textRect.lengthX();
                        int height = textRect.lengthY();

                        if (width > 0 && height > 0) {
                            // determine position of this picture
                            float posx = (textRect.minX + textRect.maxX) / 2.0f;
                            float posy = (textRect.minY + textRect.maxY) / 2.0f;

                            // Global Map special case
                            if (mainPicPosVal != null && section.getSectionName() == Section.SectionName.GlobalMap) {
                                posx += mainPicPosVal.minX;
                                posy += mainPicPosVal.minY;
                            }

                            // calc screen pixel coordinates
                            Vector2f pos = new Vector2f(posx, posy);

                            // this is text (primitive) overlay representing the area which text is populating
                            PrimitiveQuad txtOlay = new PrimitiveQuad(width, height, pos);
                            txtOlay.setColor(intrface.getTextOverlayColor());

                            // text display (content)
                            String regex = txtKey.getPrefix() + "|" + "Text";
                            String content = txtKey.getStringValue().replaceFirst(regex, "");

                            // font char dimensions
                            int fntWidth = Math.round(Text.STD_FONT_WIDTH * scaleXYFactor.getKey());
                            int fntHeight = Math.round(Text.STD_FONT_HEIGHT * scaleXYFactor.getValue());

                            // this is text component                    
                            Text txt = new Text(txtKey, GLComponent.Inheritance.BASE, fntTexture, content, pos, fntWidth, fntHeight);
                            txt.setColor(intrface.getTextColor());
                            txt.setAlignment(Text.ALIGNMENT_CENTER);
                            txt.getOverlay().setColor(intrface.getTextOverlayColor());
                            txt.getOverlay().setWidth(width);
                            txt.getOverlay().setHeight(height);
                            txt.setScale(0.67f * MathUtils.lerp(fntWidth, width, 0.75f) / (float) (fntWidth + width) + 0.33f * MathUtils.lerp(fntHeight, height, 0.75f) / (float) (fntHeight + height));
                            txtComps.add(txt);
                        }
                    } else if (txtVal != null) {
                        FO2IELogger.reportWarning("Unexisting cast for ("
                                + txtKey.getStringValue() + ", " + txtVal.getStringValue() + ")", null);
                    }

                    oldProgress = progress;
                    progress += 50.0f / textValues.length;

                    firePropertyChange("progress", oldProgress, progress);
                }
            }

            module.components.addAll(picComps);
            module.components.addAll(txtComps);
        }

        oldProgress = progress;
        progress = 100.0f;

        firePropertyChange("progress", oldProgress, progress);

        //module.components.addAll(result);
    }

    /**
     * Builds components list from current section based on resolution
     *
     * @throws java.io.IOException if building the module fails due to missing
     * image
     */
    public void buildTargetRes() throws IOException {
        float oldProgress = 0.0f, progress = 0.0f;

        module.components.clear();

        final Map<FeatureKey, FeatureValue> resFeatMap = new HashMap<>(intrface.getCommonFeatMap());

        ResolutionPragma resolutionPragma = intrface.getResolutionPragma();
        if (resolutionPragma != null) {
            for (FeatureKey custKey : resolutionPragma.getCustomFeatMap().keySet()) {
                FeatureValue custVal = resolutionPragma.getCustomFeatMap().get(custKey);
                if (resFeatMap.containsKey(custKey)) {
                    resFeatMap.replace(custKey, custVal);
                } else {
                    resFeatMap.put(custKey, custVal);
                }
            }
        }

        intrface.modeWidth = (resolutionPragma == null) ? 800 : resolutionPragma.getWidth();
        intrface.modeHeight = (resolutionPragma == null) ? 600 : resolutionPragma.getHeight();

        intrface.mainPicWidth = 800;
        intrface.mainPicHeight = 600;

        // final result is array list of components
        final Section section = intrface.getNameToSectionMap().get(sectionName);
        if (section != null) {
            FeatureKey mainPicKey = section.getRoot().getMainPic();
            MyRectangle mainPicPosVal = null;

            // it is displayed on the small panel under some resolution, scaling is required
            Pair<Float, Float> scaleXYFactor = ScalingUtils.scaleXYFactor(intrface.modeWidth, intrface.modeHeight, intrface.mainPicWidth, intrface.mainPicHeight);

            // if main picture exists (and in most cases it does apart from LMenu (known as pop-up menu)
            if (mainPicKey != null && resFeatMap.containsKey(mainPicKey)) {
                ImageWrapper mainPicVal = (ImageWrapper) resFeatMap.get(mainPicKey);
                mainPicVal.loadImages();

                // texture for main picture
                Texture rootTex;
                // if main picture holds the image load the texture
                if (mainPicVal.getImages() != null && mainPicVal.getImages().length == 1) {
                    intrface.mainPicWidth = mainPicVal.getImages()[0].getWidth();
                    intrface.mainPicHeight = mainPicVal.getImages()[0].getHeight();
                    rootTex = Texture.loadTexture(mainPicVal.getStringValue(), gl20, mainPicVal.getImages()[0]);
                    // otherwise load missing question mark texture
                } else {
                    rootTex = Texture.loadLocalTexture(gl20, GUI.QMARK_PIC);
                }

                scaleXYFactor = ScalingUtils.scaleXYFactor(intrface.modeWidth, intrface.modeHeight, intrface.mainPicWidth, intrface.mainPicHeight);
                FeatureKey mainPicPosKey = mainPicKey.getMainPicPos();
                if (mainPicPosKey != null && resFeatMap.containsKey(mainPicPosKey)) {
                    mainPicPosVal = (MyRectangle) resFeatMap.get(mainPicPosKey);
                    MyRectangle vtemp = new MyRectangle();
                    mainPicPosVal = mainPicPosVal.scaleXY(intrface.mainPicWidth, intrface.mainPicHeight, intrface.modeWidth, intrface.modeHeight, vtemp);
                }

                Vector2f rootPos = new Vector2f(intrface.mainPicWidth * scaleXYFactor.getKey() / 2.0f, intrface.mainPicHeight * scaleXYFactor.getValue() / 2.0f);
                GLComponent.Inheritance inheritance = null;
                if (resolutionPragma != null && resolutionPragma.getCustomFeatMap().containsKey(mainPicPosKey)) {
                    inheritance = GLComponent.Inheritance.DERIVED;
                } else if (intrface.getCommonFeatMap().containsKey(mainPicPosKey)) {
                    inheritance = GLComponent.Inheritance.BASE;
                }
                Quad root = new Quad(mainPicPosKey == null ? mainPicKey : mainPicPosKey, GLComponent.Inheritance.BASE, Math.round(intrface.mainPicWidth * scaleXYFactor.getKey()), Math.round(intrface.mainPicHeight * scaleXYFactor.getValue()), rootTex, rootPos);
                module.components.add(root);
            }

            // defining mutually exclusive Lists for pictures, primitives (overlays) and text
            final List<GLComponent> picComps = new ArrayList<>();
            final List<GLComponent> txtComps = new ArrayList<>();

            FeatureKey[] picPosValues = section.getRoot().getPicPosValues();
            if (picPosValues != null) {
                for (FeatureKey picPosKey : picPosValues) {
                    if (picPosKey != section.getRoot().getMainPicPos()) {
                        FeatureValue picPosVal = resFeatMap.get(picPosKey);
                        if (picPosVal instanceof MyRectangle) {
                            MyRectangle picPosRect = (MyRectangle) picPosVal;
                            MyRectangle temp = new MyRectangle();

                            // scale rectangle to the drawing surface
                            picPosRect = picPosRect.scaleXY(scaleXYFactor.getKey(), scaleXYFactor.getValue(), temp);

                            Pair<FeatureKey, FeatureKey> splitValues = FeatureKey.getSplitValues(picPosKey);
                            float splitW = 0.0f, splitH = 0.0f;
                            if (splitValues != null) {
                                FeatureKey keyW = splitValues.getKey();
                                if (keyW != null) {
                                    FeatureValue valW = resFeatMap.get(keyW);
                                    if (valW instanceof SingleValue) {
                                        splitW = ((SingleValue) valW).getNumber();
                                    }
                                }
                                FeatureKey keyH = splitValues.getValue();
                                if (keyH != null) {
                                    FeatureValue valH = resFeatMap.get(keyH);
                                    if (valH instanceof SingleValue) {
                                        splitH = ((SingleValue) valH).getNumber();
                                    }
                                }
                            }

                            List<FeatureKey> pics = picPosKey.getPics();
                            for (FeatureKey picKey : pics) {
                                FeatureValue picVal = resFeatMap.get(picKey);
                                if (picVal instanceof ImageWrapper) {
                                    ImageWrapper iw = (ImageWrapper) picVal;
                                    iw.loadImages();
                                    BufferedImage[] images = iw.getImages();
                                    if (images != null && images.length > 0) {
                                        // dimension of picture/animation in pixels
                                        int width, height;
                                        // pixel (screen) coordinates
                                        Vector2f pos = new Vector2f();
                                        if (images.length == 1 && splitW == 0.0f && splitH == 0.0f) {
                                            // pixel dimension
                                            width = Math.round(scaleXYFactor.getKey() * images[0].getWidth());
                                            height = Math.round(scaleXYFactor.getValue() * images[0].getHeight());

                                            if (width > 0 && height > 0) {
                                                // pixel position
                                                pos.x = picPosRect.minX + width / 2.0f;
                                                pos.y = picPosRect.minY + height / 2.0f;

                                                // Global Map special case
                                                if (mainPicPosVal != null && section.getSectionName() == Section.SectionName.GlobalMap) {
                                                    pos.x += mainPicPosVal.minX;
                                                    pos.y += mainPicPosVal.minY;
                                                }

                                                // texture from loaded image
                                                Texture tex = Texture.loadTexture(iw.getStringValue(), gl20, images[0]);
                                                GLComponent.Inheritance inheritance = null;
                                                if (resolutionPragma != null && resolutionPragma.getCustomFeatMap().containsKey(picPosKey)) {
                                                    inheritance = GLComponent.Inheritance.DERIVED;
                                                } else if (intrface.getCommonFeatMap().containsKey(picPosKey)) {
                                                    inheritance = GLComponent.Inheritance.BASE;
                                                }
                                                Quad imgComp = new Quad(picPosKey, inheritance, width, height, tex, pos);
                                                picComps.add(imgComp);
                                            }
                                        } else if (splitW != 0.0f || splitH != 0.0f) {
                                            // pixel dimension
                                            width = Math.round(scaleXYFactor.getKey() * images[0].getWidth());
                                            height = Math.round(scaleXYFactor.getValue() * images[0].getHeight());
                                            // pixel position (picPosRect is already scaled)
                                            if (width > 0 && height > 0) {
                                                pos.x = picPosRect.minX + width / 2.0f;
                                                pos.y = picPosRect.minY + height / 2.0f;

                                                Vector2f posMax = new Vector2f(
                                                        picPosRect.maxX - width / 2.0f, picPosRect.maxY - height / 2.0f
                                                );

                                                // texture from loaded image
                                                Texture aqtex = Texture.loadTexture(iw.getStringValue(), gl20, images[0]);
                                                GLComponent.Inheritance inheritance = null;
                                                if (resolutionPragma != null && resolutionPragma.getCustomFeatMap().containsKey(picPosKey)) {
                                                    inheritance = GLComponent.Inheritance.DERIVED;
                                                } else if (intrface.getCommonFeatMap().containsKey(picPosKey)) {
                                                    inheritance = GLComponent.Inheritance.BASE;
                                                }
                                                AddressableQuad aq = new AddressableQuad(picPosKey, inheritance, width, height, aqtex, pos, splitW, splitH, posMax);
                                                picComps.add(aq);
                                            }
                                        } else {
                                            // pixel dimension
                                            width = picPosRect.lengthX();
                                            height = picPosRect.lengthY();

                                            if (width > 0 && height > 0) {
                                                // pixel position (picPosRect is already scaled)
                                                pos.x = (picPosRect.minX + picPosRect.maxX) / 2.0f;
                                                pos.y = (picPosRect.minY + picPosRect.maxY) / 2.0f;

                                                // Global Map special case
                                                if (mainPicPosVal != null && section.getSectionName() == Section.SectionName.GlobalMap) {
                                                    pos.x += mainPicPosVal.minX;
                                                    pos.y += mainPicPosVal.minY;
                                                }

                                                int index = 0;
                                                // array of textures for an animation
                                                final Texture[] texas = new Texture[images.length];
                                                for (BufferedImage image : images) {
                                                    texas[index] = Texture.loadTexture(iw.getStringValue() + index, gl20, image);
                                                    index++;
                                                }
                                                GLComponent.Inheritance inheritance = null;
                                                if (resolutionPragma != null && resolutionPragma.getCustomFeatMap().containsKey(picPosKey)) {
                                                    inheritance = GLComponent.Inheritance.DERIVED;
                                                } else if (intrface.getCommonFeatMap().containsKey(picPosKey)) {
                                                    inheritance = GLComponent.Inheritance.BASE;
                                                }
                                                Animation anim = new Animation(picPosKey, inheritance, iw.getFps(), width, height, texas, pos);
                                                picComps.add(anim);
                                            }
                                        }
                                    }
                                } else if (picVal != null) {
                                    FO2IELogger.reportWarning("Unexisting cast for ("
                                            + picKey.getStringValue() + ", " + picVal.getStringValue() + ")", null);
                                }
                            }

                            // missing picture -> question mark for missing..
                            if (pics.isEmpty()) {
                                if (splitW != 0.0f || splitH != 0.0f) {
                                    // pixel dimension
                                    int width = (splitW != 0.0f) ? Math.round(splitW) : picPosRect.lengthX();
                                    int height = (splitH != 0.0f) ? Math.round(splitH) : picPosRect.lengthY();
                                    // pixel position (picPosRect is already scaled)

                                    Vector2f pos = new Vector2f(
                                            picPosRect.minX + width / 2.0f,
                                            picPosRect.minY + height / 2.0f
                                    );

                                    Vector2f posMax = new Vector2f(
                                            picPosRect.maxX - width / 2.0f,
                                            picPosRect.maxY - height / 2.0f
                                    );

                                    // texture is missing texture
                                    Texture aqtex = Texture.loadLocalTexture(gl20, GUI.QMARK_PIC);
                                    GLComponent.Inheritance inheritance = null;
                                    if (resolutionPragma != null && resolutionPragma.getCustomFeatMap().containsKey(picPosKey)) {
                                        inheritance = GLComponent.Inheritance.DERIVED;
                                    } else if (intrface.getCommonFeatMap().containsKey(picPosKey)) {
                                        inheritance = GLComponent.Inheritance.BASE;
                                    }
                                    AddressableQuad aq = new AddressableQuad(picPosKey, inheritance, width, height, aqtex, pos, splitW, splitH, posMax);
                                    aq.setColor(intrface.getQmarkColor());
                                    picComps.add(aq);
                                } else {
                                    // position is always center (half sum of coords)
                                    Vector2f pos = new Vector2f(
                                            (picPosRect.minX + picPosRect.maxX) / 2.0f,
                                            (picPosRect.minY + picPosRect.maxY) / 2.0f
                                    );
                                    // Global Map special case
                                    if (mainPicPosVal != null && section.getSectionName() == Section.SectionName.GlobalMap) {
                                        pos.x += mainPicPosVal.minX;
                                        pos.y += mainPicPosVal.minY;
                                    }
                                    GLComponent.Inheritance inheritance = null;
                                    if (resolutionPragma != null && resolutionPragma.getCustomFeatMap().containsKey(picPosKey)) {
                                        inheritance = GLComponent.Inheritance.DERIVED;
                                    } else if (intrface.getCommonFeatMap().containsKey(picPosKey)) {
                                        inheritance = GLComponent.Inheritance.BASE;
                                    }
                                    Quad qmark = new Quad(
                                            picPosKey, inheritance, picPosRect.lengthX(), picPosRect.lengthY(),
                                            Texture.loadLocalTexture(gl20, GUI.QMARK_PIC), pos
                                    );
                                    qmark.setColor(intrface.getQmarkColor());
                                    picComps.add(qmark);
                                }

                            }

                        } else if (picPosVal != null) {
                            FO2IELogger.reportWarning("Unexisting cast for ("
                                    + picPosKey.getStringValue() + ", " + picPosVal.getStringValue() + ")", null);
                        }
                    }

                    oldProgress = progress;
                    progress += 50.0f / picPosValues.length;

                    firePropertyChange("progress", oldProgress, progress);
                }
            }

            FeatureKey[] textValues = section.getRoot().getTextValues();
            if (textValues != null) {
                for (FeatureKey txtKey : textValues) {
                    FeatureValue txtVal = resFeatMap.get(txtKey);
                    if (txtVal instanceof MyRectangle) {
                        MyRectangle textRect = (MyRectangle) txtVal;
                        MyRectangle temp = new MyRectangle();

                        // scale rectangle to the drawing surface
                        textRect = textRect.scaleXY(scaleXYFactor.getKey(), scaleXYFactor.getValue(), temp);

                        int width = textRect.lengthX();
                        int height = textRect.lengthY();

                        if (width > 0 && height > 0) {
                            // determine position of this picture
                            float posx = (textRect.minX + textRect.maxX) / 2.0f;
                            float posy = (textRect.minY + textRect.maxY) / 2.0f;

                            // Global Map special case
                            if (mainPicPosVal != null && section.getSectionName() == Section.SectionName.GlobalMap) {
                                posx += mainPicPosVal.minX;
                                posy += mainPicPosVal.minY;
                            }

                            // calc screen pixel coordinates
                            Vector2f pos = new Vector2f(posx, posy);

                            // this is text (primitive) overlay representing the area which text is populating
                            PrimitiveQuad txtOlay = new PrimitiveQuad(width, height, pos);
                            txtOlay.setColor(intrface.getTextOverlayColor());

                            // text display (content)
                            String regex = txtKey.getPrefix() + "|" + "Text";
                            String content = txtKey.getStringValue().replaceFirst(regex, "");

                            // font char dimensions
                            int fntWidth = Math.round(Text.STD_FONT_WIDTH * scaleXYFactor.getKey());
                            int fntHeight = Math.round(Text.STD_FONT_HEIGHT * scaleXYFactor.getValue());

                            // this is text component
                            GLComponent.Inheritance inheritance = null;
                            if (resolutionPragma != null && resolutionPragma.getCustomFeatMap().containsKey(txtKey)) {
                                inheritance = GLComponent.Inheritance.DERIVED;
                            } else if (intrface.getCommonFeatMap().containsKey(txtKey)) {
                                inheritance = GLComponent.Inheritance.BASE;
                            }
                            Text txt = new Text(txtKey, inheritance, fntTexture, content, pos, fntWidth, fntHeight);
                            txt.setColor(intrface.getTextColor());
                            txt.setAlignment(Text.ALIGNMENT_CENTER);
                            txt.getOverlay().setColor(intrface.getTextOverlayColor());
                            txt.getOverlay().setWidth(width);
                            txt.getOverlay().setHeight(height);
                            txt.setScale(0.67f * MathUtils.lerp(fntWidth, width, 0.75f) / (float) (fntWidth + width) + 0.33f * MathUtils.lerp(fntHeight, height, 0.75f) / (float) (fntHeight + height));
                            txtComps.add(txt);
                        }
                    } else if (txtVal != null) {
                        FO2IELogger.reportWarning("Unexisting cast for ("
                                + txtKey.getStringValue() + ", " + txtVal.getStringValue() + ")", null);
                    }

                    oldProgress = progress;
                    progress += 50.0f / textValues.length;

                    firePropertyChange("progress", oldProgress, progress);
                }
            }

            module.components.addAll(picComps);
            module.components.addAll(txtComps);
        }

        oldProgress = progress;
        progress = 100.0f;

        firePropertyChange("progress", oldProgress, progress);
//        module.components.addAll(result);
    }

    @Override
    protected Object doInBackground() throws Exception {
        switch (buildMode) {
            case ALL_RES:
                // take away context from module renderer
                gl20.getContext().makeCurrent();
                // build for all resolutions
                buildAllRes();
                // on finish release the context so module renderer can take it
                gl20.getContext().release();
                break;
            case TARGET_RES:
                // take away context from module renderer
                gl20.getContext().makeCurrent();
                // build for specific resolutions supported by the interface
                buildTargetRes();
                // on finish release the context so module renderer can take it
                gl20.getContext().release();
                break;
        }

        return null;
    }

    public Module getModule() {
        return module;
    }

    public Texture getFntTexture() {
        return fntTexture;
    }

    public Texture getUnusedTexture() {
        return unusedTexture;
    }

    public GL2 getGl20() {
        return gl20;
    }

    public SectionName getSectionName() {
        return sectionName;
    }

}
