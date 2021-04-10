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

import java.util.ArrayList;
import java.util.List;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Section {

    protected final FeatureKey root;
    protected final FeatureKey[] keys;

    public static enum SectionName {
        Aim, Barter, Character, Chosen, Console, DialogBox, Faction, FixBoy, GlobalMap, GroundPickup,
        InputBox, Intrface, Inventory, PopUp, MiniMap, Login, Options, PriceSetup, Perk, PipBoy,
        PickUp, Radio, Registration, SaveLoad, SayBox, SkillBox, Split,
        TownView, Timer, Use
    };

    protected final SectionName sectionName;

    protected List<FeatureKey> pics = new ArrayList<>();
    protected List<FeatureKey> picpos = new ArrayList<>();
    protected List<FeatureKey> text = new ArrayList<>();
    protected List<FeatureKey> vals = new ArrayList<>();

    /**
     * Creates new section with given section name.One section should be created
     * only once unless it's resolution section.
     *
     * @param sectionName section name
     * @param root main image of this section (can be null)
     * @param keys keys of this section
     */
    public Section(SectionName sectionName, FeatureKey root, FeatureKey[] keys) {
        this.sectionName = sectionName;
        this.root = root;
        this.keys = keys;
        init(); // initalize split lists
    }

    /**
     * Creates separates lists of pics, pic pos and text
     */
    private void init() {
        for (FeatureKey fk : keys) {
            switch (fk.getType()) {
                case PIC:
                    pics.add(fk);
                    break;
                case PIC_POS:
                    picpos.add(fk);
                    break;
                case TXT:
                    text.add(fk);
                    break;
                case VALUE:
                    vals.add(fk);
                    break;
            }
        }
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

    /**
     * Gets root of this section (aka main picture)
     *
     * @return section root
     */
    public FeatureKey getRoot() {
        return root;
    }

}
