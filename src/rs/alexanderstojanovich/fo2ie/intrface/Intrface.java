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
package rs.alexanderstojanovich.fo2ie.intrface;

import com.jogamp.opengl.GL2;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.editor.GUI;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.ImageWrapper;
import rs.alexanderstojanovich.fo2ie.feature.MyVector4;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.PrimitiveQuad;
import rs.alexanderstojanovich.fo2ie.ogl.Quad;
import rs.alexanderstojanovich.fo2ie.ogl.Text;
import rs.alexanderstojanovich.fo2ie.ogl.Texture;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.GLColor;
import rs.alexanderstojanovich.fo2ie.util.GLCoords;
import rs.alexanderstojanovich.fo2ie.util.MathUtils;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Intrface {

    private final StringBuilder errStrMsg = new StringBuilder();
    private int errorNum = 0;

    private final Configuration config = Configuration.getInstance();

    public static final String PIC_REGEX = "(Main|Green|Yellow|Red)?(Pic|Anim)(Dn|Dow|Off|Mask|Na)?";

    private final Vector4f textColor = GLColor.awtColorToVec4(config.getTxtCol());
    private final Vector4f textOverlayColor = GLColor.awtColorToVec4(config.getTxtOverlayCol());
    private final Vector4f qmarkColor = GLColor.awtColorToVec4(config.getQmarkCol());

    /**
     * Mode for reading {STD = standard - loading common values; RES =
     * resolution - loading from pragmas}
     */
    public static enum Mode {
        STD, RES
    }

    private boolean initialized = false;
    private Mode mode = Mode.STD;

    private final Section aim = new Section(Section.SectionName.Aim, FeatureKey.Aim.AimMainPic, FeatureKey.Aim.values());
    private final Section barter = new Section(Section.SectionName.Barter, FeatureKey.Barter.BarterMainPic, FeatureKey.Barter.values());
    private final Section character = new Section(Section.SectionName.Character, FeatureKey.Character.ChaMainPic, FeatureKey.Character.values());
    private final Section chosen = new Section(Section.SectionName.Chosen, FeatureKey.Chosen.ChosenTabPic, FeatureKey.Chosen.values());
    private final Section console = new Section(Section.SectionName.Console, FeatureKey.Console.ConsoleMainPic, FeatureKey.Console.values());
    private final Section dialogBox = new Section(Section.SectionName.DialogBox, FeatureKey.Dialog.DlgMainPic, FeatureKey.Dialog.values());
    private final Section fixBoy = new Section(Section.SectionName.FixBoy, FeatureKey.FixBoy.FixMainPic, FeatureKey.FixBoy.values());
    private final Section globalMap = new Section(Section.SectionName.GlobalMap, FeatureKey.GlobalMap.GmapMainPic, FeatureKey.GlobalMap.values());
    private final Section inputBox = new Section(Section.SectionName.InputBox, FeatureKey.InputBox.IboxMainPic, FeatureKey.InputBox.values());
    private final Section intrface = new Section(Section.SectionName.Intrface, FeatureKey.Interface.IntMainPic, FeatureKey.Interface.values());
    private final Section inventory = new Section(Section.SectionName.Inventory, FeatureKey.Inventory.InvMainPic, FeatureKey.Inventory.values());
    private final Section popUp = new Section(Section.SectionName.PopUp, FeatureKey.Radio.PriceSetup.PSMainPic, FeatureKey.PopUp.values());
    private final Section miniMap = new Section(Section.SectionName.MiniMap, FeatureKey.MiniMap.LmapMainPic, FeatureKey.MiniMap.values());
    private final Section login = new Section(Section.SectionName.Login, FeatureKey.Login.LogMainPic, FeatureKey.Login.values());
    private final Section options = new Section(Section.SectionName.Options, FeatureKey.Options.MoptMainPic, FeatureKey.Options.values());
    private final Section priceSetup = new Section(Section.SectionName.PriceSetup, FeatureKey.PriceSetup.PSMainPic, FeatureKey.PriceSetup.values());
    private final Section perk = new Section(Section.SectionName.Perk, FeatureKey.Perk.PerkMainPic, FeatureKey.Perk.values());
    private final Section pipBoy = new Section(Section.SectionName.PipBoy, FeatureKey.PipBoy.PipMainPic, FeatureKey.PipBoy.values());
    private final Section pickUp = new Section(Section.SectionName.PickUp, FeatureKey.PickUp.PupMainPic, FeatureKey.PipBoy.values());
    private final Section radio = new Section(Section.SectionName.Radio, FeatureKey.Radio.RadioMainPic, FeatureKey.Radio.values());
    private final Section registration = new Section(Section.SectionName.Registration, FeatureKey.Registration.RegMainPic, FeatureKey.Registration.values());
    private final Section saveLoad = new Section(Section.SectionName.SaveLoad, FeatureKey.SaveLoad.SaveLoadMainPic, FeatureKey.SaveLoad.values());
    private final Section sayBox = new Section(Section.SectionName.SayBox, FeatureKey.SayBox.SayMainPic, FeatureKey.SayBox.values());
    private final Section skillBox = new Section(Section.SectionName.SkillBox, FeatureKey.SkillBox.SboxMainPic, FeatureKey.SkillBox.values());
    private final Section split = new Section(Section.SectionName.Split, FeatureKey.Split.SplitMainPic, FeatureKey.Split.values());
    private final Section townView = new Section(Section.SectionName.TownView, FeatureKey.TownView.TViewMainPic, FeatureKey.TownView.values());
    private final Section timer = new Section(Section.SectionName.Timer, FeatureKey.Timer.TimerMainPic, FeatureKey.Timer.values());
    private final Section use = new Section(Section.SectionName.Use, FeatureKey.Use.UseMainPic, FeatureKey.Use.values());

    private final Map<SectionName, Section> nameToSectionMap = new HashMap<>();
    private final Map<Section, String> sectionToPrefixMap = new HashMap<>();

    protected final Map<FeatureKey, FeatureValue> commonFeatMap = new LinkedHashMap<>();
    private final List<ResolutionPragma> customResolutions = new ArrayList<>();

    private SectionName sectionName;
    private ResolutionPragma resolutionPragma;

    public Intrface() {
        initMap();
    }

    /**
     * Cover all the sections using the name map
     */
    private void initMap() {
        final Section[] sections = {
            aim, barter, character, chosen, console, dialogBox, fixBoy, globalMap,
            inputBox, intrface, inventory, popUp, miniMap, login, options, priceSetup, perk, pipBoy,
            pickUp, radio, registration, saveLoad, sayBox, skillBox, split,
            townView, timer, use
        };

        int index = 0;
        for (Section section : sections) {
            nameToSectionMap.put(section.getSectionName(), section);
            sectionToPrefixMap.put(section, FeatureKey.ABBRS[index]);
            index++;
        }

    }

    /**
     * Reads ini file and initializes the interface (check flag initialized to
     * see if it's successfully initialized)
     *
     * @return initialization ok status
     */
    public boolean readIniFile() {
        boolean ok = false;

        // set standard (default) mode
        mode = Mode.STD;
        // reset error number
        errorNum = 0;
        errStrMsg.setLength(0);

        // clear common and resolution sections
        commonFeatMap.clear();
        customResolutions.clear();

        FO2IELogger.reportInfo("Loading ini..", null);
        File inDir = Configuration.getInstance().getInDir();
        final File iniFile = new File(inDir.getPath() + File.separator + config.getDefaultIni());
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(iniFile));
            String line;
            ResolutionPragma rs = null;
            int lineNum = 0;
            // whilst the last line is not reached
            while ((line = br.readLine()) != null) {
                lineNum++;

                // removing single line comments
                String[] things = line.split("[#;]");
                if (things.length > 0) {
                    line = things[0];
                }

                // for now comments are ignored {#, ;} and so is the auto cursor
                if (!line.isEmpty() && !line.startsWith("autocursor")) {
                    // if word resolution occurs 
                    // switch mode, add it to custom resolution
                    // and take control over it
                    if (line.startsWith("resolution")) {
                        String[] res = line.split(" ");
                        int width = Integer.parseInt(res[1]);
                        int height = Integer.parseInt(res[2]);
                        mode = Mode.RES;

                        rs = new ResolutionPragma(
                                commonFeatMap,
                                width,
                                height
                        );
                        customResolutions.add(rs);
                        // in other case if line starts with =
                    } else if (line.contains("=")) {
                        String[] words = line.split("=");
                        words[0] = words[0].trim();
                        words[1] = words[1].trim();

                        FeatureKey fk = FeatureKey.valueOf(words[0]);
                        if (fk == null) {
                            errorNum++;
                            errStrMsg.append(lineNum).append(": @").append(words[0]).append("\n");
                            FO2IELogger.reportError(lineNum + ":" + " @" + words[0], null);
                        }
                        FeatureValue fv = FeatureValue.valueOf(words[1]);
                        if (fv == null) {
                            errorNum++;
                            errStrMsg.append(lineNum).append(": @").append(words[1]).append("\n");
                            FO2IELogger.reportError(lineNum + ":" + " @" + words[1], null);
                        }

                        if (fk != null && fv != null) {
                            switch (mode) {
                                case STD:
                                    commonFeatMap.put(fk, fv);
                                    break;
                                case RES:
                                    if (rs != null) {
                                        rs.getCustomFeatMap().put(fk, fv);
                                    }
                                    break;
                            }
                        }
                    }

                }

            }

            FO2IELogger.reportInfo("Loaded total common key/vals: " + commonFeatMap.size(), null);
            FO2IELogger.reportInfo("Total custom resolutions: " + customResolutions.size(), null);
            FO2IELogger.reportInfo("Total lines: " + lineNum, null);
            FO2IELogger.reportInfo("Total errors: " + errorNum, null);

            ok = true;
        } catch (FileNotFoundException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } catch (IOException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    FO2IELogger.reportError(ex.getMessage(), ex);
                }
            }
        }

        FO2IELogger.reportInfo("Loading ini finished!", null);
        if (ok && errorNum == 0) {
            FO2IELogger.reportInfo("Ini has been loaded succefully!", null);
        } else {
            FO2IELogger.reportInfo("Loading ini resulted in error!", null);
        }

        initialized = ok;

        return ok;
    }

    /**
     * Builds components list from common section based on all resolutions
     *
     * @param gl20 GL2 binding
     * @param fntTexture font texture for text rendering
     * @param unusedTexture unused texture (known as question mark texture)
     * @return built list from all the features containg OpenGL components
     * @throws java.io.IOException if building the module fails due to missing
     * image
     */
    public List<GLComponent> buildAllRes(GL2 gl20, Texture fntTexture, Texture unusedTexture) throws IOException {
        final int screenWidth = GUI.GL_CANVAS.getWidth();
        final int screenHeight = GUI.GL_CANVAS.getHeight();

        // final result is array list of components
        final List<GLComponent> result = new ArrayList<>();
        final Section section = this.nameToSectionMap.get(sectionName);
        if (section != null) {
            FeatureKey mainPicKey = section.root.getMainPic();

            // it's intially assumed that picture is 800x600 unless specified otherwise
            int mainPicWidth = 800;
            int mainPicHeight = 600;

            // if main picute exists (and in most cases it does apart from LMenu (known as pop-up menu)
            if (mainPicKey != null) {
                ImageWrapper mainPicVal = (ImageWrapper) commonFeatMap.get(mainPicKey);
                mainPicVal.loadImage();

                // texture for main picture
                Texture rootTex;
                // if main picture holds the image load the texture
                if (mainPicVal.getImages() != null && mainPicVal.getImages().length == 1) {
                    mainPicWidth = mainPicVal.getImages()[0].getWidth();
                    mainPicHeight = mainPicVal.getImages()[0].getHeight();
                    rootTex = Texture.loadTexture(mainPicVal.getStringValue(), gl20, mainPicVal.getImages()[0]);
                    // otherwise load missing question mark texture
                } else {
                    rootTex = Texture.loadLocalTexture(gl20, GUI.QMARK_PIC);
                }

                FeatureKey mainPicPosKey = mainPicKey.getMainPicPos();

                // defining root of the module (the main image)
                // all positions are referred to this root (image)
                Quad root;
                // if position exists for the main (root) image
                if (mainPicPosKey != null && commonFeatMap.containsKey(mainPicPosKey)) {
                    MyVector4 mainPicPosVal = (MyVector4) commonFeatMap.get(mainPicPosKey);
                    MyVector4 temp = new MyVector4();
                    mainPicPosVal = mainPicPosVal.setScaled(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);

                    // center is the mid point (essentially that's the formula)
                    float posx = (mainPicPosVal.x + mainPicPosVal.z) / 2.0f;
                    float posy = (mainPicPosVal.y + mainPicPosVal.w) / 2.0f;

                    Vector2f pos = new Vector2f(posx, posy);
                    Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);

                    // width and height are according to the corner coodinates substraction
                    int width = Math.round(mainPicPosVal.z - mainPicPosVal.x);
                    int height = Math.round(mainPicPosVal.w - mainPicPosVal.y);

                    root = new Quad(width, height, rootTex, posGL);
                    result.add(root);
                } else { // otherwise just make it size as read before and center it
                    float scMainPicWidth = MathUtils.getScaled(mainPicWidth, 0.0f, mainPicWidth, 0.0f, screenWidth);
                    float scMainPicHeight = MathUtils.getScaled(mainPicHeight, 0.0f, mainPicHeight, 0.0f, screenHeight);
                    root = new Quad(Math.round(scMainPicWidth), Math.round(scMainPicHeight), rootTex);
                    result.add(root);
                }
            }

            // defining mutually exclusive sets for pictures, primitives (overlays) and text
            final Set<GLComponent> picComps = new LinkedHashSet<>();
            final Set<GLComponent> prmComps = new LinkedHashSet<>();
            final Set<GLComponent> txtComps = new LinkedHashSet<>();

            // iterating through the section keys (left side of assignment of features)
            for (FeatureKey featKey : section.keys) {
                FeatureKey.Type fkType = featKey.getType();
                if (featKey != featKey.getMainPic()) {
                    switch (fkType) {
                        // if feat key is picture
                        case PIC:
                            FeatureValue picVal = commonFeatMap.get(featKey);
                            if (picVal instanceof ImageWrapper) {
                                ImageWrapper picWrap = (ImageWrapper) picVal;
                                String picPosStr = featKey.getStringValue().replaceAll(PIC_REGEX, "");
                                if (!picPosStr.equalsIgnoreCase("LogSinglePlayer")) {
                                    FeatureKey picPosKey = FeatureKey.valueOf(picPosStr);

                                    if (picPosKey != null) {
                                        FeatureValue picPosVal = commonFeatMap.get(picPosKey);
                                        if (picPosVal instanceof MyVector4) {
                                            MyVector4 picPosVec = (MyVector4) picPosVal;
                                            MyVector4 temp = new MyVector4();
                                            picPosVec = picPosVec.setScaled(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);

                                            float posx = (picPosVec.x + picPosVec.z) / 2.0f;
                                            float posy = (picPosVec.y + picPosVec.w) / 2.0f;

                                            Vector2f pos = new Vector2f(posx, posy);
                                            Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);

                                            int width = Math.round(picPosVec.z - picPosVec.x);
                                            int height = Math.round(picPosVec.w - picPosVec.y);

                                            // load image from filesystem to the memory
                                            picWrap.loadImage();
                                            // get array of images (in that case it's FRM) otherwise 
                                            // and in most case it's single image (.PNG for instance)
                                            BufferedImage[] images = picWrap.getImages();

                                            int index = 0;
                                            if (images != null) {
                                                for (BufferedImage image : images) {
                                                    Texture tex = Texture.loadTexture(picWrap.getStringValue() + index, gl20, image);
                                                    Quad imgComp = new Quad(width, height, tex, posGL);
                                                    picComps.add(imgComp);
                                                    index++;
                                                }
                                            }
                                        } else if (picPosVal != null) {
                                            FO2IELogger.reportWarning("Unexisting cast for ("
                                                    + featKey.getStringValue() + ", " + picPosVal.getStringValue() + ")", null);
                                        }

                                    }
                                }
                            } else if (picVal != null) {
                                FO2IELogger.reportWarning("Unexisting cast for ("
                                        + featKey.getStringValue() + ", " + picVal.getStringValue() + ")", null);
                            }
                            break;
                        // if feat key is picture positon
                        case PIC_POS:
                            FeatureValue picPosVal = commonFeatMap.get(featKey);
                            if (picPosVal instanceof MyVector4) {
                                MyVector4 picPosVec = (MyVector4) picPosVal;
                                MyVector4 temp = new MyVector4();
                                picPosVec = picPosVec.setScaled(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);
                                float posx = (picPosVec.x + picPosVec.z) / 2.0f;
                                float posy = (picPosVec.y + picPosVec.w) / 2.0f;
                                Vector2f pos = new Vector2f(posx, posy);
                                Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);
                                int width = Math.round(picPosVec.z - picPosVec.x);
                                int height = Math.round(picPosVec.w - picPosVec.y);
                                // gets the possible pictur keys
                                List<FeatureKey> pics = FeatureKey.getPics(featKey);
                                if (!pics.isEmpty()) {
                                    for (FeatureKey fkPic : pics) {
                                        if (fkPic != fkPic.getMainPic()) {
                                            FeatureValue fvPic = commonFeatMap.get(fkPic);
                                            if (fvPic instanceof ImageWrapper) {
                                                ImageWrapper picWrapX = (ImageWrapper) fvPic;
                                                picWrapX.loadImage();
                                                BufferedImage[] images = picWrapX.getImages();
                                                if (images != null) {
                                                    int index = 0;
                                                    for (BufferedImage image : images) {
                                                        Texture tex = Texture.loadTexture(picWrapX.getStringValue() + index, gl20, image);
                                                        Quad imgComp = new Quad(width, height, tex, posGL);
                                                        picComps.add(imgComp);
                                                        index++;
                                                    }
                                                }
                                            } else if (fvPic != null) {
                                                FO2IELogger.reportWarning("Unexisting cast for ("
                                                        + featKey.getStringValue() + ", " + fvPic.getStringValue() + ")", null);
                                            }
                                        }
                                    }
                                } else {
                                    Quad imgComp = new Quad(width, height, unusedTexture, posGL);
                                    imgComp.setColor(qmarkColor);
                                    picComps.add(imgComp);
                                }
                            } else if (picPosVal != null) {
                                FO2IELogger.reportWarning("Unexisting cast for ("
                                        + featKey.getStringValue() + ", " + picPosVal.getStringValue() + ")", null);
                            }
                            break;
                        // if feat key is text position
                        case TXT:
                            FeatureValue txtVal = commonFeatMap.get(featKey);
                            if (txtVal instanceof MyVector4) {
                                MyVector4 txtValVec = (MyVector4) txtVal;
                                MyVector4 ttemp = new MyVector4();
                                txtValVec = txtValVec.setScaled(mainPicWidth, mainPicHeight, screenWidth, screenHeight, ttemp);
                                float tposx = (txtValVec.x + txtValVec.z) / 2.0f;
                                float tposy = (txtValVec.y + txtValVec.w) / 2.0f;
                                Vector2f tpos = new Vector2f(tposx, tposy);
                                Vector2f tposGL = GLCoords.getOpenGLCoordinates(tpos, screenWidth, screenHeight);
                                int twidth = Math.round(txtValVec.z - txtValVec.x);
                                int theight = Math.round(txtValVec.w - txtValVec.y);
                                String regex = featKey.getPrefix() + "|" + "Text";
                                String text = featKey.getStringValue().replaceAll(regex, "");
                                // this is text (primitive) overlay representing the area which text is populating
                                PrimitiveQuad txtOlay = new PrimitiveQuad(twidth, theight, tposGL);
                                txtOlay.setColor(textOverlayColor);
                                // text is set to the default (font) texture
                                Text txtComp = new Text(fntTexture, text, textColor, tposGL);
                                txtComp.setAlignment(Text.ALIGNMENT_CENTER);
                                prmComps.add(txtOlay);
                                txtComps.add(txtComp);
                            } else if (txtVal != null) {
                                FO2IELogger.reportWarning("Unexisting cast for ("
                                        + featKey.getStringValue() + ", " + txtVal.getStringValue() + ")", null);
                            }
                            break;

                        default:
                            break;
                    }
                }
            }

            result.addAll(picComps);
            result.addAll(prmComps);
            result.addAll(txtComps);
        }

        return result;
    }

    /**
     * Builds components list from current section based on resolution
     *
     * @param gl20 GL2 binding
     * @param fntTexture font texture for text rendering
     * @param unusedTexture unused texture (known as question mark texture)
     * @return built list from all the features containg OpenGL components
     * @throws java.io.IOException if building the module fails due to missing
     * image
     */
    public List<GLComponent> buildTargetRes(GL2 gl20, Texture fntTexture, Texture unusedTexture) throws IOException {
        final int screenWidth = GUI.GL_CANVAS.getWidth();
        final int screenHeight = GUI.GL_CANVAS.getHeight();

        // final result is array list of components
        final List<GLComponent> result = new ArrayList<>();
        final Section section = this.nameToSectionMap.get(sectionName);
        if (section != null) {
            FeatureKey mainPicKey = section.root.getMainPic();

            // it's intially assumed that picture is 800x600 unless specified otherwise
            int mainPicWidth = 800;
            int mainPicHeight = 600;

            // if main picute exists (and in most cases it does apart from LMenu (known as pop-up menu)
            if (mainPicKey != null) {
                ImageWrapper mainPicVal = (ImageWrapper) resolutionPragma.customFeatMap.get(mainPicKey);
                mainPicVal.loadImage();

                // texture for main picture
                Texture rootTex;
                // if main picture holds the image load the texture
                if (mainPicVal.getImages() != null && mainPicVal.getImages().length == 1) {
                    mainPicWidth = mainPicVal.getImages()[0].getWidth();
                    mainPicHeight = mainPicVal.getImages()[0].getHeight();
                    rootTex = Texture.loadTexture(mainPicVal.getStringValue(), gl20, mainPicVal.getImages()[0]);
                    // otherwise load missing question mark texture
                } else {
                    rootTex = Texture.loadLocalTexture(gl20, GUI.QMARK_PIC);
                }

                FeatureKey mainPicPosKey = mainPicKey.getMainPicPos();

                // defining root of the module (the main image)
                // all positions are referred to this root (image)
                Quad root;
                // if position exists for the main (root) image
                if (mainPicPosKey != null && resolutionPragma.customFeatMap.containsKey(mainPicPosKey)) {
                    MyVector4 mainPicPosVal = (MyVector4) resolutionPragma.customFeatMap.get(mainPicPosKey);
                    MyVector4 temp = new MyVector4();
                    mainPicPosVal = mainPicPosVal.setScaled(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);

                    // center is the mid point (essentially that's the formula)
                    float posx = (mainPicPosVal.x + mainPicPosVal.z) / 2.0f;
                    float posy = (mainPicPosVal.y + mainPicPosVal.w) / 2.0f;

                    Vector2f pos = new Vector2f(posx, posy);
                    Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);

                    // width and height are according to the corner coodinates substraction
                    int width = Math.round(mainPicPosVal.z - mainPicPosVal.x);
                    int height = Math.round(mainPicPosVal.w - mainPicPosVal.y);

                    root = new Quad(width, height, rootTex, posGL);
                    result.add(root);
                } else { // otherwise just make it size as read before and center it
                    float scMainPicWidth = MathUtils.getScaled(mainPicWidth, 0.0f, mainPicWidth, 0.0f, screenWidth);
                    float scMainPicHeight = MathUtils.getScaled(mainPicHeight, 0.0f, mainPicHeight, 0.0f, screenHeight);
                    root = new Quad(Math.round(scMainPicWidth), Math.round(scMainPicHeight), rootTex);
                    result.add(root);
                }
            }

            // defining mutually exclusive sets for pictures, primitives (overlays) and text
            final Set<GLComponent> picComps = new LinkedHashSet<>();
            final Set<GLComponent> prmComps = new LinkedHashSet<>();
            final Set<GLComponent> txtComps = new LinkedHashSet<>();

            // iterating through the section keys (left side of assignment of features)
            for (FeatureKey featKey : section.keys) {
                FeatureKey.Type fkType = featKey.getType();
                if (featKey != featKey.getMainPic()) {
                    switch (fkType) {
                        // if feat key is picture
                        case PIC:
                            FeatureValue picVal = resolutionPragma.customFeatMap.get(featKey);
                            if (picVal instanceof ImageWrapper) {
                                ImageWrapper picWrap = (ImageWrapper) picVal;
                                String picPosStr = featKey.getStringValue().replaceAll(PIC_REGEX, "");
                                if (!picPosStr.equalsIgnoreCase("LogSinglePlayer")) {
                                    FeatureKey picPosKey = FeatureKey.valueOf(picPosStr);

                                    if (picPosKey != null) {
                                        FeatureValue picPosVal = resolutionPragma.customFeatMap.get(picPosKey);
                                        if (picPosVal instanceof MyVector4) {
                                            MyVector4 picPosVec = (MyVector4) picPosVal;
                                            MyVector4 temp = new MyVector4();
                                            picPosVec = picPosVec.setScaled(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);

                                            float posx = (picPosVec.x + picPosVec.z) / 2.0f;
                                            float posy = (picPosVec.y + picPosVec.w) / 2.0f;

                                            Vector2f pos = new Vector2f(posx, posy);
                                            Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);

                                            int width = Math.round(picPosVec.z - picPosVec.x);
                                            int height = Math.round(picPosVec.w - picPosVec.y);

                                            // load image from filesystem to the memory
                                            picWrap.loadImage();
                                            // get array of images (in that case it's FRM) otherwise 
                                            // and in most case it's single image (.PNG for instance)
                                            BufferedImage[] images = picWrap.getImages();

                                            int index = 0;
                                            if (images != null) {
                                                for (BufferedImage image : images) {
                                                    Texture tex = Texture.loadTexture(picWrap.getStringValue() + index, gl20, image);
                                                    Quad imgComp = new Quad(width, height, tex, posGL);
                                                    picComps.add(imgComp);
                                                    index++;
                                                }
                                            }
                                        } else if (picPosVal != null) {
                                            FO2IELogger.reportWarning("Unexisting cast for ("
                                                    + featKey.getStringValue() + ", " + picPosVal.getStringValue() + ")", null);
                                        }

                                    }
                                }
                            } else if (picVal != null) {
                                FO2IELogger.reportWarning("Unexisting cast for ("
                                        + featKey.getStringValue() + ", " + picVal.getStringValue() + ")", null);
                            }
                            break;
                        // if feat key is picture positon
                        case PIC_POS:
                            FeatureValue picPosVal = resolutionPragma.customFeatMap.get(featKey);
                            if (picPosVal instanceof MyVector4) {
                                MyVector4 picPosVec = (MyVector4) picPosVal;
                                MyVector4 temp = new MyVector4();
                                picPosVec = picPosVec.setScaled(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);
                                float posx = (picPosVec.x + picPosVec.z) / 2.0f;
                                float posy = (picPosVec.y + picPosVec.w) / 2.0f;
                                Vector2f pos = new Vector2f(posx, posy);
                                Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);
                                int width = Math.round(picPosVec.z - picPosVec.x);
                                int height = Math.round(picPosVec.w - picPosVec.y);
                                // gets the possible pictur keys
                                List<FeatureKey> pics = FeatureKey.getPics(featKey);
                                if (!pics.isEmpty()) {
                                    for (FeatureKey fkPic : pics) {
                                        if (fkPic != fkPic.getMainPic()) {
                                            FeatureValue fvPic = resolutionPragma.customFeatMap.get(fkPic);
                                            if (fvPic instanceof ImageWrapper) {
                                                ImageWrapper picWrapX = (ImageWrapper) fvPic;
                                                picWrapX.loadImage();
                                                BufferedImage[] images = picWrapX.getImages();
                                                if (images != null) {
                                                    int index = 0;
                                                    for (BufferedImage image : images) {
                                                        Texture tex = Texture.loadTexture(picWrapX.getStringValue() + index, gl20, image);
                                                        Quad imgComp = new Quad(width, height, tex, posGL);
                                                        picComps.add(imgComp);
                                                        index++;
                                                    }
                                                }
                                            } else if (fvPic != null) {
                                                FO2IELogger.reportWarning("Unexisting cast for ("
                                                        + featKey.getStringValue() + ", " + fvPic.getStringValue() + ")", null);
                                            }
                                        }
                                    }
                                } else {
                                    Quad imgComp = new Quad(width, height, unusedTexture, posGL);
                                    imgComp.setColor(qmarkColor);
                                    picComps.add(imgComp);
                                }
                            } else if (picPosVal != null) {
                                FO2IELogger.reportWarning("Unexisting cast for ("
                                        + featKey.getStringValue() + ", " + picPosVal.getStringValue() + ")", null);
                            }
                            break;
                        // if feat key is text position
                        case TXT:
                            FeatureValue txtVal = resolutionPragma.customFeatMap.get(featKey);
                            if (txtVal instanceof MyVector4) {
                                MyVector4 txtValVec = (MyVector4) txtVal;
                                MyVector4 ttemp = new MyVector4();
                                txtValVec = txtValVec.setScaled(mainPicWidth, mainPicHeight, screenWidth, screenHeight, ttemp);
                                float tposx = (txtValVec.x + txtValVec.z) / 2.0f;
                                float tposy = (txtValVec.y + txtValVec.w) / 2.0f;
                                Vector2f tpos = new Vector2f(tposx, tposy);
                                Vector2f tposGL = GLCoords.getOpenGLCoordinates(tpos, screenWidth, screenHeight);
                                int twidth = Math.round(txtValVec.z - txtValVec.x);
                                int theight = Math.round(txtValVec.w - txtValVec.y);
                                String regex = featKey.getPrefix() + "|" + "Text";
                                String text = featKey.getStringValue().replaceAll(regex, "");
                                // this is text (primitive) overlay representing the area which text is populating
                                PrimitiveQuad txtOlay = new PrimitiveQuad(twidth, theight, tposGL);
                                txtOlay.setColor(textOverlayColor);
                                // text is set to the default (font) texture
                                Text txtComp = new Text(fntTexture, text, textColor, tposGL);
                                txtComp.setAlignment(Text.ALIGNMENT_CENTER);
                                prmComps.add(txtOlay);
                                txtComps.add(txtComp);
                            } else if (txtVal != null) {
                                FO2IELogger.reportWarning("Unexisting cast for ("
                                        + featKey.getStringValue() + ", " + txtVal.getStringValue() + ")", null);
                            }
                            break;

                        default:
                            break;
                    }
                }
            }

            result.addAll(picComps);
            result.addAll(prmComps);
            result.addAll(txtComps);
        }

        return result;
    }

    public Mode getMode() {
        return mode;
    }

    public Section getAim() {
        return aim;
    }

    public Section getBarter() {
        return barter;
    }

    public Section getCharacter() {
        return character;
    }

    public Section getChosen() {
        return chosen;
    }

    public Section getConsole() {
        return console;
    }

    public Section getDialogBox() {
        return dialogBox;
    }

    public Section getFixBoy() {
        return fixBoy;
    }

    public Section getGlobalMap() {
        return globalMap;
    }

    public Section getInputBox() {
        return inputBox;
    }

    public Section getIntrface() {
        return intrface;
    }

    public Section getInventory() {
        return inventory;
    }

    public Section getPopUp() {
        return popUp;
    }

    public Section getMiniMap() {
        return miniMap;
    }

    public Section getLogin() {
        return login;
    }

    public Section getOptions() {
        return options;
    }

    public Section getPriceSetup() {
        return priceSetup;
    }

    public Section getPerk() {
        return perk;
    }

    public Section getPipBoy() {
        return pipBoy;
    }

    public Section getPickUp() {
        return pickUp;
    }

    public Section getRadio() {
        return radio;
    }

    public Section getRegistration() {
        return registration;
    }

    public Section getSaveLoad() {
        return saveLoad;
    }

    public Section getSayBox() {
        return sayBox;
    }

    public Section getSkillBox() {
        return skillBox;
    }

    public Section getSplit() {
        return split;
    }

    public Section getTownView() {
        return townView;
    }

    public Section getTimer() {
        return timer;
    }

    public Section getUse() {
        return use;
    }

    public Map<SectionName, Section> getNameToSectionMap() {
        return nameToSectionMap;
    }

    public Map<Section, String> getSectionToPrefixMap() {
        return sectionToPrefixMap;
    }

    public Map<FeatureKey, FeatureValue> getCommonFeatMap() {
        return commonFeatMap;
    }

    public List<ResolutionPragma> getCustomResolutions() {
        return customResolutions;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public SectionName getSectionName() {
        return sectionName;
    }

    public void setSectionName(SectionName sectionName) {
        this.sectionName = sectionName;
    }

    public ResolutionPragma getResolutionPragma() {
        return resolutionPragma;
    }

    public void setResolutionPragma(ResolutionPragma resolutionPragma) {
        this.resolutionPragma = resolutionPragma;
    }

    public Configuration getConfig() {
        return config;
    }

    public int getErrorNum() {
        return errorNum;
    }

    public StringBuilder getErrStrMsg() {
        return errStrMsg;
    }

    public static String getPIC_REGEX() {
        return PIC_REGEX;
    }

    public Vector4f getTextColor() {
        return textColor;
    }

    public Vector4f getTextOverlayColor() {
        return textOverlayColor;
    }

    public Vector4f getQmarkColor() {
        return qmarkColor;
    }

}
