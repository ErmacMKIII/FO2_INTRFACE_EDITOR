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
package rs.alexanderstojanovich.fo2ie.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rs.alexanderstojanovich.fo2ie.util.Pair;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public interface FeatureKey {

    public static final String PIC_REGEX = "(Main|Green|Yellow|Red)?(Pic|Anim)(Dn|Dow|Off|Mask|Na)?";

    public static final String AIM = "Aim";
    public static final String BARTER = "Barter";
    public static final String CHARACTER = "Cha";
    public static final String CHOSEN = "Chosen";
    public static final String CONSOLE = "Console";
    public static final String DIALOG_BOX = "Dlg";
    public static final String FACTION = "Faction";
    public static final String FIX_BOY = "Fix";
    public static final String GLOBAL_MAP = "Gmap";
    public static final String GROUND_PICKUP = "GPickup";

    public static final String INPUT_BOX = "Ibox";
    public static final String INTRFACE = "Int";
    public static final String INVENTORY = "Inv";
    public static final String POP_UP = "LMenu";
    public static final String MINI_MAP = "Lmap";
    public static final String LOGIN = "Log";
    public static final String OPTIONS = "Mopt";
    public static final String PRICE_SETUP = "PS";
    public static final String PERK = "Perk";
    public static final String PIP_BOY = "Pip";

    public static final String PICK_UP = "Pup";
    public static final String RADIO = "Radio";
    public static final String REGISTRATION = "Reg";
    public static final String SAVE_LOAD = "SaveLoad";
    public static final String SAY_BOX = "Say";
    public static final String SKILL_BOX = "Sbox";
    public static final String SPLIT = "Split";

    public static final String TOWN_VIEW = "TView";
    public static final String TIMER = "Timer";
    public static final String USE = "Use";

    public static final String[] ABBRS = {
        AIM, BARTER, CHARACTER, CHOSEN, CONSOLE, DIALOG_BOX, FACTION, FIX_BOY, GLOBAL_MAP, GROUND_PICKUP,
        INPUT_BOX, INTRFACE, INVENTORY, POP_UP, MINI_MAP, LOGIN, OPTIONS, PRICE_SETUP, PERK, PIP_BOY,
        PICK_UP, RADIO, REGISTRATION, SAVE_LOAD, SAY_BOX, SKILL_BOX, SPLIT,
        TOWN_VIEW, TIMER, USE
    };

    // Wesan auto-cursor
    public static enum AutoCursor {
        autocursor_hex_anim
    }

    // Aim
    public static enum Aim implements FeatureKey {
        AimCancel,
        AimCancelPicDn,
        AimEyesProc,
        AimEyesText,
        AimGroinProc,
        AimGroinText,
        AimHeadProc,
        AimHeadText,
        AimLArmProc,
        AimLArmText,
        AimLLegProc,
        AimLLegText,
        AimMain,
        AimMainPic,
        AimPicX,
        AimPicY,
        AimRArmProc,
        AimRArmText,
        AimRLegProc,
        AimRLegText,
        AimTorsoProc,
        AimTorsoText;

        @Override
        public String getPrefix() {
            return AIM;
        }

        @Override
        public Type getType() {
            switch (this) {
                case AimCancelPicDn:
                case AimMainPic:
                    return Type.PIC;
                case AimCancel:
                case AimMain:
                    return Type.PIC_POS;
                case AimEyesProc:
                case AimEyesText:
                case AimGroinProc:
                case AimGroinText:
                case AimHeadProc:
                case AimHeadText:
                case AimLArmProc:
                case AimLArmText:
                case AimLLegProc:
                case AimLLegText:
                case AimRArmProc:
                case AimRArmText:
                case AimRLegProc:
                case AimRLegText:
                case AimTorsoProc:
                case AimTorsoText:
                    return Type.TXT;
                case AimPicX:
                case AimPicY:
                    return Type.OFFSET;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return AimMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return AimMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{AimCancelPicDn, AimMainPic};
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{AimCancel, AimMain};
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                AimEyesProc,
                AimEyesText,
                AimGroinProc,
                AimGroinText,
                AimHeadProc,
                AimHeadText,
                AimLArmProc,
                AimLArmText,
                AimLLegProc,
                AimLLegText,
                AimRArmProc,
                AimRArmText,
                AimRLegProc,
                AimRLegText,
                AimTorsoProc,
                AimTorsoText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return new FeatureKey[]{AimPicX, AimPicY};
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Barter
    public static enum Barter implements FeatureKey {
        BarterButton0,
        BarterButton1,
        BarterButton2,
        BarterButton3,
        BarterButton4,
        BarterButtonOpponent0,
        BarterButtonOpponent1,
        BarterButtonOpponent2,
        BarterButtonOpponent3,
        BarterButtonOpponent4,
        BarterButtonPic0,
        BarterButtonPic1,
        BarterButtonPic2,
        BarterButtonPic3,
        BarterButtonPic4,
        BarterChosen,
        BarterCont1,
        BarterCont1ItemHeight,
        BarterCont1Pic,
        BarterCont1ScrDn,
        BarterCont1ScrDnPicDn,
        BarterCont1ScrUp,
        BarterCont1ScrUpPicDn,
        BarterCont1o,
        BarterCont1oItemHeight,
        BarterCont1oScrDn,
        BarterCont1oScrDnPicDn,
        BarterCont1oScrUp,
        BarterCont1oScrUpPicDn,
        BarterCont2,
        BarterCont2ItemHeight,
        BarterCont2Pic,
        BarterCont2ScrDn,
        BarterCont2ScrDnPicDn,
        BarterCont2ScrUp,
        BarterCont2ScrUpPicDn,
        BarterCont2o,
        BarterCont2oItemHeight,
        BarterCont2oScrDn,
        BarterCont2oScrDnPicDn,
        BarterCont2oScrUp,
        BarterCont2oScrUpPicDn,
        BarterCost1,
        BarterCost2,
        BarterCritter,
        BarterMain,
        BarterMainPic,
        BarterOffer,
        BarterOfferPic,
        BarterOfferText,
        BarterTalk,
        BarterTalkPic,
        BarterTalkText;

        @Override
        public String getPrefix() {
            return BARTER;
        }

        @Override
        public Type getType() {
            switch (this) {
                case BarterButtonPic0:
                case BarterButtonPic1:
                case BarterButtonPic2:
                case BarterButtonPic3:
                case BarterButtonPic4:
                case BarterCont1ScrDnPicDn:
                case BarterCont1oScrDnPicDn:
                case BarterCont1oScrUpPicDn:
                case BarterCont1ScrUpPicDn:
                case BarterCont2oScrUpPicDn:
                case BarterCont2ScrUpPicDn:
                case BarterCont2ScrDnPicDn:
                case BarterCont2oScrDnPicDn:
                case BarterTalkPic:
                case BarterOfferPic:
                case BarterMainPic:
                    return Type.PIC;
                case BarterButton0:
                case BarterButton1:
                case BarterButton2:
                case BarterButton3:
                case BarterButton4:
                case BarterButtonOpponent0:
                case BarterButtonOpponent1:
                case BarterButtonOpponent2:
                case BarterButtonOpponent3:
                case BarterButtonOpponent4:
                case BarterChosen:
                case BarterCritter:
                case BarterCont1:
                case BarterCont2:
                case BarterCont1oScrDn:
                case BarterCont1oScrUp:
                case BarterCont2ScrDn:
                case BarterCont2ScrUp:
                case BarterCont2oScrDn:
                case BarterCont2oScrUp:
                case BarterCont1ScrDn:
                case BarterCont1ScrUp:
                case BarterCont1Pic:
                case BarterCont2Pic:
                case BarterMain:
                case BarterTalk:
                case BarterOffer:
                case BarterCont1o:
                case BarterCont2o:
                    return Type.PIC_POS;
                case BarterCost1:
                case BarterCost2:
                case BarterOfferText:
                case BarterTalkText:
                    return Type.TXT;
                case BarterCont1ItemHeight:
                case BarterCont1oItemHeight:
                case BarterCont2ItemHeight:
                case BarterCont2oItemHeight:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return BarterMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return BarterMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                BarterButtonPic0,
                BarterButtonPic1,
                BarterButtonPic2,
                BarterButtonPic3,
                BarterButtonPic4,
                BarterCont1ScrDnPicDn,
                BarterCont1oScrDnPicDn,
                BarterCont1oScrUpPicDn,
                BarterCont1ScrUpPicDn,
                BarterCont2oScrUpPicDn,
                BarterCont2ScrUpPicDn,
                BarterCont2ScrDnPicDn,
                BarterCont2oScrDnPicDn,
                BarterTalkPic,
                BarterOfferPic,
                BarterMainPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                BarterButton0,
                BarterButton1,
                BarterButton2,
                BarterButton3,
                BarterButton4,
                BarterButtonOpponent0,
                BarterButtonOpponent1,
                BarterButtonOpponent2,
                BarterButtonOpponent3,
                BarterButtonOpponent4,
                BarterChosen,
                BarterCritter,
                BarterCont1,
                BarterCont2,
                BarterCont1oScrDn,
                BarterCont1oScrUp,
                BarterCont2ScrDn,
                BarterCont2ScrUp,
                BarterCont2oScrDn,
                BarterCont2oScrUp,
                BarterCont1ScrDn,
                BarterCont1ScrUp,
                BarterCont1Pic,
                BarterCont2Pic,
                BarterMain,
                BarterTalk,
                BarterOffer,
                BarterCont1o,
                BarterCont2o
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                BarterCost1,
                BarterCost2,
                BarterOfferText,
                BarterTalkText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{
                BarterCont1ItemHeight,
                BarterCont1oItemHeight,
                BarterCont2ItemHeight,
                BarterCont2oItemHeight
            };
        }

    }

    // Character
    public static enum Character implements FeatureKey {
        ChaAge,
        ChaAgeAge,
        ChaAgeDown,
        ChaAgeDownPicDn,
        ChaAgeMain,
        ChaAgeMainPic,
        ChaAgePicDn,
        ChaAgeUp,
        ChaAgeUpPicDn,
        ChaCancel,
        ChaCancelPicDn,
        ChaCancelText,
        ChaDmg,
        ChaDmgLife,
        ChaDmgNextX,
        ChaDmgNextY,
        ChaExp,
        ChaLevel,
        ChaMain,
        ChaMainPic,
        ChaName,
        ChaNameMain,
        ChaNameMainPic,
        ChaNameName,
        ChaNameNameText,
        ChaNamePass,
        ChaNamePassText,
        ChaNamePicDn,
        ChaNextLevel,
        ChaOk,
        ChaOkPicDn,
        ChaOkText,
        ChaParamDesc,
        ChaParamName,
        ChaParamPic,
        ChaPrint,
        ChaPrintPicDn,
        ChaPrintText,
        ChaSex,
        ChaSexFemale,
        ChaSexFemalePicDn,
        ChaSexMain,
        ChaSexMainPic,
        ChaSexMale,
        ChaSexMalePicDn,
        ChaSexPicDn,
        ChaSkillName,
        ChaSkillNextX,
        ChaSkillNextY,
        ChaSkillText,
        ChaSkillValue,
        ChaSliderMinus,
        ChaSliderMinusPicDn,
        ChaSliderPic,
        ChaSliderPlus,
        ChaSliderPlusPicDn,
        ChaSliderX,
        ChaSliderY,
        ChaSpecialLevel,
        ChaSpecialNextX,
        ChaSpecialNextY,
        ChaSpecialParams,
        ChaSpecialText,
        ChaSpecialValue,
        ChaStatsName,
        ChaStatsNextX,
        ChaStatsNextY,
        ChaStatsValue,
        ChaSwitch,
        ChaSwitchKarmaPic,
        ChaSwitchKillsPic,
        ChaSwitchMaskPic,
        ChaSwitchPerksPic,
        ChaSwitchScrDn,
        ChaSwitchScrDnPic,
        ChaSwitchScrDnPicDn,
        ChaSwitchScrUp,
        ChaSwitchScrUpPic,
        ChaSwitchScrUpPicDn,
        ChaSwitchText,
        ChaUnspentSP,
        ChaUnspentSPText;

        @Override
        public String getPrefix() {
            return CHARACTER;
        }

        @Override
        public Type getType() {
            switch (this) {
                case ChaAgeDownPicDn:
                case ChaAgeMainPic:
                case ChaAgePicDn:
                case ChaAgeUpPicDn:
                case ChaCancelPicDn:
                case ChaMainPic:
                case ChaNameMainPic:
                case ChaNamePicDn:
                case ChaOkPicDn:
                case ChaPrintPicDn:
                case ChaSexFemalePicDn:
                case ChaSexMainPic:
                case ChaSexMalePicDn:
                case ChaSliderPic:
                case ChaSliderMinusPicDn:
                case ChaSliderPlusPicDn:
                case ChaSwitchKarmaPic:
                case ChaSwitchScrDnPic:
                case ChaSwitchKillsPic:
                case ChaSwitchMaskPic:
                case ChaSwitchScrDnPicDn:
                case ChaSwitchPerksPic:
                case ChaSwitchScrUpPicDn:
                case ChaSwitchScrUpPic:
                case ChaSexPicDn:
                    return Type.PIC;
                case ChaAge:
                case ChaSex:
                case ChaName:
                case ChaMain:
                case ChaPrint:
                case ChaOk:
                case ChaCancel:
                case ChaSliderMinus:
                case ChaSliderPlus:
                case ChaAgeUp:
                case ChaAgeDown:
                case ChaAgeMain:
                case ChaNameMain:
                case ChaSexFemale:
                case ChaSexMale:
                case ChaSexMain:
                case ChaSwitch:
                    return Type.PIC_POS;
                case ChaParamPic:
                case ChaCancelText:
                case ChaOkText:
                case ChaNamePassText:
                case ChaPrintText:
                case ChaLevel:
                case ChaExp:
                case ChaDmg:
                case ChaDmgLife:
                case ChaSpecialText:
                case ChaSpecialValue:
                case ChaSpecialLevel:
                case ChaNextLevel:
                case ChaStatsName:
                case ChaStatsValue:
                case ChaSwitchScrDn:
                case ChaSwitchScrUp:
                case ChaAgeAge:
                case ChaNameName:
                case ChaNameNameText:
                case ChaNamePass:
                case ChaSwitchText:
                case ChaSkillText:
                case ChaSkillValue:
                case ChaSkillName:
                case ChaParamDesc:
                case ChaParamName:
                case ChaSpecialParams:
                case ChaUnspentSP:
                case ChaUnspentSPText:
                    return Type.TXT;
                case ChaDmgNextX:
                case ChaDmgNextY:
                case ChaSkillNextX:
                case ChaSkillNextY:
                case ChaSliderX:
                case ChaSliderY:
                case ChaSpecialNextX:
                case ChaSpecialNextY:
                case ChaStatsNextX:
                case ChaStatsNextY:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;

            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return ChaMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return ChaMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                ChaAgeDownPicDn,
                ChaAgeMainPic,
                ChaAgePicDn,
                ChaAgeUpPicDn,
                ChaCancelPicDn,
                ChaMainPic,
                ChaNameMainPic,
                ChaNamePicDn,
                ChaOkPicDn,
                ChaPrintPicDn,
                ChaSexFemalePicDn,
                ChaSexMainPic,
                ChaSexMalePicDn,
                ChaSliderPic,
                ChaSliderMinusPicDn,
                ChaSliderPlusPicDn,
                ChaSwitchKarmaPic,
                ChaSwitchScrDnPic,
                ChaSwitchKillsPic,
                ChaSwitchMaskPic,
                ChaSwitchScrDnPicDn,
                ChaSwitchPerksPic,
                ChaSwitchScrUpPicDn,
                ChaSwitchScrUpPic,
                ChaSexPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                ChaAge,
                ChaSex,
                ChaName,
                ChaMain,
                ChaPrint,
                ChaOk,
                ChaCancel,
                ChaSliderMinus,
                ChaSliderPlus,
                ChaAgeUp,
                ChaAgeDown,
                ChaAgeMain,
                ChaNameMain,
                ChaSexFemale,
                ChaSexMale,
                ChaSexMain,
                ChaSwitch
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                ChaParamPic,
                ChaCancelText,
                ChaOkText,
                ChaNamePassText,
                ChaPrintText,
                ChaLevel,
                ChaExp,
                ChaDmg,
                ChaDmgLife,
                ChaSpecialText,
                ChaSpecialValue,
                ChaSpecialLevel,
                ChaNextLevel,
                ChaStatsName,
                ChaStatsValue,
                ChaSwitchScrDn,
                ChaSwitchScrUp,
                ChaAgeAge,
                ChaNameName,
                ChaNameNameText,
                ChaNamePass,
                ChaSwitchText,
                ChaSkillText,
                ChaSkillValue,
                ChaSkillName,
                ChaParamDesc,
                ChaParamName,
                ChaSpecialParams,
                ChaUnspentSP,
                ChaUnspentSPText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{
                ChaDmgNextX,
                ChaDmgNextY,
                ChaSkillNextX,
                ChaSkillNextY,
                ChaSliderX,
                ChaSliderY,
                ChaSpecialNextX,
                ChaSpecialNextY,
                ChaStatsNextX,
                ChaStatsNextY
            };
        }

    }

    // Chosen Tabs
    public static enum Chosen implements FeatureKey {
        ChosenTab,
        ChosenTabPic,
        ChosenTabStepX,
        ChosenTabStepY;

        @Override
        public String getPrefix() {
            return CHOSEN;
        }

        @Override
        public Type getType() {
            switch (this) {
                case ChosenTabPic:
                    return Type.PIC;
                case ChosenTab:
                    return Type.PIC_POS;
                case ChosenTabStepX:
                case ChosenTabStepY:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return ChosenTabPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return ChosenTab;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{ChosenTabPic};
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{ChosenTab};
        }

        @Override
        public FeatureKey[] getTextValues() {
            return null;
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{ChosenTabStepX, ChosenTabStepY};
        }

    }

    // Console
    public static enum Console implements FeatureKey {
        ConsoleMainPic,
        ConsoleMainPicX,
        ConsoleMainPicY,
        ConsoleTextX,
        ConsoleTextY;

        @Override
        public String getPrefix() {
            return CONSOLE;
        }

        @Override
        public Type getType() {
            switch (this) {
                case ConsoleMainPic:
                    return Type.PIC;
                case ConsoleTextX:
                case ConsoleTextY:
                    return Type.TXT;
                case ConsoleMainPicX:
                case ConsoleMainPicY:
                    return Type.OFFSET;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return ConsoleMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return null;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{ConsoleMainPic};
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return null;
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{ConsoleTextX, ConsoleTextY};
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return new FeatureKey[]{ConsoleTextX, ConsoleTextY};
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Dialog (Talk)
    public static enum Dialog implements FeatureKey {
        DlgAnsw,
        DlgAnswPic,
        DlgAnswText,
        DlgAvatar,
        DlgBarter,
        DlgBarterPicDn,
        DlgBarterText,
        DlgMain,
        DlgMainPic,
        DlgMoney,
        DlgNextAnswX,
        DlgNextAnswY,
        DlgSay,
        DlgSayPicDn,
        DlgSayText,
        DlgScrDn,
        DlgScrDnPicDn,
        DlgScrUp,
        DlgScrUpPicDn,
        DlgText,
        DlgTimer,
        DlgboxBottom,
        DlgboxBottomPic,
        DlgboxButton,
        DlgboxButtonPicDn,
        DlgboxButtonText,
        DlgboxMiddle,
        DlgboxMiddlePic,
        DlgboxText,
        DlgboxTop,
        DlgboxTopPic;

        @Override
        public String getPrefix() {
            return DIALOG_BOX;
        }

        @Override
        public Type getType() {
            switch (this) {
                case DlgAnswPic:
                case DlgAvatar:
                case DlgBarterPicDn:
                case DlgMainPic:
                case DlgSayPicDn:
                case DlgScrDnPicDn:
                case DlgScrUpPicDn:
                case DlgboxButtonPicDn:
                case DlgboxBottomPic:
                case DlgboxMiddlePic:
                case DlgboxTopPic:
                    return Type.PIC;
                case DlgAnsw:
                case DlgBarter:
                case DlgSay:
                case DlgScrDn:
                case DlgScrUp:
                case DlgMain:
                case DlgboxButton:
                case DlgboxBottom:
                case DlgboxMiddle:
                case DlgboxTop:
                    return Type.PIC_POS;
                case DlgAnswText:
                case DlgBarterText:
                case DlgMoney:
                case DlgSayText:
                case DlgTimer:
                case DlgText:
                case DlgboxButtonText:
                case DlgboxText:
                    return Type.TXT;
                case DlgNextAnswX:
                case DlgNextAnswY:
                    return Type.OFFSET;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return DlgMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return DlgMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                DlgAnswPic,
                DlgAvatar,
                DlgBarterPicDn,
                DlgMainPic,
                DlgSayPicDn,
                DlgScrDnPicDn,
                DlgScrUpPicDn,
                DlgboxButtonPicDn,
                DlgboxBottomPic,
                DlgboxMiddlePic,
                DlgboxTopPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                DlgAnsw,
                DlgBarter,
                DlgSay,
                DlgScrDn,
                DlgScrUp,
                DlgMain,
                DlgboxButton,
                DlgboxBottom,
                DlgboxMiddle,
                DlgboxTop
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                DlgAnswText,
                DlgBarterText,
                DlgMoney,
                DlgSayText,
                DlgTimer,
                DlgText,
                DlgboxButtonText,
                DlgboxText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return new FeatureKey[]{
                DlgNextAnswX,
                DlgNextAnswY
            };
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Faction (deprecated)
    public static enum Faction implements FeatureKey {
        FactionButton0,
        FactionButton1,
        FactionButton2,
        FactionButton3,
        FactionButton4,
        FactionButton5,
        FactionLabel,
        FactionMainPic,
        FactionMainText;

        @Override
        public FeatureKey getMainPic() {
            return FactionMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return null;
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public String getPrefix() {
            return FACTION;
        }

        @Override
        public Type getType() {
            switch (this) {
                case FactionMainPic:
                    return Type.PIC;
                case FactionButton0:
                case FactionButton1:
                case FactionButton2:
                case FactionButton3:
                case FactionButton4:
                case FactionButton5:
                    return Type.PIC_POS;
                case FactionLabel:
                case FactionMainText:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{FactionMainPic};
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                FactionButton0,
                FactionButton1,
                FactionButton2,
                FactionButton3,
                FactionButton4,
                FactionButton5
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                FactionLabel,
                FactionMainText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Fix-boy
    public static enum FixBoy implements FeatureKey {
        FixButton1,
        FixButton2,
        FixButton3,
        FixButton4,
        FixButton5,
        FixButtonPic1,
        FixButtonPic2,
        FixButtonPic3,
        FixButtonPic4,
        FixButtonPic5,
        FixDone,
        FixDonePicDn,
        FixDow,
        FixDowPic,
        FixFix,
        FixFixPicDn,
        FixMain,
        FixMainPic,
        FixNum,
        FixScrDn,
        FixScrDnPicDn,
        FixScrUp,
        FixScrUpPicDn,
        FixUp,
        FixUpPic,
        FixWin;

        @Override
        public String getPrefix() {
            return FIX_BOY;
        }

        @Override
        public Type getType() {
            switch (this) {
                case FixButtonPic1:
                case FixButtonPic2:
                case FixButtonPic3:
                case FixButtonPic4:
                case FixButtonPic5:
                case FixDonePicDn:
                case FixDowPic:
                case FixFixPicDn:
                case FixMainPic:
                case FixScrDnPicDn:
                case FixScrUpPicDn:
                case FixUpPic:
                    return Type.PIC;
                case FixButton1:
                case FixButton2:
                case FixButton3:
                case FixButton4:
                case FixButton5:
                case FixDone:
                case FixDow:
                case FixFix:
                case FixMain:
                case FixScrDn:
                case FixScrUp:
                case FixUp:
                    return Type.PIC_POS;
                case FixNum:
                case FixWin:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return FixMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return FixMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                FixButtonPic1,
                FixButtonPic2,
                FixButtonPic3,
                FixButtonPic4,
                FixButtonPic5,
                FixDonePicDn,
                FixDowPic,
                FixFixPicDn,
                FixMainPic,
                FixScrDnPicDn,
                FixScrUpPicDn,
                FixUpPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                FixButton1,
                FixButton2,
                FixButton3,
                FixButton4,
                FixButton5,
                FixDone,
                FixDow,
                FixFix,
                FixMain,
                FixScrDn,
                FixScrUp,
                FixUp
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{FixNum, FixWin};
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Global map (World map)
    public static enum GlobalMap implements FeatureKey {
        GmapCar,
        GmapCha,
        GmapChaPicDn,
        GmapDayTime,
        GmapDayTimeAnim,
        GmapFix,
        GmapFixPicDn,
        GmapFollowCritPic,
        GmapFollowCritSelfPic,
        GmapInv,
        GmapInvPicDn,
        GmapLock,
        GmapMain,
        GmapMainPic,
        GmapMap,
        GmapMenu,
        GmapMenuPicDn,
        GmapMessageBox,
        GmapName,
        GmapNameStepX,
        GmapNameStepY,
        GmapPanel,
        GmapPip,
        GmapPipPicDn,
        GmapStayPic,
        GmapStayPicDn,
        GmapStayPicMask,
        GmapTabs,
        GmapTabsScrDn,
        GmapTabsScrDnPicDn,
        GmapTabsScrUp,
        GmapTabsScrUpPicDn,
        GmapTilesPic,
        GmapTilesX,
        GmapTilesY,
        GmapTime,
        GmapTown,
        GmapTownInOffsX,
        GmapTownInOffsY,
        GmapTownInPic,
        GmapTownInPicDn,
        GmapTownInPicMask,
        GmapTownPicDn,
        GmapTownViewOffsX,
        GmapTownViewOffsY,
        GmapTownViewPic,
        GmapTownViewPicDn,
        GmapTownViewPicMask;

        @Override
        public String getPrefix() {
            return GLOBAL_MAP;
        }

        @Override
        public Type getType() {
            switch (this) {
                case GmapChaPicDn:
                case GmapFixPicDn:
                case GmapFollowCritPic:
                case GmapFollowCritSelfPic:
                case GmapMainPic:
                case GmapMenuPicDn:
                case GmapPipPicDn:
                case GmapTownPicDn:
                case GmapTownViewPic:
                case GmapTownViewPicDn:
                case GmapTownViewPicMask:
                case GmapTownInPicDn:
                case GmapTownInPicMask:
                case GmapTabsScrDnPicDn:
                case GmapStayPic:
                case GmapStayPicDn:
                case GmapTabsScrUpPicDn:
                case GmapTownInPic:
                case GmapTilesPic:
                case GmapStayPicMask:
                case GmapDayTimeAnim:
                case GmapInvPicDn:
                    return Type.PIC;
                case GmapCha:
                case GmapFix:
                case GmapMain:
                case GmapPip:
                case GmapTown:
                case GmapCar:
                case GmapTabs:
                case GmapDayTime:
                case GmapInv:
                case GmapMap:
                case GmapMenu:
                case GmapPanel:
                case GmapTabsScrDn:
                case GmapTabsScrUp:
                    return Type.PIC_POS;
                case GmapName:
                case GmapLock:
                case GmapMessageBox:
                case GmapTime:
                    return Type.TXT;
                case GmapNameStepX:
                case GmapNameStepY:
                    return Type.VALUE;
                case GmapTilesX:
                case GmapTilesY:
                case GmapTownInOffsX:
                case GmapTownInOffsY:
                case GmapTownViewOffsX:
                case GmapTownViewOffsY:
                    return Type.OFFSET;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return GmapMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return GmapMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                GmapChaPicDn,
                GmapFixPicDn,
                GmapFollowCritPic,
                GmapFollowCritSelfPic,
                GmapMainPic,
                GmapMenuPicDn,
                GmapPipPicDn,
                GmapTownPicDn,
                GmapTownViewPic,
                GmapTownViewPicDn,
                GmapTownViewPicMask,
                GmapTownInPicDn,
                GmapTownInPicMask,
                GmapTabsScrDnPicDn,
                GmapStayPic,
                GmapStayPicDn,
                GmapTabsScrUpPicDn,
                GmapTownInPic,
                GmapTilesPic,
                GmapStayPicMask,
                GmapDayTimeAnim,
                GmapInvPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                GmapCha,
                GmapFix,
                GmapMain,
                GmapPip,
                GmapTown,
                GmapCar,
                GmapTabs,
                GmapDayTime,
                GmapInv,
                GmapMap,
                GmapMenu,
                GmapPanel,
                GmapTabsScrDn,
                GmapTabsScrUp
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                GmapName,
                GmapLock,
                GmapMessageBox,
                GmapTime
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return new FeatureKey[]{
                GmapTilesX,
                GmapTilesY,
                GmapTownInOffsX,
                GmapTownInOffsY,
                GmapTownViewOffsX,
                GmapTownViewOffsY
            };
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{GmapNameStepX, GmapNameStepY};
        }

    }

    // Ground pickup
    public static enum GroundPickup implements FeatureKey {
        GPickupCancel,
        GPickupCancelPicDn,
        GPickupHeightItem,
        GPickupInv,
        GPickupInvScrDn,
        GPickupInvScrDnPic,
        GPickupInvScrDnPicDn,
        GPickupInvScrDnPicNa,
        GPickupInvScrUp,
        GPickupInvScrUpPic,
        GPickupInvScrUpPicDn,
        GPickupInvScrUpPicNa,
        GPickupMainPic,
        GPickupWidthItem;

        @Override
        public FeatureKey getMainPic() {
            return GPickupMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return null;
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public String getPrefix() {
            return GROUND_PICKUP;
        }

        @Override
        public Type getType() {
            switch (this) {
                case GPickupCancelPicDn:
                case GPickupInvScrDnPic:
                case GPickupInvScrDnPicDn:
                case GPickupInvScrDnPicNa:
                case GPickupInvScrUpPic:
                case GPickupInvScrUpPicDn:
                case GPickupInvScrUpPicNa:
                case GPickupMainPic:
                    return Type.PIC;
                case GPickupCancel:
                case GPickupInv:
                case GPickupInvScrDn:
                case GPickupInvScrUp:
                    return Type.PIC_POS;
                case GPickupWidthItem:
                case GPickupHeightItem:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                GPickupCancelPicDn,
                GPickupInvScrDnPic,
                GPickupInvScrDnPicDn,
                GPickupInvScrDnPicNa,
                GPickupInvScrUpPic,
                GPickupInvScrUpPicDn,
                GPickupInvScrUpPicNa,
                GPickupMainPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                GPickupCancel,
                GPickupInv,
                GPickupInvScrDn,
                GPickupInvScrUp
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return null;
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{
                GPickupWidthItem,
                GPickupHeightItem
            };
        }

    }

    // Input box
    public static enum InputBox implements FeatureKey {
        IboxCancel,
        IboxCancelPicDn,
        IboxCancelText,
        IboxDone,
        IboxDonePicDn,
        IboxDoneText,
        IboxMain,
        IboxMainPic,
        IboxText,
        IboxTitle;

        @Override
        public String getPrefix() {
            return INPUT_BOX;
        }

        @Override
        public Type getType() {
            switch (this) {
                case IboxCancelPicDn:
                case IboxDonePicDn:
                case IboxMainPic:
                    return Type.PIC;
                case IboxCancel:
                case IboxDone:
                case IboxMain:
                    return Type.PIC_POS;
                case IboxCancelText:
                case IboxDoneText:
                case IboxText:
                case IboxTitle:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return IboxMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return IboxMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                IboxCancelPicDn,
                IboxDonePicDn,
                IboxMainPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                IboxCancel,
                IboxDone,
                IboxMain
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                IboxCancelText,
                IboxDoneText,
                IboxText,
                IboxTitle
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // (Game) Interface
    public static enum Interface implements FeatureKey {
        IntAc,
        IntAddMess,
        IntAddMessPicDn,
        IntAddMessWindow,
        IntAddMessWindowPic,
        IntAim,
        IntAimPic,
        IntAimX,
        IntAimY,
        IntAmmoCount,
        IntAmmoCountText,
        IntAp,
        IntApCost,
        IntApGreenPic,
        IntApMax,
        IntApRedPic,
        IntApStepX,
        IntApStepY,
        IntApYellowPic,
        IntBreakTime,
        IntBreakTimePic,
        IntCha,
        IntChaPicDn,
        IntChangeSlot,
        IntChangeSlotPicDn,
        IntCombat,
        IntCombatAnim,
        IntCombatEnd,
        IntCombatEndPicDn,
        IntCombatTurn,
        IntCombatTurnPicDn,
        IntFix,
        IntFixPicDn,
        IntHp,
        IntInv,
        IntInvPicDn,
        IntItem,
        IntItemOffsX,
        IntItemOffsY,
        IntItemPicDn,
        IntMain,
        IntMainPic,
        IntMap,
        IntMapPicDn,
        IntMenu,
        IntMenuPicDn,
        IntMess,
        IntMessFilter1,
        IntMessFilter1PicDn,
        IntMessFilter2,
        IntMessFilter2PicDn,
        IntMessLarge,
        IntPip,
        IntPipPicDn,
        IntSkill,
        IntSkillPicDn,
        IntUseX,
        IntUseY,
        IntWearProcent,
        IntWearProcentText,
        IntX;

        @Override
        public String getPrefix() {
            return INTRFACE;
        }

        @Override
        public Type getType() {
            switch (this) {
                case IntAddMessPicDn:
                case IntAddMessWindowPic:
                case IntAimPic:
                case IntApGreenPic:
                case IntApRedPic:
                case IntApYellowPic:
                case IntBreakTimePic:
                case IntChangeSlotPicDn:
                case IntCombatEndPicDn:
                case IntChaPicDn:
                case IntCombatTurnPicDn:
                case IntFixPicDn:
                case IntInvPicDn:
                case IntItemPicDn:
                case IntMainPic:
                case IntMapPicDn:
                case IntMenuPicDn:
                case IntMessFilter1PicDn:
                case IntMessFilter2PicDn:
                case IntPipPicDn:
                case IntSkillPicDn:
                case IntCombatAnim:
                    return Type.PIC;
                case IntAddMess:
                case IntAddMessWindow:
                case IntAim:
                case IntBreakTime:
                case IntChangeSlot:
                case IntCombatEnd:
                case IntMessFilter1:
                case IntMessFilter2:
                case IntSkill:
                case IntPip:
                case IntFix:
                case IntMap:
                case IntInv:
                case IntCha:
                case IntMenu:
                case IntAmmoCount:
                case IntWearProcent:
                case IntAp:
                case IntCombat:
                case IntCombatTurn:
                case IntMain:
                case IntItem:
                    return Type.PIC_POS;
                case IntAmmoCountText:
                case IntWearProcentText:
                case IntMess:
                case IntMessLarge:
                case IntApCost:
                case IntHp:
                case IntAc:
                    return Type.TXT;
                case IntAimX:
                case IntAimY:
                case IntUseX:
                case IntUseY:
                case IntX:
                case IntItemOffsX:
                case IntItemOffsY:
                    return Type.OFFSET;
                case IntApMax:
                case IntApStepX:
                case IntApStepY:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return IntMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return IntMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                IntAddMessPicDn,
                IntAddMessWindowPic,
                IntAimPic,
                IntApGreenPic,
                IntApRedPic,
                IntApYellowPic,
                IntBreakTimePic,
                IntChangeSlotPicDn,
                IntCombatEndPicDn,
                IntChaPicDn,
                IntCombatTurnPicDn,
                IntFixPicDn,
                IntInvPicDn,
                IntItemPicDn,
                IntMainPic,
                IntMapPicDn,
                IntMenuPicDn,
                IntMessFilter1PicDn,
                IntMessFilter2PicDn,
                IntPipPicDn,
                IntSkillPicDn,
                IntCombatAnim
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                IntAddMess,
                IntAddMessWindow,
                IntAim,
                IntBreakTime,
                IntChangeSlot,
                IntCombatEnd,
                IntMessFilter1,
                IntMessFilter2,
                IntSkill,
                IntPip,
                IntFix,
                IntMap,
                IntInv,
                IntCha,
                IntMenu,
                IntAmmoCount,
                IntWearProcent,
                IntAp,
                IntCombat,
                IntCombatTurn,
                IntMain,
                IntItem
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                IntAmmoCountText,
                IntWearProcentText,
                IntMess,
                IntMessLarge,
                IntApCost,
                IntHp
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return new FeatureKey[]{
                IntAimX,
                IntAimY,
                IntUseX,
                IntUseY,
                IntX,
                IntItemOffsX,
                IntItemOffsY
            };
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{
                IntApMax,
                IntApStepX,
                IntApStepY
            };
        }

    }

    // Inventory
    public static enum Inventory implements FeatureKey {
        InvArmor,
        InvChosen,
        InvHeightItem,
        InvInv,
        InvMain,
        InvMainPic,
        InvOk,
        InvOkPic,
        InvOkPicDn,
        InvScrDn,
        InvScrDnPic,
        InvScrDnPicDn,
        InvScrDnPicNa,
        InvScrUp,
        InvScrUpPic,
        InvScrUpPicDn,
        InvScrUpPicNa,
        InvSlot1,
        InvSlot2,
        InvText;

        @Override
        public String getPrefix() {
            return INVENTORY;
        }

        @Override
        public Type getType() {
            switch (this) {
                case InvMainPic:
                case InvOkPic:
                case InvOkPicDn:
                case InvScrDnPic:
                case InvScrDnPicDn:
                case InvScrDnPicNa:
                case InvScrUpPic:
                case InvScrUpPicDn:
                case InvScrUpPicNa:
                    return Type.PIC;
                case InvMain:
                case InvOk:
                case InvInv:
                case InvChosen:
                case InvArmor:
                case InvScrDn:
                case InvScrUp:
                case InvSlot1:
                case InvSlot2:
                    return Type.PIC_POS;
                case InvText:
                    return Type.TXT;
                case InvHeightItem:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return InvMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return InvMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                InvMainPic,
                InvOkPic,
                InvOkPicDn,
                InvScrDnPic,
                InvScrDnPicDn,
                InvScrDnPicNa,
                InvScrUpPic,
                InvScrUpPicDn,
                InvScrUpPicNa
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                InvMain,
                InvOk,
                InvInv,
                InvChosen,
                InvArmor,
                InvScrDn,
                InvScrUp,
                InvSlot1,
                InvSlot2
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{InvText};
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{InvHeightItem};
        }

    }

    // Popup menu
    public static enum PopUp implements FeatureKey {
        LMenuBagPic,
        LMenuBagPicDn,
        LMenuBarterHidePic,
        LMenuBarterHidePicDn,
        LMenuBarterOpenPic,
        LMenuBarterOpenPicDn,
        LMenuCancelPic,
        LMenuCancelPicDn,
        LMenuDropPic,
        LMenuDropPicDn,
        LMenuGMFollowPic,
        LMenuGMFollowPicDn,
        LMenuGmapKickPic,
        LMenuGmapKickPicDn,
        LMenuGmapRulePic,
        LMenuGmapRulePicDn,
        LMenuLookPic,
        LMenuLookPicDn,
        LMenuNodeHeight,
        LMenuPickItemPic,
        LMenuPickItemPicDn,
        LMenuPushPic,
        LMenuPushPicDn,
        LMenuRotatePic,
        LMenuRotatePicDn,
        LMenuSkillPic,
        LMenuSkillPicDn,
        LMenuSortDownPic,
        LMenuSortDownPicDn,
        LMenuSortUpPic,
        LMenuSortUpPicDn,
        LMenuTalkPic,
        LMenuTalkPicDn,
        LMenuUnloadPic,
        LMenuUnloadPicDn,
        LMenuUsePic,
        LMenuUsePicDn,
        LMenuVoteDownPic,
        LMenuVoteDownPicDn,
        LMenuVoteUpPic,
        LMenuVoteUpPicDn;

        @Override
        public String getPrefix() {
            return POP_UP;
        }

        @Override
        public Type getType() {
            switch (this) {
                case LMenuBagPic:
                case LMenuBagPicDn:
                case LMenuBarterHidePic:
                case LMenuBarterHidePicDn:
                case LMenuBarterOpenPic:
                case LMenuBarterOpenPicDn:
                case LMenuCancelPic:
                case LMenuCancelPicDn:
                case LMenuDropPic:
                case LMenuDropPicDn:
                case LMenuGMFollowPic:
                case LMenuGMFollowPicDn:
                case LMenuGmapKickPic:
                case LMenuGmapKickPicDn:
                case LMenuGmapRulePic:
                case LMenuGmapRulePicDn:
                case LMenuLookPic:
                case LMenuLookPicDn:
                case LMenuPickItemPic:
                case LMenuPickItemPicDn:
                case LMenuPushPic:
                case LMenuPushPicDn:
                case LMenuRotatePic:
                case LMenuRotatePicDn:
                case LMenuSkillPic:
                case LMenuSkillPicDn:
                case LMenuSortDownPic:
                case LMenuSortDownPicDn:
                case LMenuSortUpPic:
                case LMenuSortUpPicDn:
                case LMenuTalkPic:
                case LMenuTalkPicDn:
                case LMenuUnloadPic:
                case LMenuUnloadPicDn:
                case LMenuUsePic:
                case LMenuUsePicDn:
                case LMenuVoteDownPic:
                case LMenuVoteDownPicDn:
                case LMenuVoteUpPic:
                case LMenuVoteUpPicDn:
                    return Type.PIC;
                case LMenuNodeHeight:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return null;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return null;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                LMenuBagPic,
                LMenuBagPicDn,
                LMenuBarterHidePic,
                LMenuBarterHidePicDn,
                LMenuBarterOpenPic,
                LMenuBarterOpenPicDn,
                LMenuCancelPic,
                LMenuCancelPicDn,
                LMenuDropPic,
                LMenuDropPicDn,
                LMenuGMFollowPic,
                LMenuGMFollowPicDn,
                LMenuGmapKickPic,
                LMenuGmapKickPicDn,
                LMenuGmapRulePic,
                LMenuGmapRulePicDn,
                LMenuLookPic,
                LMenuLookPicDn,
                LMenuPickItemPic,
                LMenuPickItemPicDn,
                LMenuPushPic,
                LMenuPushPicDn,
                LMenuRotatePic,
                LMenuRotatePicDn,
                LMenuSkillPic,
                LMenuSkillPicDn,
                LMenuSortDownPic,
                LMenuSortDownPicDn,
                LMenuSortUpPic,
                LMenuSortUpPicDn,
                LMenuTalkPic,
                LMenuTalkPicDn,
                LMenuUnloadPic,
                LMenuUnloadPicDn,
                LMenuUsePic,
                LMenuUsePicDn,
                LMenuVoteDownPic,
                LMenuVoteDownPicDn,
                LMenuVoteUpPic,
                LMenuVoteUpPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return null;
        }

        @Override
        public FeatureKey[] getTextValues() {
            return null;
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{LMenuNodeHeight};
        }

    }

    // Mini-map
    public static enum MiniMap implements FeatureKey {
        LmapLoHi,
        LmapLoHiPicDn,
        LmapMain,
        LmapMainPic,
        LmapMap,
        LmapOk,
        LmapOkPicDn,
        LmapScan,
        LmapScanPicDn;

        @Override
        public String getPrefix() {
            return MINI_MAP;
        }

        @Override
        public Type getType() {
            switch (this) {
                case LmapLoHiPicDn:
                case LmapMainPic:
                case LmapOkPicDn:
                case LmapScanPicDn:
                    return Type.PIC;
                case LmapLoHi:
                case LmapMain:
                case LmapMap:
                case LmapOk:
                case LmapScan:
                    return Type.PIC_POS;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return LmapMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return LmapMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                LmapLoHiPicDn,
                LmapMainPic,
                LmapOkPicDn,
                LmapScanPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                LmapLoHi,
                LmapMain,
                LmapMap,
                LmapOk,
                LmapScan
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return null;
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Login (Main menu)
    public static enum Login implements FeatureKey {
        LogCredits,
        LogCreditsPicDn,
        LogCreditsText,
        LogExit,
        LogExitPicDn,
        LogExitText,
        LogMain,
        LogMainPic,
        LogMessageBox,
        LogName,
        LogOptions,
        LogOptionsPicDn,
        LogOptionsText,
        LogPass,
        LogPlay,
        LogPlayPicDn,
        LogPlayText,
        LogReg,
        LogRegPicDn,
        LogRegText,
        LogSingleplayerMainPic,
        LogVersion;

        @Override
        public String getPrefix() {
            return LOGIN;
        }

        @Override
        public Type getType() {
            switch (this) {
                case LogCreditsPicDn:
                case LogExitPicDn:
                case LogMainPic:
                case LogOptionsPicDn:
                case LogPlayPicDn:
                case LogRegPicDn:
                case LogSingleplayerMainPic:
                    return Type.PIC;
                case LogCredits:
                case LogExit:
                case LogMain:
                case LogOptions:
                case LogPlay:
                case LogReg:
                    return Type.PIC_POS;
                case LogMessageBox:
                case LogVersion:
                case LogCreditsText:
                case LogExitText:
                case LogName:
                case LogOptionsText:
                case LogPass:
                case LogPlayText:
                case LogRegText:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return LogMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return LogMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                LogCreditsPicDn,
                LogExitPicDn,
                LogMainPic,
                LogOptionsPicDn,
                LogPlayPicDn,
                LogRegPicDn,
                LogSingleplayerMainPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                LogCredits,
                LogExit,
                LogMain,
                LogOptions,
                LogPlay,
                LogReg
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                LogMessageBox,
                LogVersion,
                LogCreditsText,
                LogExitText,
                LogName,
                LogOptionsText,
                LogPass,
                LogPlayText,
                LogRegText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Options
    public static enum Options implements FeatureKey {
        MoptExit,
        MoptExitPicDn,
        MoptLoadGame,
        MoptLoadGamePicDn,
        MoptMain,
        MoptMainPic,
        MoptOptions,
        MoptOptionsPicDn,
        MoptResume,
        MoptResumePicDn,
        MoptSaveGame,
        MoptSaveGamePicDn,
        MoptSingleplayerMainPic;

        @Override
        public String getPrefix() {
            return OPTIONS;
        }

        @Override
        public Type getType() {
            switch (this) {
                case MoptExitPicDn:
                case MoptLoadGamePicDn:
                case MoptMainPic:
                case MoptOptionsPicDn:
                case MoptResumePicDn:
                case MoptSaveGamePicDn:
                case MoptSingleplayerMainPic:
                    return Type.PIC;
                case MoptExit:
                case MoptLoadGame:
                case MoptMain:
                case MoptOptions:
                case MoptResume:
                case MoptSaveGame:
                    return Type.PIC_POS;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return MoptMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return MoptMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                MoptExitPicDn,
                MoptLoadGamePicDn,
                MoptMainPic,
                MoptOptionsPicDn,
                MoptResumePicDn,
                MoptSaveGamePicDn,
                MoptSingleplayerMainPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                MoptExit,
                MoptLoadGame,
                MoptMain,
                MoptOptions,
                MoptResume,
                MoptSaveGame
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return null;
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Price setup
    public static enum PriceSetup implements FeatureKey {
        PSHeightItem,
        PSInv,
        PSInvScrDn,
        PSInvScrDnPic,
        PSInvScrDnPicDn,
        PSInvScrDnPicNa,
        PSInvScrUp,
        PSInvScrUpPic,
        PSInvScrUpPicDn,
        PSInvScrUpPicNa,
        PSItem,
        PSMainPic,
        PSValue,
        PSWidthItem,
        PScancel,
        PScancelPicDn,
        PSdone,
        PSdonePicDn;

        @Override
        public String getPrefix() {
            return PRICE_SETUP;
        }

        @Override
        public Type getType() {
            switch (this) {
                case PSInvScrDnPic:
                case PSInvScrDnPicDn:
                case PSInvScrDnPicNa:
                case PSInvScrUpPic:
                case PSInvScrUpPicDn:
                case PSInvScrUpPicNa:
                case PSMainPic:
                case PScancelPicDn:
                case PSdonePicDn:
                    return Type.PIC;
                case PSInvScrDn:
                case PSInvScrUp:
                case PSdone:
                case PScancel:
                case PSInv:
                case PSItem:
                    return Type.PIC_POS;
                case PSValue:
                    return Type.TXT;
                case PSHeightItem:
                case PSWidthItem:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return PSMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return null;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                PSInvScrDnPic,
                PSInvScrDnPicDn,
                PSInvScrDnPicNa,
                PSInvScrUpPic,
                PSInvScrUpPicDn,
                PSInvScrUpPicNa,
                PSMainPic,
                PScancelPicDn,
                PSdonePicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                PSInvScrDn,
                PSInvScrUp,
                PSdone,
                PScancel,
                PSInv,
                PSItem
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{PSValue};
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{
                PSHeightItem,
                PSWidthItem
            };
        }

    }

    // Perk window
    public static enum Perk implements FeatureKey {
        PerkCancel,
        PerkCancelPic,
        PerkCancelText,
        PerkMain,
        PerkMainPic,
        PerkMainTitle,
        PerkNextX,
        PerkNextY,
        PerkOk,
        PerkOkPic,
        PerkOkText,
        PerkPerks,
        PerkPic,
        PerkScrDn,
        PerkScrDnPic,
        PerkScrUp,
        PerkScrUpPic,
        PerkText;

        @Override
        public String getPrefix() {
            return PERK;
        }

        @Override
        public Type getType() {
            switch (this) {
                case PerkCancelPic:
                case PerkMainPic:
                case PerkOkPic:
                case PerkScrDnPic:
                case PerkScrUpPic:
                    return Type.PIC;
                case PerkCancel:
                case PerkMain:
                case PerkOk:
                case PerkScrDn:
                case PerkScrUp:
                case PerkPic:
                    return Type.PIC_POS;
                case PerkCancelText:
                case PerkMainTitle:
                case PerkOkText:
                case PerkPerks:
                case PerkText:
                    return Type.TXT;
                case PerkNextX:
                case PerkNextY:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return PerkMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return PerkMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                PerkCancelPic,
                PerkMainPic,
                PerkOkPic,
                PerkScrDnPic,
                PerkScrUpPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                PerkCancel,
                PerkMain,
                PerkOk,
                PerkScrDn,
                PerkScrUp,
                PerkPic
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                PerkCancelText,
                PerkMainTitle,
                PerkOkText,
                PerkPerks,
                PerkText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{PerkNextX, PerkNextY};
        }

    }

    // Pip-boy
    public static enum PipBoy implements FeatureKey {
        PipArchives,
        PipArchivesPicDn,
        PipAutomaps,
        PipAutomapsPicDn,
        PipClose,
        PipClosePicDn,
        PipMain,
        PipMainPic,
        PipMonitor,
        PipMonitorPic,
        PipStatus,
        PipStatusPicDn,
        PipTime;

        @Override
        public String getPrefix() {
            return PIP_BOY;
        }

        @Override
        public Type getType() {
            switch (this) {
                case PipArchivesPicDn:
                case PipAutomapsPicDn:
                case PipClosePicDn:
                case PipMonitorPic:
                case PipStatusPicDn:
                case PipMainPic:
                    return Type.PIC;
                case PipArchives:
                case PipAutomaps:
                case PipClose:
                case PipMonitor:
                case PipMain:
                case PipStatus:
                    return Type.PIC_POS;
                case PipTime:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return PipMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return PipMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                PipArchivesPicDn,
                PipAutomapsPicDn,
                PipClosePicDn,
                PipMonitorPic,
                PipStatusPicDn,
                PipMainPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                PipArchives,
                PipAutomaps,
                PipClose,
                PipMonitor,
                PipMain,
                PipStatus
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{PipTime};
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Pick up
    public static enum PickUp implements FeatureKey {
        PupCont1,
        PupCont2,
        PupHeightCont1,
        PupHeightCont2,
        PupInfo,
        PupMain,
        PupMainPic,
        PupNextCritLeft,
        PupNextCritLeftPic,
        PupNextCritLeftPicDn,
        PupNextCritRight,
        PupNextCritRightPic,
        PupNextCritRightPicDn,
        PupOk,
        PupOkPicDn,
        PupScrDw1,
        PupScrDw1PicDn,
        PupScrDw1PicOff,
        PupScrDw2,
        PupScrDw2PicDn,
        PupScrDw2PicOff,
        PupScrUp1,
        PupScrUp1PicDn,
        PupScrUp1PicOff,
        PupScrUp2,
        PupScrUp2PicDn,
        PupScrUp2PicOff,
        PupTAPicDn,
        PupTakeAll;

        @Override
        public String getPrefix() {
            return PICK_UP;
        }

        @Override
        public Type getType() {
            switch (this) {
                case PupMainPic:
                case PupNextCritLeftPic:
                case PupNextCritLeftPicDn:
                case PupNextCritRightPic:
                case PupScrDw1PicDn:
                case PupScrDw1PicOff:
                case PupNextCritRightPicDn:
                case PupScrDw2PicDn:
                case PupScrDw2PicOff:
                case PupScrUp1PicDn:
                case PupScrUp2PicDn:
                case PupScrUp1PicOff:
                case PupScrUp2PicOff:
                case PupTAPicDn:
                case PupOkPicDn:
                    return Type.PIC;
                case PupMain:
                case PupNextCritLeft:
                case PupNextCritRight:
                case PupScrUp1:
                case PupScrUp2:
                case PupScrDw1:
                case PupScrDw2:
                case PupCont1:
                case PupCont2:
                case PupOk:
                case PupTakeAll:
                case PupInfo:
                    return Type.PIC_POS;
                case PupHeightCont1:
                case PupHeightCont2:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return PupMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return PupMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                PupMainPic,
                PupNextCritLeftPic,
                PupNextCritLeftPicDn,
                PupNextCritRightPic,
                PupScrDw1PicDn,
                PupScrDw1PicOff,
                PupNextCritRightPicDn,
                PupScrDw2PicDn,
                PupScrDw2PicOff,
                PupScrUp1PicDn,
                PupScrUp2PicDn,
                PupScrUp1PicOff,
                PupScrUp2PicOff,
                PupTAPicDn,
                PupOkPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                PupMain,
                PupNextCritLeft,
                PupNextCritRight,
                PupScrUp1,
                PupScrUp2,
                PupScrDw1,
                PupScrDw2,
                PupCont1,
                PupCont2,
                PupOk,
                PupTakeAll,
                PupInfo
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return null;
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{
                PupHeightCont1,
                PupHeightCont2
            };
        }

    }

    // Radio
    public static enum Radio implements FeatureKey {
        RadioBroadcast1Text,
        RadioBroadcast2Text,
        RadioBroadcast3Text,
        RadioBroadcast4Text,
        RadioBroadcast5Text,
        RadioBroadcastRecv,
        RadioBroadcastRecv1,
        RadioBroadcastRecv1PicDn,
        RadioBroadcastRecv2,
        RadioBroadcastRecv2PicDn,
        RadioBroadcastRecv3,
        RadioBroadcastRecv3PicDn,
        RadioBroadcastRecv4,
        RadioBroadcastRecv4PicDn,
        RadioBroadcastRecv5,
        RadioBroadcastRecv5PicDn,
        RadioBroadcastRecvPicDn,
        RadioBroadcastRecvText,
        RadioBroadcastSend,
        RadioBroadcastSend1,
        RadioBroadcastSend1PicDn,
        RadioBroadcastSend2,
        RadioBroadcastSend2PicDn,
        RadioBroadcastSend3,
        RadioBroadcastSend3PicDn,
        RadioBroadcastSend4,
        RadioBroadcastSend4PicDn,
        RadioBroadcastSend5,
        RadioBroadcastSend5PicDn,
        RadioBroadcastSendPicDn,
        RadioBroadcastSendText,
        RadioChannel,
        RadioChannelText,
        RadioMain,
        RadioMainPic,
        RadioMainText,
        RadioRefresh,
        RadioRefreshPicDn,
        RadioRefreshText;

        @Override
        public String getPrefix() {
            return RADIO;
        }

        @Override
        public Type getType() {
            switch (this) {
                case RadioBroadcastRecv1PicDn:
                case RadioBroadcastRecv2PicDn:
                case RadioBroadcastRecv3PicDn:
                case RadioBroadcastRecv4PicDn:
                case RadioBroadcastRecv5PicDn:
                case RadioBroadcastRecvPicDn:
                case RadioBroadcastSend1PicDn:
                case RadioBroadcastSend2PicDn:
                case RadioBroadcastSend3PicDn:
                case RadioBroadcastSend4PicDn:
                case RadioBroadcastSend5PicDn:
                case RadioBroadcastSendPicDn:
                case RadioMainPic:
                case RadioRefreshPicDn:
                    return Type.PIC;
                case RadioBroadcastRecv:
                case RadioBroadcastRecv1:
                case RadioBroadcastRecv2:
                case RadioBroadcastRecv3:
                case RadioBroadcastRecv4:
                case RadioBroadcastRecv5:
                case RadioBroadcastSend:
                case RadioBroadcastSend1:
                case RadioBroadcastSend2:
                case RadioBroadcastSend3:
                case RadioBroadcastSend4:
                case RadioBroadcastSend5:
                case RadioChannel:
                case RadioMain:
                case RadioRefresh:
                    return Type.PIC_POS;
                case RadioBroadcast1Text:
                case RadioBroadcast2Text:
                case RadioBroadcast3Text:
                case RadioBroadcast4Text:
                case RadioBroadcast5Text:
                case RadioBroadcastRecvText:
                case RadioBroadcastSendText:
                case RadioChannelText:
                case RadioMainText:
                case RadioRefreshText:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return RadioMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return RadioMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                RadioBroadcastRecv1PicDn,
                RadioBroadcastRecv2PicDn,
                RadioBroadcastRecv3PicDn,
                RadioBroadcastRecv4PicDn,
                RadioBroadcastRecv5PicDn,
                RadioBroadcastRecvPicDn,
                RadioBroadcastSend1PicDn,
                RadioBroadcastSend2PicDn,
                RadioBroadcastSend3PicDn,
                RadioBroadcastSend4PicDn,
                RadioBroadcastSend5PicDn,
                RadioBroadcastSendPicDn,
                RadioMainPic,
                RadioRefreshPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                RadioBroadcastRecv,
                RadioBroadcastRecv1,
                RadioBroadcastRecv2,
                RadioBroadcastRecv3,
                RadioBroadcastRecv4,
                RadioBroadcastRecv5,
                RadioBroadcastSend,
                RadioBroadcastSend1,
                RadioBroadcastSend2,
                RadioBroadcastSend3,
                RadioBroadcastSend4,
                RadioBroadcastSend5,
                RadioChannel,
                RadioMain,
                RadioRefresh
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                RadioBroadcast1Text,
                RadioBroadcast2Text,
                RadioBroadcast3Text,
                RadioBroadcast4Text,
                RadioBroadcast5Text,
                RadioBroadcastRecvText,
                RadioBroadcastSendText,
                RadioChannelText,
                RadioMainText,
                RadioRefreshText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Registration
    public static enum Registration implements FeatureKey {
        RegMain,
        RegMainPic,
        RegSpecialMinus,
        RegSpecialMinusPicDn,
        RegSpecialNextX,
        RegSpecialNextY,
        RegSpecialPlus,
        RegSpecialPlusPicDn,
        RegTagSkill,
        RegTagSkillNextX,
        RegTagSkillNextY,
        RegTagSkillPicDn,
        RegTraitL,
        RegTraitLText,
        RegTraitNextX,
        RegTraitNextY,
        RegTraitPicDn,
        RegTraitR,
        RegTraitRText,
        RegUnspentSpecial,
        RegUnspentSpecialText;

        @Override
        public String getPrefix() {
            return REGISTRATION;
        }

        @Override
        public Type getType() {
            switch (this) {
                case RegMainPic:
                case RegSpecialMinusPicDn:
                case RegSpecialPlusPicDn:
                case RegTagSkillPicDn:
                case RegTraitPicDn:
                    return Type.PIC;
                case RegMain:
                case RegSpecialMinus:
                case RegSpecialPlus:
                case RegTagSkill:
                case RegTraitL:
                case RegTraitR:
                case RegUnspentSpecial:
                    return Type.PIC_POS;
                case RegTraitLText:
                case RegTraitRText:
                case RegUnspentSpecialText:
                    return Type.TXT;
                case RegSpecialNextX:
                case RegSpecialNextY:
                case RegTagSkillNextX:
                case RegTagSkillNextY:
                case RegTraitNextX:
                case RegTraitNextY:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return RegMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return RegMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                RegMainPic,
                RegSpecialMinusPicDn,
                RegSpecialPlusPicDn,
                RegTagSkillPicDn,
                RegTraitPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                RegMain,
                RegSpecialMinus,
                RegSpecialPlus,
                RegTagSkill,
                RegTraitL,
                RegTraitR,
                RegUnspentSpecial
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                RegTraitLText,
                RegTraitRText,
                RegUnspentSpecialText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{
                RegSpecialNextX,
                RegSpecialNextY,
                RegTagSkillNextX,
                RegTagSkillNextY,
                RegTraitNextX,
                RegTraitNextY
            };
        }

    }

    // Save/Load
    public static enum SaveLoad implements FeatureKey {
        SaveLoadBack,
        SaveLoadBackPicDn,
        SaveLoadBackText,
        SaveLoadDone,
        SaveLoadDonePicDn,
        SaveLoadDoneText,
        SaveLoadInfo,
        SaveLoadMain,
        SaveLoadMainPic,
        SaveLoadPic,
        SaveLoadScrDown,
        SaveLoadScrDownPicDn,
        SaveLoadScrUp,
        SaveLoadScrUpPicDn,
        SaveLoadSlots,
        SaveLoadText;

        @Override
        public String getPrefix() {
            return SAVE_LOAD;
        }

        @Override
        public Type getType() {
            switch (this) {
                case SaveLoadBackPicDn:
                case SaveLoadDonePicDn:
                case SaveLoadMainPic:
                case SaveLoadPic:
                case SaveLoadScrUpPicDn:
                case SaveLoadScrDownPicDn:
                    return Type.PIC;
                case SaveLoadBack:
                case SaveLoadDone:
                case SaveLoadMain:
                case SaveLoadScrDown:
                case SaveLoadScrUp:
                case SaveLoadSlots:
                    return Type.PIC_POS;
                case SaveLoadBackText:
                case SaveLoadDoneText:
                case SaveLoadInfo:
                case SaveLoadText:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return SaveLoadMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return SaveLoadMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                SaveLoadBackPicDn,
                SaveLoadDonePicDn,
                SaveLoadMainPic,
                SaveLoadPic,
                SaveLoadScrUpPicDn,
                SaveLoadScrDownPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                SaveLoadBack,
                SaveLoadDone,
                SaveLoadMain,
                SaveLoadScrDown,
                SaveLoadScrUp,
                SaveLoadSlots
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                SaveLoadBackText,
                SaveLoadDoneText,
                SaveLoadInfo,
                SaveLoadText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Say box
    public static enum SayBox implements FeatureKey {
        SayCancel,
        SayCancelPicDn,
        SayCancelText,
        SayMain,
        SayMainPic,
        SayMainText,
        SayOk,
        SayOkPicDn,
        SayOkText,
        SaySay;

        @Override
        public String getPrefix() {
            return "Say";
        }

        @Override
        public Type getType() {
            switch (this) {
                case SayCancelPicDn:
                case SayMainPic:
                case SayOkPicDn:
                    return Type.PIC;
                case SayCancel:
                case SayMain:
                case SayOk:
                case SaySay:
                    return Type.PIC_POS;
                case SayCancelText:
                case SayMainText:
                case SayOkText:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return SayMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return SayMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                SayCancelPicDn,
                SayMainPic,
                SayOkPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                SayCancel,
                SayMain,
                SayOk,
                SaySay
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                SayCancelText,
                SayMainText,
                SayOkText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Skillbox
    public static enum SkillBox implements FeatureKey {
        SboxCancel,
        SboxCancelPicDn,
        SboxCancelText,
        SboxDoctor,
        SboxDoctorPicDn,
        SboxDoctorText,
        SboxFirstAid,
        SboxFirstAidPicDn,
        SboxFirstAidText,
        SboxLockpick,
        SboxLockpickPicDn,
        SboxLockpickText,
        SboxMain,
        SboxMainPic,
        SboxMainText,
        SboxRepair,
        SboxRepairPicDn,
        SboxRepairText,
        SboxScience,
        SboxSciencePicDn,
        SboxScienceText,
        SboxSneak,
        SboxSneakPicDn,
        SboxSneakText,
        SboxSteal,
        SboxStealPicDn,
        SboxStealText,
        SboxTrap,
        SboxTrapPicDn,
        SboxTrapText;

        @Override
        public String getPrefix() {
            return SKILL_BOX;
        }

        @Override
        public Type getType() {
            switch (this) {
                case SboxCancelPicDn:
                case SboxDoctorPicDn:
                case SboxFirstAidPicDn:
                case SboxLockpickPicDn:
                case SboxMainPic:
                case SboxRepairPicDn:
                case SboxSciencePicDn:
                case SboxSneakPicDn:
                case SboxStealPicDn:
                case SboxTrapPicDn:
                    return Type.PIC;
                case SboxCancel:
                case SboxDoctor:
                case SboxFirstAid:
                case SboxLockpick:
                case SboxMain:
                case SboxRepair:
                case SboxScience:
                case SboxSneak:
                case SboxSteal:
                case SboxTrap:
                    return Type.PIC_POS;
                case SboxCancelText:
                case SboxFirstAidText:
                case SboxDoctorText:
                case SboxLockpickText:
                case SboxMainText:
                case SboxRepairText:
                case SboxScienceText:
                case SboxSneakText:
                case SboxStealText:
                case SboxTrapText:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return SboxMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return SboxMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                SboxCancelPicDn,
                SboxDoctorPicDn,
                SboxFirstAidPicDn,
                SboxLockpickPicDn,
                SboxMainPic,
                SboxRepairPicDn,
                SboxSciencePicDn,
                SboxSneakPicDn,
                SboxStealPicDn,
                SboxTrapPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                SboxCancel,
                SboxDoctor,
                SboxFirstAid,
                SboxLockpick,
                SboxMain,
                SboxRepair,
                SboxScience,
                SboxSneak,
                SboxSteal,
                SboxTrap
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                SboxCancelText,
                SboxFirstAidText,
                SboxDoctorText,
                SboxLockpickText,
                SboxMainText,
                SboxRepairText,
                SboxScienceText,
                SboxSneakText,
                SboxStealText,
                SboxTrapText
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Split
    public static enum Split implements FeatureKey {
        SplitAll,
        SplitAllPicDn,
        SplitCancel,
        SplitCancelPic,
        SplitDone,
        SplitDonePic,
        SplitDown,
        SplitDownPicDn,
        SplitItem,
        SplitMain,
        SplitMainPic,
        SplitTitle,
        SplitUp,
        SplitUpPicDn,
        SplitValue;

        @Override
        public String getPrefix() {
            return SPLIT;
        }

        @Override
        public Type getType() {
            switch (this) {
                case SplitAllPicDn:
                case SplitCancelPic:
                case SplitDonePic:
                case SplitDownPicDn:
                case SplitMainPic:
                case SplitUpPicDn:
                    return Type.PIC;
                case SplitAll:
                case SplitCancel:
                case SplitDone:
                case SplitDown:
                case SplitMain:
                case SplitUp:
                case SplitItem:
                    return Type.PIC_POS;
                case SplitTitle:
                case SplitValue:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return SplitMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return SplitMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                SplitAllPicDn,
                SplitCancelPic,
                SplitDonePic,
                SplitDownPicDn,
                SplitMainPic,
                SplitUpPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                SplitAll,
                SplitCancel,
                SplitDone,
                SplitDown,
                SplitMain,
                SplitUp,
                SplitItem
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                SplitTitle,
                SplitValue
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Town view
    public static enum TownView implements FeatureKey {
        TViewBack,
        TViewBackPicDn,
        TViewContours,
        TViewContoursPicDn,
        TViewEnter,
        TViewEnterPicDn,
        TViewMain,
        TViewMainPic;

        @Override
        public String getPrefix() {
            return TOWN_VIEW;
        }

        @Override
        public Type getType() {
            switch (this) {
                case TViewBackPicDn:
                case TViewContoursPicDn:
                case TViewEnterPicDn:
                case TViewMainPic:
                    return Type.PIC;
                case TViewBack:
                case TViewContours:
                case TViewEnter:
                case TViewMain:
                    return Type.PIC_POS;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return TViewMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return TViewMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                TViewBackPicDn,
                TViewContoursPicDn,
                TViewEnterPicDn,
                TViewMainPic
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                TViewBack,
                TViewContours,
                TViewEnter,
                TViewMain
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return null;
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Timer
    public static enum Timer implements FeatureKey {
        TimerCancel,
        TimerCancelPicDn,
        TimerDone,
        TimerDonePicDn,
        TimerDown,
        TimerDownPicDn,
        TimerItem,
        TimerMain,
        TimerMainPic,
        TimerTitle,
        TimerUp,
        TimerUpPicDn,
        TimerValue;

        @Override
        public String getPrefix() {
            return TIMER;
        }

        @Override
        public Type getType() {
            switch (this) {
                case TimerCancelPicDn:
                case TimerDonePicDn:
                case TimerDownPicDn:
                case TimerMainPic:
                case TimerUpPicDn:
                    return Type.PIC;
                case TimerCancel:
                case TimerDone:
                case TimerDown:
                case TimerUp:
                case TimerMain:
                case TimerItem:
                    return Type.PIC_POS;
                case TimerTitle:
                case TimerValue:
                    return Type.TXT;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return TimerMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return TimerMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                TimerCancelPicDn,
                TimerDonePicDn,
                TimerDownPicDn,
                TimerMainPic,
                TimerUpPicDn
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                TimerCancel,
                TimerDone,
                TimerDown,
                TimerUp,
                TimerMain,
                TimerItem
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return new FeatureKey[]{
                TimerTitle,
                TimerValue
            };
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return null;
        }

    }

    // Use
    public static enum Use implements FeatureKey {
        UseCancel,
        UseCancelPic,
        UseCancelPicDn,
        UseChosen,
        UseHeightItem,
        UseInv,
        UseMain,
        UseMainPic,
        UseScrDown,
        UseScrDownPic,
        UseScrDownPicDn,
        UseScrDownPicOff,
        UseScrUp,
        UseScrUpPic,
        UseScrUpPicDn,
        UseScrUpPicOff;

        @Override
        public String getPrefix() {
            return USE;
        }

        @Override
        public Type getType() {
            switch (this) {
                case UseCancelPic:
                case UseCancelPicDn:
                case UseMainPic:
                case UseScrDownPic:
                case UseScrDownPicDn:
                case UseScrDownPicOff:
                case UseScrUpPic:
                case UseScrUpPicDn:
                case UseScrUpPicOff:
                    return Type.PIC;
                case UseCancel:
                case UseChosen:
                case UseMain:
                case UseScrDown:
                case UseScrUp:
                case UseInv:
                    return Type.PIC_POS;
                case UseHeightItem:
                    return Type.VALUE;
                default:
                    return Type.UNKNOWN;
            }
        }

        @Override
        public String getStringValue() {
            return this.name();
        }

        @Override
        public int getNumericValue() {
            return this.ordinal();
        }

        @Override
        public FeatureKey getMainPic() {
            return UseMainPic;
        }

        @Override
        public FeatureKey getMainPicPos() {
            return UseMain;
        }

        @Override
        public FeatureKey[] getPicValues() {
            return new FeatureKey[]{
                UseCancelPic,
                UseCancelPicDn,
                UseMainPic,
                UseScrDownPic,
                UseScrDownPicDn,
                UseScrDownPicOff,
                UseScrUpPic,
                UseScrUpPicDn,
                UseScrUpPicOff
            };
        }

        @Override
        public FeatureKey[] getPicPosValues() {
            return new FeatureKey[]{
                UseCancel,
                UseChosen,
                UseMain,
                UseScrDown,
                UseScrUp,
                UseInv
            };
        }

        @Override
        public FeatureKey[] getTextValues() {
            return null;
        }

        @Override
        public FeatureKey[] getOffsetValues() {
            return null;
        }

        @Override
        public FeatureKey[] getSplitValues() {
            return new FeatureKey[]{UseHeightItem};
        }

    }

    /**
     * Type used to determine this Feature Key
     */
    public static enum Type {
        UNKNOWN, PIC, PIC_POS, TXT, VALUE, OFFSET
    }

    public static final String SPLIT_REGEX = ".*(Width|Height)$";
    public static final String OFFSET_REGEX = ".*(X|Y)?$";

    /**
     * Gets main picture (needed for module building)
     *
     * @return main picture
     */
    public FeatureKey getMainPic();

    /**
     * Gets main picture position (needed for module building)
     *
     * @return main picture position
     */
    public FeatureKey getMainPicPos();

    /**
     * Gets name of this feature key
     *
     * @return name as string value
     */
    public String getStringValue();

    /**
     * Gets int value (index in array) of this feature key
     *
     * @return ordinal as numeric value
     */
    public int getNumericValue();

    /**
     * Gets interface prefix of this feature key
     *
     * @return feature key prefix
     */
    public String getPrefix();

    /**
     * Gets all feats keys which are pictures
     *
     * @return all pic values
     */
    public FeatureKey[] getPicValues();

    /**
     * Gets all feats keys which are picture positions
     *
     * @return all pic pos values
     */
    public FeatureKey[] getPicPosValues();

    /**
     * Gets all feats keys which are text positions
     *
     * @return all text positions
     */
    public FeatureKey[] getTextValues();

    /**
     * Gets all feats keys which are offsets
     *
     * @return all offsets
     */
    public FeatureKey[] getOffsetValues();

    /**
     * Gets all feats keys which are split values
     *
     * @return all split values
     */
    public FeatureKey[] getSplitValues();

    /**
     * Gets feature type of this feature key {PIC, PIC_POS, TXT, etc}
     *
     * @return
     */
    public Type getType();

    /**
     * Parse this string into feature key
     *
     * @param string word to be parsed on left side of assignment
     * @return completed feature key or null if no exists
     */
    public static FeatureKey valueOf(String string) {
        for (String prefix : ABBRS) {
            if (string.toLowerCase().startsWith(prefix.toLowerCase())) {
                FeatureKey[] keys = valuesOf(prefix);
                Arrays.sort(keys);

                for (FeatureKey key : keys) {
                    if (key.getStringValue().equalsIgnoreCase(string)) {
                        return key;
                    }
                }

                return null;
            }
        }

        return null;
    }

    /**
     * Gets values of this Feature Key prefix
     *
     * @param prefix Feature Key prefix
     * @return array of possible keys
     */
    public static FeatureKey[] valuesOf(String prefix) {
        switch (prefix) {
            case AIM:
                return Aim.values();
            case BARTER:
                return Barter.values();
            case CHARACTER:
                return Character.values();
            case CHOSEN:
                return Chosen.values();
            case CONSOLE:
                return Console.values();
            case DIALOG_BOX:
                return Dialog.values();
            case FACTION:
                return Faction.values();
            case FIX_BOY:
                return FixBoy.values();
            case GLOBAL_MAP:
                return GlobalMap.values();
            case GROUND_PICKUP:
                return GroundPickup.values();
            case INPUT_BOX:
                return InputBox.values();
            case INTRFACE:
                return Interface.values();
            case INVENTORY:
                return Inventory.values();
            case LOGIN:
                return Login.values();
            case MINI_MAP:
                return MiniMap.values();
            case OPTIONS:
                return Options.values();
            case PERK:
                return Perk.values();
            case PICK_UP:
                return PickUp.values();
            case PIP_BOY:
                return PipBoy.values();
            case POP_UP:
                return PopUp.values();
            case PRICE_SETUP:
                return PriceSetup.values();
            case RADIO:
                return Radio.values();
            case REGISTRATION:
                return Registration.values();
            case SAVE_LOAD:
                return SaveLoad.values();
            case SAY_BOX:
                return SayBox.values();
            case SKILL_BOX:
                return SkillBox.values();
            case SPLIT:
                return Split.values();
            case TIMER:
                return Timer.values();
            case TOWN_VIEW:
                return TownView.values();
            case USE:
                return Use.values();
            default:
                return null;
        }
    }

    /**
     * Get pictures using possible picture position
     *
     * @param picPos picture position
     * @return list of pictures (null if picPos isn't picture position)
     */
    public static List<FeatureKey> getPics(FeatureKey picPos) {
        if (picPos.getType() == Type.PIC_POS) {
            List<FeatureKey> result = new ArrayList<>();

            String begin = picPos.getStringValue() + PIC_REGEX;
            final String altRegex = begin;

            begin = begin.replaceAll(FeatureValue.NUMBER_REGEX, "");
            begin = begin.replaceAll("Opponent", "");
            final boolean hasNumber = picPos.getStringValue().matches(
                    picPos.getStringValue().replaceAll(FeatureValue.NUMBER_REGEX, "") + FeatureValue.NUMBER_REGEX
            );
            final String regex = hasNumber ? begin + FeatureValue.NUMBER_REGEX : begin;

            for (FeatureKey key : picPos.getPicValues()) {
                if (key.getStringValue().matches(regex)
                        || key.getStringValue().matches(altRegex)) {
                    result.add(key);
                }
            }

            return result;
        }

        return null;
    }

    /**
     * Gets Offset for the given this Feature Key
     *
     * @param fk feature key
     * @return [X,Y] as pair
     */
    public static Pair<FeatureKey, FeatureKey> getOffset(FeatureKey fk) {
        FeatureKey fkx = null;
        FeatureKey fky = null;

        int len = 0;
        for (FeatureKey off : fk.getOffsetValues()) {
            String offStr = off.getStringValue();
            if (!offStr.contains(fk.getStringValue().replaceAll(PIC_REGEX, "").replaceAll("Text", ""))) {
                continue;
            }

            if (fk.getType() == FeatureKey.Type.PIC || fk.getType() == FeatureKey.Type.PIC_POS || fk.getType() == FeatureKey.Type.TXT) {
                if (offStr.endsWith("X")) {
                    fkx = off;
                    len++;
                } else if (offStr.endsWith("Y")) {
                    fky = off;
                    len++;
                }
            }

            if (len == 2) {
                break;
            }
        }

        if (fkx != null || fky != null) {
            return new Pair<>(fkx, fky);
        }

        return null;
    }

    /**
     * Gets Split Values for the given this Feature Key
     *
     * @param fk feature key
     * @return [Width, Height] as pair
     */
    public static Pair<FeatureKey, FeatureKey> getSplitValues(FeatureKey fk) {
        FeatureKey fkw = null;
        FeatureKey fkh = null;

        int len = 0;
        for (FeatureKey split : fk.getSplitValues()) {
            String offStr = split.getStringValue();
            if (!offStr.contains(fk.getStringValue().replaceAll(PIC_REGEX, "").replaceAll("Text", ""))) {
                continue;
            }

            if (fk.getType() == FeatureKey.Type.PIC || fk.getType() == FeatureKey.Type.PIC_POS || fk.getType() == FeatureKey.Type.TXT) {
                if (offStr.endsWith("Width")) {
                    fkw = split;
                    len++;
                } else if (offStr.endsWith("Height")) {
                    fkh = split;
                    len++;
                }
            }

            if (len == 2) {
                break;
            }
        }

        if (fkw != null || fkh != null) {
            return new Pair<>(fkw, fkh);
        }

        return null;
    }

}
