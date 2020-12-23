/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.intrface;

import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Section {

    protected final FeatureKey[] keys;

    public static enum SectionName {
        Aim, Barter, Character, Chosen, Console, DialogBox, FixBoy, GlobalMap,
        InputBox, Intrface, Inventory, PopUp, MiniMap, Login, Options, PriceSetup, Perk, PipBoy,
        PickUp, Radio, Registration, SaveLoad, SayBox, SkillBox, Split,
        TownView, Timer, Use
    };

    protected final SectionName sectionName;

    /**
     * Creates new section with given section name.One section should be created
     * only once unless it's resolution section.
     *
     * @param sectionName section name
     * @param keys keys of this section
     */
    public Section(SectionName sectionName, FeatureKey[] keys) {
        this.sectionName = sectionName;
        this.keys = keys;
    }

    /**
     * Gets name of the section
     *
     * @return section name
     */
    public SectionName getSectionName() {
        return sectionName;
    }

    /**
     * Gets keys of this section
     *
     * @return section keys
     */
    public FeatureKey[] getKeys() {
        return keys;
    }

}
