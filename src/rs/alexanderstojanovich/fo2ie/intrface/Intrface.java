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
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.ImageWrapper;
import rs.alexanderstojanovich.fo2ie.feature.MyRectangle;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.main.GUI;
import rs.alexanderstojanovich.fo2ie.ogl.Animation;
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

    private int mainPicWidth = 800;
    private int mainPicHeight = 600;

    private final StringBuilder errStrMsg = new StringBuilder();
    private int errorNum = 0;

    private final Configuration config = Configuration.getInstance();

    public static final String PIC_REGEX = "(Main|Green|Yellow|Red)?(Pic|Anim)(Dn|Dow|Off|Mask|Na)?";

    private final Vector4f textColor = GLColor.awtColorToVec4(config.getTxtCol());
    private final Vector4f textOverlayColor = GLColor.awtColorToVec4(config.getTxtOverlayCol());
    private final Vector4f qmarkColor = GLColor.awtColorToVec4(config.getQmarkCol());

    /**
     * ReadMode for reading {STD = standard - loading common values; RES =
     * resolution - loading from pragmas}
     */
    public static enum ReadMode {
        STD, RES
    }

    /*{FROM COMMON MAP (ALL RES), FROM RES PRAGMA
     * (TARGET RES}
     */
    public static enum BuildMode {
        NONE, COMMON, PRAGMA
    }

    private boolean initialized = false;
    private ReadMode readMode = ReadMode.STD;
    private BuildMode buildMode = BuildMode.NONE;

    private final Section aim = new Section(Section.SectionName.Aim, FeatureKey.Aim.AimMainPic, FeatureKey.Aim.values());
    private final Section barter = new Section(Section.SectionName.Barter, FeatureKey.Barter.BarterMainPic, FeatureKey.Barter.values());
    private final Section character = new Section(Section.SectionName.Character, FeatureKey.Character.ChaMainPic, FeatureKey.Character.values());
    private final Section chosen = new Section(Section.SectionName.Chosen, FeatureKey.Chosen.ChosenTabPic, FeatureKey.Chosen.values());
    private final Section console = new Section(Section.SectionName.Console, FeatureKey.Console.ConsoleMainPic, FeatureKey.Console.values());
    private final Section dialogBox = new Section(Section.SectionName.DialogBox, FeatureKey.Dialog.DlgMainPic, FeatureKey.Dialog.values());
    private final Section faction = new Section(Section.SectionName.Faction, FeatureKey.Faction.FactionMainPic, FeatureKey.Faction.values());
    private final Section fixBoy = new Section(Section.SectionName.FixBoy, FeatureKey.FixBoy.FixMainPic, FeatureKey.FixBoy.values());
    private final Section globalMap = new Section(Section.SectionName.GlobalMap, FeatureKey.GlobalMap.GmapMainPic, FeatureKey.GlobalMap.values());
    private final Section groundPickup = new Section(Section.SectionName.GroundPickup, FeatureKey.GroundPickup.GPickupMainPic, FeatureKey.GroundPickup.values());
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
    private final Section pickUp = new Section(Section.SectionName.PickUp, FeatureKey.PickUp.PupMainPic, FeatureKey.PickUp.values());
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

    private float progress = 0.0f;

    public Intrface() {
        initMap();
    }

    /**
     * Cover all the sections using the name map
     */
    private void initMap() {
        final Section[] sections = {
            aim, barter, character, chosen, console, dialogBox, faction, fixBoy, globalMap, groundPickup,
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
     * Default ini file will be used (set in configuration)
     *
     * @return initialization ok status
     */
    public boolean readIniFile() {
        boolean ok = false;

        FO2IELogger.reportInfo("Loading ini..", null);
        File inDir = Configuration.getInstance().getInDir();
        final File iniFile = new File(inDir.getPath() + File.separator + config.getDefaultIni());

        if (!iniFile.exists()) {
            FO2IELogger.reportInfo("Loading ini finished!", null);
            FO2IELogger.reportInfo("Loading ini resulted in error!", null);
            return ok;
        }

        // set standard (default) readMode
        readMode = ReadMode.STD;
        // reset error number
        errorNum = 0;
        errStrMsg.setLength(0);

        // clear common and resolution sections
        commonFeatMap.clear();
        customResolutions.clear();

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
                    // switch readMode, add it to custom resolution
                    // and take control over it
                    if (line.startsWith("resolution")) {
                        String[] res = line.split(" ");
                        int width = Integer.parseInt(res[1]);
                        int height = Integer.parseInt(res[2]);
                        readMode = ReadMode.RES;

                        rs = new ResolutionPragma(
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
                            switch (readMode) {
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

        buildMode = BuildMode.NONE;

        return ok;
    }

    /**
     * Reads ini file and initializes the interface (check flag initialized to
     * see if it's successfully initialized)
     *
     * @param iniFile ini file of the interface
     * @return initialization ok status
     */
    public boolean readIniFile(File iniFile) {
        boolean ok = false;

        if (!iniFile.exists()) {
            FO2IELogger.reportInfo("Loading ini finished!", null);
            FO2IELogger.reportInfo("Loading ini resulted in error!", null);
            return ok;
        }

        // set standard (default) readMode
        readMode = ReadMode.STD;
        // reset error number
        errorNum = 0;
        errStrMsg.setLength(0);

        // clear common and resolution sections
        commonFeatMap.clear();
        customResolutions.clear();

        FO2IELogger.reportInfo("Loading ini..", null);

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
                    // switch readMode, add it to custom resolution
                    // and take control over it
                    if (line.startsWith("resolution")) {
                        String[] res = line.split(" ");
                        int width = Integer.parseInt(res[1]);
                        int height = Integer.parseInt(res[2]);
                        readMode = ReadMode.RES;

                        rs = new ResolutionPragma(
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
                            switch (readMode) {
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
     * @return built list from all the features containing OpenGL components
     * @throws java.io.IOException if building the module fails due to missing
     * image
     */
    public List<GLComponent> buildAllRes(GL2 gl20, Texture fntTexture, Texture unusedTexture) throws IOException {
        progress = 0.0f;

        buildMode = BuildMode.COMMON;

        final int screenWidth = GUI.GL_CANVAS.getWidth();
        final int screenHeight = GUI.GL_CANVAS.getHeight();

        // final result is array list of components
        final List<GLComponent> result = new ArrayList<>();
        final Section section = this.nameToSectionMap.get(sectionName);
        if (section != null) {
            FeatureKey mainPicKey = section.root.getMainPic();
            MyRectangle mainPicPosVal = null;

            // it's intially assumed that picture is 800x600 unless specified otherwise
            mainPicWidth = 800;
            mainPicHeight = 600;

            // if main picute exists (and in most cases it does apart from LMenu (known as pop-up menu)
            if (mainPicKey != null && commonFeatMap.containsKey(mainPicKey)) {
                ImageWrapper mainPicVal = (ImageWrapper) commonFeatMap.get(mainPicKey);
                mainPicVal.loadImages();

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
                // if position exists for the main (root) image
                if (mainPicPosKey != null && commonFeatMap.containsKey(mainPicPosKey)) {
                    mainPicPosVal = (MyRectangle) commonFeatMap.get(mainPicPosKey);
                    MyRectangle temp = new MyRectangle();
                    mainPicPosVal = mainPicPosVal.scalex(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);
                }

                float scMainPicWidth = MathUtils.getScaled(mainPicWidth, 0.0f, mainPicWidth, 0.0f, screenWidth);
                float scMainPicHeight = MathUtils.getScaled(mainPicHeight, 0.0f, mainPicHeight, 0.0f, screenHeight);
                Quad root = new Quad(mainPicPosKey, Math.round(scMainPicWidth), Math.round(scMainPicHeight), rootTex);
                result.add(root);
            }

            // defining mutually exclusive Lists for pictures, primitives (overlays) and text
            final List<GLComponent> picComps = new ArrayList<>();
            final List<GLComponent> txtComps = new ArrayList<>();

            for (FeatureKey picPosKey : section.picpos) {
                if (picPosKey != section.root.getMainPicPos()) {
                    FeatureValue picPosVal = commonFeatMap.get(picPosKey);
                    if (picPosVal instanceof MyRectangle) {
                        MyRectangle picPosRect = (MyRectangle) picPosVal;
                        MyRectangle temp = new MyRectangle();

                        // scale rectangle to the drawing surface
                        picPosRect = picPosRect.scalex(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);

                        // determine position of this picture
                        float posx = (picPosRect.minX + picPosRect.maxX) / 2.0f;
                        float posy = (picPosRect.minY + picPosRect.maxY) / 2.0f;

                        // apply shift if Global Map (only Global Map has such a property)
                        if (mainPicPosVal != null && section.sectionName == SectionName.GlobalMap) {
                            posx += mainPicPosVal.minX;
                            posy += mainPicPosVal.minY;
                        }

                        // calc OpenGL coordinates
                        Vector2f pos = new Vector2f(posx, posy);
                        Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);

                        List<FeatureKey> pics = FeatureKey.getPics(picPosKey);
                        for (FeatureKey picKey : pics) {
                            FeatureValue picVal = commonFeatMap.get(picKey);
                            if (picVal instanceof ImageWrapper) {
                                ImageWrapper iw = (ImageWrapper) picVal;
                                iw.loadImages();
                                BufferedImage[] images = iw.getImages();
                                if (images != null && images.length > 0) {
                                    int width, height; // dimension of picture/animation in pixels
                                    if (images.length == 1) {
                                        Texture tex = Texture.loadTexture(iw.getStringValue(), gl20, images[0]);
                                        width = Math.round(images[0].getWidth() * screenWidth / (float) mainPicWidth);
                                        height = Math.round(images[0].getHeight() * screenHeight / (float) mainPicHeight);
                                        Quad imgComp = new Quad(picPosKey, width, height, tex, posGL);
                                        picComps.add(imgComp);
                                    } else {
                                        int index = 0;
                                        final Texture[] texas = new Texture[images.length];
                                        for (BufferedImage image : images) {
                                            texas[index] = Texture.loadTexture(iw.getStringValue() + index, gl20, image);
                                            index++;
                                        }
                                        width = picPosRect.lengthX();
                                        height = picPosRect.lengthY();
                                        Animation anim = new Animation(picPosKey, iw.getFps(), width, height, texas, posGL);
                                        picComps.add(anim);
                                    }
                                }
                            } else if (picVal != null) {
                                FO2IELogger.reportWarning("Unexisting cast for ("
                                        + picKey.getStringValue() + ", " + picVal.getStringValue() + ")", null);
                            }
                        }

                    } else if (picPosVal != null) {
                        FO2IELogger.reportWarning("Unexisting cast for ("
                                + picPosKey.getStringValue() + ", " + picPosVal.getStringValue() + ")", null);
                    }
                }

                progress += 50.0f / section.picpos.size();
            }

            for (FeatureKey txtKey : section.text) {
                FeatureValue txtVal = commonFeatMap.get(txtKey);
                if (txtVal instanceof MyRectangle) {
                    MyRectangle textRect = (MyRectangle) txtVal;
                    MyRectangle temp = new MyRectangle();

                    // scale rectangle to the drawing surface
                    textRect = textRect.scalex(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);

                    int width = textRect.lengthX();
                    int height = textRect.lengthY();

                    // determine position of this picture
                    float posx = (textRect.minX + textRect.maxX) / 2.0f;
                    float posy = (textRect.minY + textRect.maxY) / 2.0f;

                    // apply shift if Global Map (only Global Map has such a property)
                    if (mainPicPosVal != null && section.sectionName == SectionName.GlobalMap) {
                        posx += mainPicPosVal.minX;
                        posy += mainPicPosVal.minY;
                    }

                    // calc OpenGL coordinates
                    Vector2f pos = new Vector2f(posx, posy);
                    Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);

                    // this is text (primitive) overlay representing the area which text is populating
                    PrimitiveQuad txtOlay = new PrimitiveQuad(width, height, posGL);
                    txtOlay.setColor(textOverlayColor);

                    // text display (content)
                    String regex = txtKey.getPrefix() + "|" + "Text";
                    String content = txtKey.getStringValue().replaceAll(regex, "");

                    // font char dimensions
                    int fntWidth = Math.round(Text.STD_FONT_WIDTH * screenWidth / (float) mainPicWidth);
                    int fntHeight = Math.round(Text.STD_FONT_HEIGHT * screenHeight / (float) mainPicHeight);

                    // this is text component                    
                    Text txt = new Text(txtKey, fntTexture, content, posGL, fntWidth, fntHeight);
                    txt.setColor(textColor);
                    txt.setAlignment(Text.ALIGNMENT_CENTER);
                    txt.getOverlay().setColor(textOverlayColor);
                    txt.getOverlay().setWidth(width);
                    txt.getOverlay().setHeight(height);
                    txtComps.add(txt);
                } else if (txtVal != null) {
                    FO2IELogger.reportWarning("Unexisting cast for ("
                            + txtKey.getStringValue() + ", " + txtVal.getStringValue() + ")", null);
                }

                progress += 50.0f / section.text.size();
            }

            result.addAll(picComps);
            result.addAll(txtComps);
        }

        progress = 100.0f;
        return result;
    }

    /**
     * Builds components list from current section based on resolution
     *
     * @param gl20 GL2 binding
     * @param fntTexture font texture for text rendering
     * @param unusedTexture unused texture (known as question mark texture)
     * @return built list from all the features containing OpenGL components
     * @throws java.io.IOException if building the module fails due to missing
     * image
     */
    public List<GLComponent> buildTargetRes(GL2 gl20, Texture fntTexture, Texture unusedTexture) throws IOException {
        progress = 0.0f;
        final Map<FeatureKey, FeatureValue> resFeatMap = new HashMap<>(commonFeatMap);

        buildMode = BuildMode.PRAGMA;

        if (resolutionPragma != null) {
            for (FeatureKey custKey : resolutionPragma.customFeatMap.keySet()) {
                FeatureValue custVal = resolutionPragma.customFeatMap.get(custKey);
                if (resFeatMap.containsKey(custKey)) {
                    resFeatMap.replace(custKey, custVal);
                } else {
                    resFeatMap.put(custKey, custVal);
                }
            }
        }

        final int screenWidth = GUI.GL_CANVAS.getWidth();
        final int screenHeight = GUI.GL_CANVAS.getHeight();

        // final result is array list of components
        final List<GLComponent> result = new ArrayList<>();
        final Section section = this.nameToSectionMap.get(sectionName);
        if (section != null) {
            FeatureKey mainPicKey = section.root.getMainPic();
            MyRectangle mainPicPosVal = null;

            // it's intially assumed that picture is 800x600 unless specified otherwise
            mainPicWidth = 800;
            mainPicHeight = 600;

            // if main picute exists (and in most cases it does apart from LMenu (known as pop-up menu)
            if (mainPicKey != null && resFeatMap.containsKey(mainPicKey)) {
                ImageWrapper mainPicVal = (ImageWrapper) resFeatMap.get(mainPicKey);
                mainPicVal.loadImages();

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
                // if position exists for the main (root) image
                if (mainPicPosKey != null && resFeatMap.containsKey(mainPicPosKey)) {
                    mainPicPosVal = (MyRectangle) resFeatMap.get(mainPicPosKey);
                    MyRectangle temp = new MyRectangle();
                    mainPicPosVal = mainPicPosVal.scalex(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);
                }

                float scMainPicWidth = MathUtils.getScaled(mainPicWidth, 0.0f, mainPicWidth, 0.0f, screenWidth);
                float scMainPicHeight = MathUtils.getScaled(mainPicHeight, 0.0f, mainPicHeight, 0.0f, screenHeight);
                Quad root = new Quad(mainPicPosKey, Math.round(scMainPicWidth), Math.round(scMainPicHeight), rootTex);
                result.add(root);
            }

            // defining mutually exclusive Lists for pictures, primitives (overlays) and text
            final List<GLComponent> picComps = new ArrayList<>();
            final List<GLComponent> txtComps = new ArrayList<>();

            for (FeatureKey picPosKey : section.picpos) {
                if (picPosKey != section.root.getMainPicPos()) {
                    FeatureValue picPosVal = resFeatMap.get(picPosKey);
                    if (picPosVal instanceof MyRectangle) {
                        MyRectangle picPosRect = (MyRectangle) picPosVal;
                        MyRectangle temp = new MyRectangle();

                        // scale rectangle to the drawing surface
                        picPosRect = picPosRect.scalex(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);

                        // determine position of this picture
                        float posx = (picPosRect.minX + picPosRect.maxX) / 2.0f;
                        float posy = (picPosRect.minY + picPosRect.maxY) / 2.0f;

                        // apply shift if Global Map (only Global Map has such a property)
                        if (mainPicPosVal != null && section.sectionName == SectionName.GlobalMap) {
                            posx += mainPicPosVal.minX;
                            posy += mainPicPosVal.minY;
                        }

                        // calc OpenGL coordinates
                        Vector2f pos = new Vector2f(posx, posy);
                        Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);

                        List<FeatureKey> pics = FeatureKey.getPics(picPosKey);
                        for (FeatureKey picKey : pics) {
                            FeatureValue picVal = resFeatMap.get(picKey);
                            if (picVal instanceof ImageWrapper) {
                                ImageWrapper iw = (ImageWrapper) picVal;
                                iw.loadImages();
                                BufferedImage[] images = iw.getImages();
                                if (images != null && images.length > 0) {
                                    int width, height; // dimension of picture/animation in pixels
                                    if (images.length == 1) {
                                        Texture tex = Texture.loadTexture(iw.getStringValue(), gl20, images[0]);
                                        width = Math.round(images[0].getWidth() * screenWidth / (float) mainPicWidth);
                                        height = Math.round(images[0].getHeight() * screenHeight / (float) mainPicHeight);
                                        Quad imgComp = new Quad(picPosKey, width, height, tex, posGL);
                                        picComps.add(imgComp);
                                    } else {
                                        int index = 0;
                                        final Texture[] texas = new Texture[images.length];
                                        for (BufferedImage image : images) {
                                            texas[index] = Texture.loadTexture(iw.getStringValue() + index, gl20, image);
                                            index++;
                                        }
                                        width = picPosRect.lengthX();
                                        height = picPosRect.lengthY();
                                        Animation anim = new Animation(picPosKey, iw.getFps(), width, height, texas, posGL);
                                        picComps.add(anim);
                                    }
                                }
                            } else if (picVal != null) {
                                FO2IELogger.reportWarning("Unexisting cast for ("
                                        + picKey.getStringValue() + ", " + picVal.getStringValue() + ")", null);
                            }
                        }

                    } else if (picPosVal != null) {
                        FO2IELogger.reportWarning("Unexisting cast for ("
                                + picPosKey.getStringValue() + ", " + picPosVal.getStringValue() + ")", null);
                    }
                }

                progress += 50.0f / section.picpos.size();
            }

            for (FeatureKey txtKey : section.text) {
                FeatureValue txtVal = resFeatMap.get(txtKey);
                if (txtVal instanceof MyRectangle) {
                    MyRectangle textRect = (MyRectangle) txtVal;
                    MyRectangle temp = new MyRectangle();

                    // scale rectangle to the drawing surface
                    textRect = textRect.scalex(mainPicWidth, mainPicHeight, screenWidth, screenHeight, temp);

                    int width = textRect.lengthX();
                    int height = textRect.lengthY();

                    // determine position of this picture
                    float posx = (textRect.minX + textRect.maxX) / 2.0f;
                    float posy = (textRect.minY + textRect.maxY) / 2.0f;

                    // apply shift if Global Map (only Global Map has such a property)
                    if (mainPicPosVal != null && section.sectionName == SectionName.GlobalMap) {
                        posx += mainPicPosVal.minX;
                        posy += mainPicPosVal.minY;
                    }

                    // calc OpenGL coordinates
                    Vector2f pos = new Vector2f(posx, posy);
                    Vector2f posGL = GLCoords.getOpenGLCoordinates(pos, screenWidth, screenHeight);

                    // this is text (primitive) overlay representing the area which text is populating
                    PrimitiveQuad txtOlay = new PrimitiveQuad(width, height, posGL);
                    txtOlay.setColor(textOverlayColor);

                    // text display (content)
                    String regex = txtKey.getPrefix() + "|" + "Text";
                    String content = txtKey.getStringValue().replaceAll(regex, "");

                    // font char dimensions
                    int fntWidth = Math.round(Text.STD_FONT_WIDTH * screenWidth / (float) mainPicWidth);
                    int fntHeight = Math.round(Text.STD_FONT_HEIGHT * screenHeight / (float) mainPicHeight);

                    // this is text component                    
                    Text txt = new Text(txtKey, fntTexture, content, posGL, fntWidth, fntHeight);
                    txt.setColor(textColor);
                    txt.setAlignment(Text.ALIGNMENT_CENTER);
                    txt.getOverlay().setColor(textOverlayColor);
                    txt.getOverlay().setWidth(width);
                    txt.getOverlay().setHeight(height);
                    txtComps.add(txt);
                } else if (txtVal != null) {
                    FO2IELogger.reportWarning("Unexisting cast for ("
                            + txtKey.getStringValue() + ", " + txtVal.getStringValue() + ")", null);
                }

                progress += 50.0f / section.text.size();
            }

            result.addAll(picComps);
            result.addAll(txtComps);
        }

        progress = 100.0f;
        return result;
    }

    /**
     * Write ini file to the file
     *
     * @param outfile output file to write ini to
     * @return write success (false if failed)
     */
    public boolean writeIniFile(File outfile) {
        boolean ok = false;

        FO2IELogger.reportInfo("Writing ini..", null);

        // overwrite existing
        if (outfile.exists()) {
            outfile.delete();
        }

        // if extension is missing add the extension
        if (!outfile.getName().endsWith(".ini")) {
            outfile = new File(outfile.getAbsolutePath() + ".ini");
        }

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(outfile);
            pw.println("# Generated ini by FOnline2 S3 Interface Editor");
            pw.println("# Generated at " + LocalDateTime.now().toString());
            pw.println();
            pw.println("# Common mappings");
            pw.println();
            String currClass = null;
            String prevClass = null;
            // iterating through the feature keys of the common mappings
            for (FeatureKey fk : commonFeatMap.keySet()) {
                currClass = fk.getClass().getSimpleName();
                if (!currClass.equals(prevClass)) {
                    pw.println();
                    pw.println("# " + currClass);
                    pw.println();
                }
                FeatureValue fv = commonFeatMap.get(fk);
                if (fv != null) {
                    // writing each of the common key/values to the ini file
                    pw.println(fk.getStringValue() + " = " + fv.getStringValue());
                }
                prevClass = currClass;
            }
            pw.println();
            pw.println("# Resolution pragmas");
            // iterating through the custom resolution pragmas
            for (ResolutionPragma pragma : customResolutions) {
                pw.println();
                pw.println("# Resolution pragma: " + pragma.getWidth() + "x" + pragma.getHeight());
                pw.println("resolution " + pragma.getWidth() + " " + pragma.getHeight());
                pw.println();
                for (FeatureKey fkx : pragma.customFeatMap.keySet()) {
                    // value from the pragma
                    FeatureValue fvx = pragma.customFeatMap.get(fkx);
                    // write value from the pragma to the ini file
                    if (fvx != null) {
                        pw.println(fkx.getStringValue() + " = " + fvx.getStringValue());
                    }
                }
            }

            ok = true;

        } catch (FileNotFoundException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

        FO2IELogger.reportInfo("Writing ini finished!", null);
        if (ok) {
            FO2IELogger.reportInfo("Everything OK!", null);
        } else {
            FO2IELogger.reportInfo("Writing ini resulted in error!", null);
        }

        return ok;
    }

    /**
     * Gets mapped keys (in either common mappings or custom resolution pragma
     * map)
     *
     * @param section current observed section
     * @param allRes use common mappings (true) or use custom resolution pragma
     * (false)
     * @return list of mapped keys
     */
    public FeatureKey[] getMappedKeys(Section section, boolean allRes) {
        final List<FeatureKey> result = new ArrayList<>();
        if (allRes) {
            for (FeatureKey key : section.keys) {
                if (commonFeatMap.containsKey(key)) {
                    result.add(key);
                }
            }
        } else if (resolutionPragma != null) {
            for (FeatureKey key : section.keys) {
                FeatureValue cval = commonFeatMap.get(key);
                FeatureValue pval = resolutionPragma.getCustomFeatMap().get(key);
                // if it's unique (provides overriden mapping)
                if (pval != null && !pval.equals(cval)) {
                    result.add(key);
                }
            }
        }
        FeatureKey[] toArray = result.toArray(new FeatureKey[result.size()]);

        return toArray;
    }

    /**
     * Gets unmapped keys (in either common mappings or custom resolution pragma
     * map)
     *
     * @param section current observed section
     * @param allRes use common mappings (true) or use custom resolution pragma
     * (false)
     * @return list of mapped keys
     */
    public FeatureKey[] getUnmappedKeys(Section section, boolean allRes) {
        FeatureKey[] allKeys = section.keys;
        FeatureKey[] mappedKeys = getMappedKeys(section, allRes);
        FeatureKey[] result = new FeatureKey[allKeys.length - mappedKeys.length];

        if (result.length > 0) {
            int index = 0;
            for (FeatureKey keyI : allKeys) {
                boolean isMapped = false;
                for (FeatureKey keyJ : mappedKeys) {
                    if (keyJ == keyI) {
                        isMapped = true;
                        break;
                    }
                }

                if (!isMapped) {
                    result[index++] = keyI;
                }

            }
        }

        return result;
    }

    public int getMainPicWidth() {
        return mainPicWidth;
    }

    public int getMainPicHeight() {
        return mainPicHeight;
    }

    public void resetProgress() {
        progress = 0.0f;
    }

    public Section getFaction() {
        return faction;
    }

    public float getProgress() {
        return progress;
    }

    public ReadMode getReadMode() {
        return readMode;
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

    public Section getGroundPickup() {
        return groundPickup;
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

    public BuildMode getBuildMode() {
        return buildMode;
    }

    public void setBuildMode(BuildMode buildMode) {
        this.buildMode = buildMode;
    }

}
