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
package rs.alexanderstojanovich.fo2ie.main;

import com.jogamp.opengl.GL2;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.joml.Matrix4f;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.ShaderProgram;
import rs.alexanderstojanovich.fo2ie.ogl.Text;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Module {

    protected final List<GLComponent> components = new ArrayList<>();

    protected final TimerTask unbufTask = new TimerTask() {
        @Override
        public void run() {
            for (GLComponent component : components) {
                component.unbuffer(); // hint that component should be refreshed again
            }
        }
    };

    public Module() {
        timer.schedule(unbufTask, 250L, 250L);
    }

    protected final Timer timer = new Timer("Module Timer Util");

    /**
     * Renders this module to the OpenGL canvas
     *
     * @param gl20 GL2.0 binding
     * @param projMat4 projection matrix
     * @param prmSP primitive shader program
     * @param imgSP image shader program
     * @param fntSP font shader program
     */
    public void render(GL2 gl20, Matrix4f projMat4, ShaderProgram prmSP, ShaderProgram imgSP, ShaderProgram fntSP) {
        for (GLComponent component : components) {
            if (!component.isBuffered()) {
                component.buffer(gl20);
            }

            switch (component.getType()) {
                case PIC:
                case ANIM:
                case ADDR:
                    component.render(gl20, projMat4, imgSP);
                    break;
                case TXT:
                    Text text = (Text) component;
                    text.render(gl20, projMat4, fntSP, prmSP);
                    break;
            }

        }

    }

    public List<GLComponent> getComponents() {
        return components;
    }

    /**
     * Stops timer util
     */
    public void stopTimer() {
        timer.cancel();
    }

    public TimerTask getUnbufTask() {
        return unbufTask;
    }

    public Timer getTimer() {
        return timer;
    }

}
