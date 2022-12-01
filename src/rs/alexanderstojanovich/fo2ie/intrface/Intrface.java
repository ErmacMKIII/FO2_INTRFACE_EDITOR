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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.modification.FeatureModification;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.GLColor;
import rs.alexanderstojanovich.fo2ie.modification.ModificationIfc;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Intrface {

    protected final StringBuilder errStrMsg = new StringBuilder();
    protected int errorNum = 0;

    protected final Configuration config = Configuration.getInstance();

    public static final String PIC_REGEX = "(Main|Green|Yellow|Red)?(Pic|Anim)(Dn|Dow|Off|Mask|Na)?";

    protected final Vector4f textColor = GLColor.awtColorToVec4(config.getTxtCol());
    protected final Vector4f textOverlayColor = GLColor.awtColorToVec4(config.getTxtOverlayCol());
    protected final Vector4f qmarkColor = GLColor.awtColorToVec4(config.getQmarkCol());

    /**
     * ReadMode for reading {STD = standard - loading common values; RES =
     * resolution - loading from pragmas}
     */
    public static enum ReadMode {
        STD, RES
    }

    protected boolean initialized = false;
    protected ReadMode readMode = ReadMode.STD;

    protected final Section aim = new Section(Section.SectionName.Aim, FeatureKey.Aim.AimMainPic, FeatureKey.Aim.values());
    protected final Section barter = new Section(Section.SectionName.Barter, FeatureKey.Barter.BarterMainPic, FeatureKey.Barter.values());
    protected final Section character = new Section(Section.SectionName.Character, FeatureKey.Character.ChaMainPic, FeatureKey.Character.values());
    protected final Section chosen = new Section(Section.SectionName.Chosen, FeatureKey.Empty.None, FeatureKey.Chosen.values());
    protected final Section console = new Section(Section.SectionName.Console, FeatureKey.Console.ConsoleMainPic, FeatureKey.Console.values());
    protected final Section dialogBox = new Section(Section.SectionName.DialogBox, FeatureKey.Dialog.DlgMainPic, FeatureKey.Dialog.values());
    protected final Section faction = new Section(Section.SectionName.Faction, FeatureKey.Faction.FactionMainPic, FeatureKey.Faction.values());
    protected final Section fixBoy = new Section(Section.SectionName.FixBoy, FeatureKey.FixBoy.FixMainPic, FeatureKey.FixBoy.values());
    protected final Section globalMap = new Section(Section.SectionName.GlobalMap, FeatureKey.GlobalMap.GmapMainPic, FeatureKey.GlobalMap.values());
    protected final Section groundPickup = new Section(Section.SectionName.GroundPickup, FeatureKey.GroundPickup.GPickupMainPic, FeatureKey.GroundPickup.values());
    protected final Section inputBox = new Section(Section.SectionName.InputBox, FeatureKey.InputBox.IboxMainPic, FeatureKey.InputBox.values());
    protected final Section intrface = new Section(Section.SectionName.Intrface, FeatureKey.Interface.IntMainPic, FeatureKey.Interface.values());
    protected final Section inventory = new Section(Section.SectionName.Inventory, FeatureKey.Inventory.InvMainPic, FeatureKey.Inventory.values());
    protected final Section popUp = new Section(Section.SectionName.PopUp, FeatureKey.Empty.None, FeatureKey.PopUp.values());
    protected final Section miniMap = new Section(Section.SectionName.MiniMap, FeatureKey.MiniMap.LmapMainPic, FeatureKey.MiniMap.values());
    protected final Section login = new Section(Section.SectionName.Login, FeatureKey.Login.LogMainPic, FeatureKey.Login.values());
    protected final Section options = new Section(Section.SectionName.Options, FeatureKey.Options.MoptMainPic, FeatureKey.Options.values());
    protected final Section priceSetup = new Section(Section.SectionName.PriceSetup, FeatureKey.PriceSetup.PSMainPic, FeatureKey.PriceSetup.values());
    protected final Section perk = new Section(Section.SectionName.Perk, FeatureKey.Perk.PerkMainPic, FeatureKey.Perk.values());
    protected final Section pipBoy = new Section(Section.SectionName.PipBoy, FeatureKey.PipBoy.PipMainPic, FeatureKey.PipBoy.values());
    protected final Section pickUp = new Section(Section.SectionName.PickUp, FeatureKey.PickUp.PupMainPic, FeatureKey.PickUp.values());
    protected final Section radio = new Section(Section.SectionName.Radio, FeatureKey.Radio.RadioMainPic, FeatureKey.Radio.values());
    protected final Section registration = new Section(Section.SectionName.Registration, FeatureKey.Registration.RegMainPic, FeatureKey.Registration.values());
    protected final Section saveLoad = new Section(Section.SectionName.SaveLoad, FeatureKey.SaveLoad.SaveLoadMainPic, FeatureKey.SaveLoad.values());
    protected final Section sayBox = new Section(Section.SectionName.SayBox, FeatureKey.SayBox.SayMainPic, FeatureKey.SayBox.values());
    protected final Section skillBox = new Section(Section.SectionName.SkillBox, FeatureKey.SkillBox.SboxMainPic, FeatureKey.SkillBox.values());
    protected final Section split = new Section(Section.SectionName.Split, FeatureKey.Split.SplitMainPic, FeatureKey.Split.values());
    protected final Section townView = new Section(Section.SectionName.TownView, FeatureKey.TownView.TViewMainPic, FeatureKey.TownView.values());
    protected final Section timer = new Section(Section.SectionName.Timer, FeatureKey.Timer.TimerMainPic, FeatureKey.Timer.values());
    protected final Section use = new Section(Section.SectionName.Use, FeatureKey.Use.UseMainPic, FeatureKey.Use.values());

    protected final Map<SectionName, Section> nameToSectionMap = new HashMap<>();
    protected final Map<Section, String> sectionToPrefixMap = new HashMap<>();

    protected final Dictionary originalBinds = new Dictionary();
    protected final Dictionary modifiedBinds = new Dictionary();

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
        originalBinds.clear();
        modifiedBinds.clear();

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
                        originalBinds.customResolutions.add(rs);
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
                                    originalBinds.commonFeatMap.put(fk, fv);
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

            FO2IELogger.reportInfo("Loaded total common key/vals: " + originalBinds.commonFeatMap.size(), null);
            FO2IELogger.reportInfo("Total custom resolutions: " + originalBinds.customResolutions.size(), null);
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
            originalBinds.copyTo(modifiedBinds, false);
            FO2IELogger.reportInfo("Ini has been loaded succefully!", null);
        } else {
            FO2IELogger.reportInfo("Loading ini resulted in error!", null);
        }

        initialized = ok;

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
        originalBinds.commonFeatMap.clear();
        originalBinds.customResolutions.clear();

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
                        originalBinds.customResolutions.add(rs);
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
                                    originalBinds.commonFeatMap.put(fk, fv);
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

            FO2IELogger.reportInfo("Loaded total common key/vals: " + originalBinds.commonFeatMap.size(), null);
            FO2IELogger.reportInfo("Total custom resolutions: " + originalBinds.customResolutions.size(), null);
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
            originalBinds.copyTo(modifiedBinds, false);
            FO2IELogger.reportInfo("Ini has been loaded succefully!", null);
        } else {
            FO2IELogger.reportInfo("Loading ini resulted in error!", null);
        }

        initialized = ok;

        return ok;
    }

    /**
     * Apply changes from modified to original. (Usually Before saving to the
     * ini.)
     */
    public void applyChanges() {
        modifiedBinds.copyTo(originalBinds, true);
        modifiedBinds.clear();
    }

    /**
     * Write ini file to the file
     *
     * @param outfile output file to write ini to
     * @return write success (false if failed)
     */
    public boolean writeIniFile(File outfile) {
        applyChanges();
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
            for (FeatureKey fk : originalBinds.commonFeatMap.keySet()) {
                currClass = fk.getClass().getSimpleName();
                if (!currClass.equals(prevClass)) {
                    pw.println();
                    pw.println("# " + currClass);
                    pw.println();
                }
                FeatureValue fv = originalBinds.commonFeatMap.get(fk);
                if (fv != null) {
                    // writing each of the common key/values to the ini file
                    pw.println(fk.getStringValue() + " = " + fv.getStringValue());
                }
                prevClass = currClass;
            }
            pw.println();
            pw.println("# Resolution pragmas");
            // iterating through the custom resolution pragmas
            for (ResolutionPragma pragma : originalBinds.customResolutions) {
                pw.println();
                pw.println("# Resolution pragma: " + pragma.resolution.getWidth() + "x" + pragma.resolution.getHeight());
                pw.println("resolution " + pragma.resolution.getWidth() + " " + pragma.resolution.getHeight());
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
     * @return list of mapped keys
     */
    public FeatureKey[] getMappedKeys(Section section) {
        final List<FeatureKey> result = new ArrayList<>();
        for (FeatureKey key : section.keys) {
            if (modifiedBinds.commonFeatMap.containsKey(key)) {
                result.add(key);
            }
        }
        FeatureKey[] toArray = result.toArray(new FeatureKey[result.size()]);

        return toArray;
    }

    /**
     * Gets mapped keys (in either common mappings or custom resolution pragma
     * map)
     *
     * @param section current observed section
     * @param resolution desired resolution
     * @return list of mapped keys
     */
    public FeatureKey[] getMappedKeys(Section section, Resolution resolution) {
        final List<FeatureKey> result = new ArrayList<>();
        for (FeatureKey key : section.keys) {
            FeatureValue modifiedCval = modifiedBinds.commonFeatMap.get(key);
            ResolutionPragma modifiedRP = modifiedBinds.customResolutions.stream().filter(x -> x.resolution.equals(resolution)).findAny().orElse(null);
            FeatureValue modifiedPval = null;
            if (modifiedRP != null) {
                modifiedPval = modifiedRP.getCustomFeatMap().get(key);
            }

            boolean modifiedHasMapped = modifiedPval != null && !modifiedPval.equals(modifiedCval);

            // if it's unique (provides overriden mapping)
            if (modifiedHasMapped) {
                result.add(key);
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
     * @return list of unmapped keys
     */
    public FeatureKey[] getUnmappedKeys(Section section) {
        FeatureKey[] allKeys = section.keys;
        FeatureKey[] mappedKeys = getMappedKeys(section);
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

    /**
     * Gets unmapped keys (in either common mappings or custom resolution pragma
     * map)
     *
     * @param section current observed section
     * @param resolution Resolution with mappings.
     * @return list of unmapped keys
     */
    public FeatureKey[] getUnmappedKeys(Section section, Resolution resolution) {
        FeatureKey[] allKeys = section.keys;
        FeatureKey[] mappedKeys = getMappedKeys(section, resolution);
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

    public List<ModificationIfc> getChanges() {
        return Dictionary.difference(originalBinds, modifiedBinds);
    }

    public boolean getChanges(List<ModificationIfc> outResult) {
        return Dictionary.difference(originalBinds, modifiedBinds, outResult);
    }

    public Section getFaction() {
        return faction;
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

    public Dictionary getOriginalBinds() {
        return originalBinds;
    }

    public Dictionary getModifiedBinds() {
        return modifiedBinds;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
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
