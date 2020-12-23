/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.intrface;

import java.awt.Font;
import rs.alexanderstojanovich.fo2ie.feature.Vector4;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class TextComponent {

    private final Vector4 pos;
    private final Font font;

    public TextComponent(Vector4 pos, Font font) {
        this.pos = pos;
        this.font = font;
    }

    public Vector4 getPos() {
        return pos;
    }

    public Font getFont() {
        return font;
    }

}
