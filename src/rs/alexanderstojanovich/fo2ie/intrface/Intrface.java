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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import rs.alexanderstojanovich.fo2ie.ogl.Vector3fColors;
import rs.alexanderstojanovich.fo2ie.util.CoordsConverter;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.GLColor;
import rs.alexanderstojanovich.fo2ie.util.Node;
import rs.alexanderstojanovich.fo2ie.util.Tree;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Intrface {

    private final Configuration config = Configuration.getInstance();

    public static final String INI_FILENAME = "default.ini";
    public static final String PIC_REGEX = "(Main|Green|Yellow|Red)?(Pic|Anim)(Dn|Off|Mask|Na)?";

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

    private final Section aim = new Section(Section.SectionName.Aim, FeatureKey.Aim.values());
    private final Section barter = new Section(Section.SectionName.Barter, FeatureKey.Barter.values());
    private final Section character = new Section(Section.SectionName.Character, FeatureKey.Character.values());
    private final Section chosen = new Section(Section.SectionName.Chosen, FeatureKey.Chosen.values());
    private final Section console = new Section(Section.SectionName.Console, FeatureKey.Console.values());
    private final Section dialogBox = new Section(Section.SectionName.DialogBox, FeatureKey.Dialog.values());
    private final Section fixBoy = new Section(Section.SectionName.FixBoy, FeatureKey.FixBoy.values());
    private final Section globalMap = new Section(Section.SectionName.GlobalMap, FeatureKey.GlobalMap.values());
    private final Section inputBox = new Section(Section.SectionName.InputBox, FeatureKey.InputBox.values());
    private final Section intrface = new Section(Section.SectionName.Intrface, FeatureKey.Interface.values());
    private final Section inventory = new Section(Section.SectionName.Inventory, FeatureKey.Inventory.values());
    private final Section popUp = new Section(Section.SectionName.PopUp, FeatureKey.PopUp.values());
    private final Section miniMap = new Section(Section.SectionName.MiniMap, FeatureKey.MiniMap.values());
    private final Section login = new Section(Section.SectionName.Login, FeatureKey.Login.values());
    private final Section options = new Section(Section.SectionName.Options, FeatureKey.Options.values());
    private final Section priceSetup = new Section(Section.SectionName.PriceSetup, FeatureKey.PriceSetup.values());
    private final Section perk = new Section(Section.SectionName.Perk, FeatureKey.Perk.values());
    private final Section pipBoy = new Section(Section.SectionName.PipBoy, FeatureKey.PipBoy.values());
    private final Section pickUp = new Section(Section.SectionName.PickUp, FeatureKey.PipBoy.values());
    private final Section radio = new Section(Section.SectionName.Radio, FeatureKey.Radio.values());
    private final Section registration = new Section(Section.SectionName.Registration, FeatureKey.Registration.values());
    private final Section saveLoad = new Section(Section.SectionName.SaveLoad, FeatureKey.SaveLoad.values());
    private final Section sayBox = new Section(Section.SectionName.SayBox, FeatureKey.SayBox.values());
    private final Section skillBox = new Section(Section.SectionName.SkillBox, FeatureKey.SkillBox.values());
    private final Section split = new Section(Section.SectionName.Split, FeatureKey.Split.values());
    private final Section townView = new Section(Section.SectionName.TownView, FeatureKey.TownView.values());
    private final Section timer = new Section(Section.SectionName.Timer, FeatureKey.Timer.values());
    private final Section use = new Section(Section.SectionName.Use, FeatureKey.Use.values());

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

        mode = Mode.STD;

        commonFeatMap.clear();
        customResolutions.clear();

        FO2IELogger.reportInfo("Loading ini..", null);
        File inDir = Configuration.getInstance().getInDir();
        final File iniFile = new File(inDir.getPath() + File.separator + INI_FILENAME);
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(iniFile));
            String line;
            ResolutionPragma rs = null;
//            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith(";") && !line.startsWith("autocursor")) {
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
                    } else if (line.contains("=")) {
                        String[] words = line.split("=");
                        words[0] = words[0].trim();
                        words[1] = words[1].trim();

                        FeatureKey fk = FeatureKey.valueOf(words[0]);
                        FeatureValue fv = FeatureValue.valueOf(words[1]);
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

//                FO2IELogger.reportInfo("linenum = " + ++lineNum + ": " + line, null);
            }

            FO2IELogger.reportInfo("Loaded total common key/vals: " + commonFeatMap.size(), null);
            FO2IELogger.reportInfo("Total custom resolutions: " + customResolutions.size(), null);
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
        if (ok) {
            FO2IELogger.reportInfo("Ini has been loaded succefully!", null);
        } else {
            FO2IELogger.reportInfo("Loading ini resulted in error!", null);
        }

        initialized = ok;

        return ok;
    }

    /**
     * Builds components tree from current section based on resolution
     *
     * @param gl20 GL2 binding
     * @param fntTexture font texture for text rendering
     * @param unusedTexture unused texture (known as question mark texture)
     * @return built image from all the features
     * @throws java.io.IOException if building the module fails due to missing
     * image
     */
    public Tree<GLComponent> buildTree(GL2 gl20, Texture fntTexture, Texture unusedTexture) throws IOException {
        Tree<GLComponent> result = null;
        Section section = this.nameToSectionMap.get(sectionName);
        if (section != null) {
            String prefix = this.sectionToPrefixMap.get(section);
            if (prefix != null) {
                String mainPicStr = prefix + "MainPic";
                FeatureKey mainPicKey = FeatureKey.valueOf(mainPicStr);

                String mainPicPosStr = prefix + "Main";
                FeatureKey mainPicPosKey = FeatureKey.valueOf(mainPicPosStr);

                if (mainPicKey != null && resolutionPragma.customFeatMap.containsKey(mainPicKey)) {
                    ImageWrapper mainPic = (ImageWrapper) resolutionPragma.customFeatMap.get(mainPicKey);
                    MyVector4 mainPicPos = (MyVector4) resolutionPragma.customFeatMap.get(mainPicPosKey);

                    if (mainPicPos != null) {
                        float rposx = (mainPicPos.x + mainPicPos.z) / 2.0f;
                        float rposy = (mainPicPos.y + mainPicPos.w) / 2.0f;

                        Vector2f rootPos = new Vector2f(rposx, rposy);
                        Vector2f rootPosGL = CoordsConverter.getOpenGLCoordinates(rootPos, GUI.GL_CANVAS.getWidth(), GUI.GL_CANVAS.getHeight());

                        int rwidth = mainPicPos.z - mainPicPos.x;
                        int rheight = mainPicPos.w - mainPicPos.y;

                        mainPic.loadImage();
                        Texture rootTex = new Texture(gl20, mainPic.getImages()[0]);
                        Quad root = new Quad(rwidth, rheight, rootTex, rootPosGL);
                        result = new Tree<>(new Node<>(root));
                    } else {
                        mainPic.loadImage();
                        Texture rootTex = new Texture(gl20, mainPic.getImages()[0]);
                        Quad root = new Quad(rootTex);
                        result = new Tree<>(new Node<>(root));
                    }

                    for (FeatureKey featKey : section.keys) {
                        FeatureKey.Type fkType = featKey.getType();
                        if (fkType == FeatureKey.Type.PIC && featKey != mainPicKey) {
                            ImageWrapper pic = (ImageWrapper) resolutionPragma.customFeatMap.get(featKey);
                            String picPosStr = featKey.getStringValue().replaceAll(PIC_REGEX, "");
                            if (!picPosStr.equalsIgnoreCase("LogSinglePlayer")) {
                                FeatureKey picPosKey = FeatureKey.valueOf(picPosStr);

                                if (picPosKey != null) {
                                    MyVector4 picPosVal = (MyVector4) resolutionPragma.customFeatMap.get(picPosKey);

                                    float posx = (picPosVal.x + picPosVal.z) / 2.0f;
                                    float posy = (picPosVal.y + picPosVal.w) / 2.0f;

                                    Vector2f pos = new Vector2f(posx, posy);
                                    Vector2f posGL = CoordsConverter.getOpenGLCoordinates(pos, GUI.GL_CANVAS.getWidth(), GUI.GL_CANVAS.getHeight());

                                    int width = picPosVal.z - picPosVal.x;
                                    int height = picPosVal.w - picPosVal.y;

                                    pic.loadImage();
                                    BufferedImage[] images = pic.getImages();

                                    for (BufferedImage image : images) {
                                        Texture tex = new Texture(gl20, image);
                                        Quad imgComp = new Quad(width, height, tex, posGL);
                                        result.addChild(new Node<>(imgComp));
                                    }
                                }
                            }
                        } else if (fkType == FeatureKey.Type.PIC_POS && featKey != mainPicPosKey) {
                            MyVector4 picPosVal = (MyVector4) resolutionPragma.customFeatMap.get(featKey);

                            float posx = (picPosVal.x + picPosVal.z) / 2.0f;
                            float posy = (picPosVal.y + picPosVal.w) / 2.0f;

                            Vector2f pos = new Vector2f(posx, posy);
                            Vector2f posGL = CoordsConverter.getOpenGLCoordinates(pos, GUI.GL_CANVAS.getWidth(), GUI.GL_CANVAS.getHeight());

                            int width = picPosVal.z - picPosVal.x;
                            int height = picPosVal.w - picPosVal.y;

                            List<FeatureKey> pics = FeatureKey.getPics(featKey);
                            if (!pics.isEmpty()) {
                                for (FeatureKey fkPic : pics) {
                                    ImageWrapper pic = (ImageWrapper) resolutionPragma.customFeatMap.get(fkPic);
                                    pic.loadImage();
                                    BufferedImage[] images = pic.getImages();
                                    for (BufferedImage image : images) {
                                        Texture tex = new Texture(gl20, image);
                                        Quad imgComp = new Quad(width, height, tex, posGL);
                                        result.addChild(new Node<>(imgComp));
                                    }
                                }
                            } else {
                                Quad imgComp = new Quad(width, height, unusedTexture, posGL);
                                imgComp.setColor(qmarkColor);
                                result.addChild(new Node<>(imgComp));
                            }
                        } else if (fkType == FeatureKey.Type.TXT) {
                            MyVector4 txtVal = (MyVector4) resolutionPragma.customFeatMap.get(featKey);

                            float posx = (txtVal.x + txtVal.z) / 2.0f;
                            float posy = (txtVal.y + txtVal.w) / 2.0f;

                            Vector2f pos = new Vector2f(posx, posy);
                            Vector2f posGL = CoordsConverter.getOpenGLCoordinates(pos, GUI.GL_CANVAS.getWidth(), GUI.GL_CANVAS.getHeight());

                            int width = txtVal.z - txtVal.x;
                            int height = txtVal.w - txtVal.y;

                            String regex = featKey.getPrefix() + "|" + "Text";
                            String text = featKey.getStringValue().replaceAll(regex, "");

                            PrimitiveQuad txtOlay = new PrimitiveQuad(width, height, posGL);
                            txtOlay.setColor(textOverlayColor);
                            Text txtComp = new Text(fntTexture, text, new Vector4f(Vector3fColors.GREEN, 1.0f), posGL);
                            txtComp.setAlignment(Text.ALIGNMENT_CENTER);

                            result.addChild(new Node<>(txtOlay));
                            result.addChild(new Node<>(txtComp));
                        }
                    }

                }
            }
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

}
