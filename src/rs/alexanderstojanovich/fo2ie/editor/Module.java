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
package rs.alexanderstojanovich.fo2ie.editor;

import com.jogamp.opengl.GL2;
import java.util.ArrayList;
import java.util.List;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.ShaderProgram;
import rs.alexanderstojanovich.fo2ie.util.Tree;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Module {

    protected final List<GLComponent> components = new ArrayList<>();

    /**
     * Builds this module from the component tree (main pic in the root)
     *
     * @param tree parsed component tree
     */
    public void build(Tree<GLComponent> tree) {
        components.clear();
        tree.preorder(components, tree.getRoot());
    }

    /**
     * Renders this module to the OpenGL canvas
     *
     * @param gl20 GL2.0 binding
     * @param prmSP primitive shader program
     * @param imgSP image shader program
     * @param fntSP font shader program
     */
    public void render(GL2 gl20, ShaderProgram prmSP, ShaderProgram imgSP, ShaderProgram fntSP) {
        for (GLComponent component : components) {
            if (!component.isBuffered()) {
                component.buffer(gl20);
            }

            switch (component.getType()) {
                case PRIM:
                    component.render(gl20, prmSP);
                    break;
                case PIC:
                    component.render(gl20, imgSP);
                    break;
                case TXT:
                    component.render(gl20, fntSP);
                    break;
            }

        }
    }

    public List<GLComponent> getComponents() {
        return components;
    }

}
