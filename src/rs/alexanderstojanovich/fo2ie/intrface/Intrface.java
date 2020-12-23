/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.intrface;

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
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.feature.ImageWrapper;
import rs.alexanderstojanovich.fo2ie.feature.Vector4;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.Node;
import rs.alexanderstojanovich.fo2ie.util.Tree;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Intrface {

    public static final String INI_FILENAME = "default.ini";

    /**
     * Mode for reading {STD = standard - loading common values; RES =
     * resolution - loading from pragmas}
     */
    public static enum Mode {
        STD, RES
    }

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

    public static final String PIC_REGEX = "(Main)?Pic(Dn|Off|Mask|Na)?";

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

    private final List<ResolutionPragma> customResolutions = new ArrayList<>();

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

//                FO2IELogger.reportInfo("linenum = " + ++lineNum + ":" + line, null);
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

        return ok;
    }

    /**
     * Builds image from this section
     *
     * @param sectionName sectionName
     * @param resolutionPragma resolution used to preview this module
     * @return built image from all the features
     */
    public Tree<Component> buildModule(SectionName sectionName, ResolutionPragma resolutionPragma) {
        Tree<Component> result = null;
        Section section = this.nameToSectionMap.get(sectionName);
        if (section != null) {
            String prefix = this.sectionToPrefixMap.get(section);
            if (prefix != null) {
                String mainPicStr = prefix + "MainPic";
                FeatureKey mainPicKey = FeatureKey.valueOf(mainPicStr);

                String mainPicPosStr = prefix + "Main";
                FeatureKey mainPicPosKey = FeatureKey.valueOf(mainPicPosStr);

                if (mainPicKey != null && resolutionPragma.customFeatMap.containsKey(mainPicKey)) {
                    FeatureValue mainPicVal = resolutionPragma.customFeatMap.get(mainPicKey);
                    Vector4 mainPicPos = (Vector4) resolutionPragma.customFeatMap.get(mainPicPosKey);
                    Component root = new ImageComponent(mainPicKey, (ImageWrapper) mainPicVal, mainPicPos);
                    result = new Tree<>(new Node<>(root));

                    final Set<ImageComponent> picComps = new LinkedHashSet<>();
                    for (FeatureKey featKey : section.keys) {
                        FeatureKey.Type fkType = featKey.getType();
                        if (fkType == FeatureKey.Type.PIC && featKey != mainPicKey) {
                            ImageWrapper pic = (ImageWrapper) resolutionPragma.customFeatMap.get(featKey);
                            ImageComponent picComp = new ImageComponent(featKey, pic);
                            picComps.add(picComp);
                        }
                    }

                    for (ImageComponent picComp : picComps) {
                        FeatureKey picKey = picComp.getFeatureKey();
                        String picPosStr = picKey.getStringValue().replaceAll(PIC_REGEX, "");

                        if (!picPosStr.equalsIgnoreCase("LogSinglePlayer")) {
                            FeatureKey picPosKey = FeatureKey.valueOf(picPosStr);
                            FeatureValue picPosVal = resolutionPragma.customFeatMap.get(picPosKey);
                            picComp.setPosition((Vector4) picPosVal);

                            result.addChild(new Node<>(picComp));
                        }
                    }

                }
            }
        }

        return result;
    }

    public static BufferedImage generateImageFromModule(Tree<Component> comps) {
        ImageWrapper rimgw = (ImageWrapper) comps.getRoot().getData().getFeatureValue();
        BufferedImage result = new BufferedImage(rimgw.getImage().getWidth(), rimgw.getImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
        List<Component> preorder = comps.preorder(null, comps.getRoot());
        for (Component component : preorder) {
            ImageWrapper imgw = (ImageWrapper) component.getFeatureValue();
            Vector4 pos = component.getPosition();
            result.getGraphics().drawImage(imgw.getImage(), pos.x, pos.y, pos.z - pos.x, pos.w - pos.y, null);
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

}
