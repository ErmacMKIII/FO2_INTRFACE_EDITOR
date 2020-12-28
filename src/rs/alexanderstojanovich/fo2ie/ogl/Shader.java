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
package rs.alexanderstojanovich.fo2ie.ogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderUtil;
import java.nio.IntBuffer;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.PlainTextReader;

/**
 *
 * @author Coa
 */
public class Shader {

    private final int type;
    private final String src;
    private final String filename;

    private int shaderId;

    public static final int VERTEX_SHADER = GL2.GL_VERTEX_SHADER;
    public static final int FRAGMENT_SHADER = GL2.GL_FRAGMENT_SHADER;

    /**
     * Creates shader with provided stuff
     *
     * @param gl20 GL20 binding
     * @param filename provided shaderId filename
     * @param type shader type (either VERTEX or FRAGMENT)
     */
    public Shader(GL2 gl20, String filename, int type) {
        this.type = type;
        this.filename = filename;
        src = PlainTextReader.readFromFile(filename);
        if (src.length() > 0) {
            init(gl20);
        } else {
            FO2IELogger.reportError("Invalid shader filename!", null);
        }
    }

    private void init(GL2 gl20) {
        // creating the shaderId
        IntBuffer intBuffer = GLBuffers.newDirectIntBuffer(1);
        ShaderUtil.createShader(gl20, type, intBuffer);
        shaderId = intBuffer.get(0);
        if (shaderId == 0) {
            FO2IELogger.reportError("Shader creation failed!", null);
        }
        // compiling the shaderId
        String[] srcs = {src};
        ShaderUtil.shaderSource(gl20, shaderId, srcs);
        ShaderUtil.compileShader(gl20, intBuffer);

        // debugging
        if (!ShaderUtil.isShaderStatusValid(gl20, shaderId, GL2.GL_COMPILE_STATUS, null)) {
            int size = intBuffer.get(0);
            if (size > 0) {
                String shaderInfoLog = ShaderUtil.getShaderInfoLog(gl20, shaderId);
                FO2IELogger.reportError("@" + filename, null);
                FO2IELogger.reportError(shaderInfoLog, null);
            }
        }
    }

    public int getType() {
        return type;
    }

    public String getSrc() {
        return src;
    }

    public int getShaderId() {
        return shaderId;
    }

}
