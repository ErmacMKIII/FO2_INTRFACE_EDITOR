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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Coa
 */
public class ShaderProgram {

    private final int programId; // made to link all the shaders    
    private final Shader vertex;
    private final Shader fragment;

    public ShaderProgram(GL2 gl20, Shader vertex, Shader fragment) {
        this.programId = gl20.glCreateProgram();
        this.vertex = vertex;
        this.fragment = fragment;
        initProgram(gl20);
    }

    // attaching parsed shaders
    private void attachShader(GL2 gl20, Shader shader) {
        gl20.glAttachShader(programId, shader.getShaderId());
    }

    // linking the shader program
    private void linkProgram(GL2 gl20) {
        gl20.glLinkProgram(programId);
        IntBuffer intBuffer = GLBuffers.newDirectIntBuffer(1);

        gl20.glGetProgramiv(programId, GL2.GL_LINK_STATUS, intBuffer);
        if (intBuffer.get(0) != 1) {
            gl20.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl20.glGetProgramInfoLog(programId, size, null, byteBuffer);
                FO2IELogger.reportError(new String(byteBuffer.array()), null);
            }
        }
    }

    // validating the shader program
    public void validateProgram(GL2 gl20) {
        gl20.glValidateProgram(programId);
        IntBuffer intBuffer = GLBuffers.newDirectIntBuffer(1);

        gl20.glGetProgramiv(programId, GL2.GL_VALIDATE_STATUS, intBuffer);
        if (intBuffer.get(0) != 1) {
            gl20.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl20.glGetProgramInfoLog(programId, size, null, byteBuffer);
                FO2IELogger.reportError(new String(byteBuffer.array()), null);
            }
        }
    }

    // method of do all together
    private void initProgram(GL2 gl20) {
        // attaching all the shaders        
        attachShader(gl20, vertex);
        attachShader(gl20, fragment);

        // linking program
        linkProgram(gl20);
        // validating program
        validateProgram(gl20);
    }

    public void bind(GL2 gl20) {
        gl20.glUseProgram(programId);
    }

    public static void unbind(GL2 gl20) {
        gl20.glUseProgram(0);
    }

    public void bindAttribute(GL2 gl20, int attribute, String variableName) {
        gl20.glBindAttribLocation(programId, attribute, variableName);
    }

    public void updateUniform(GL2 gl20, int value, String name) {
        int uniformLocation = gl20.glGetUniformLocation(programId, name);
        gl20.glUniform1i(uniformLocation, value);
    }

    public void updateUniform(GL2 gl20, float value, String name) {
        int uniformLocation = gl20.glGetUniformLocation(programId, name);
        gl20.glUniform1f(uniformLocation, value);
    }

    public void updateUniform(GL2 gl20, Vector2f vect, String name) {
        int uniformLocation = gl20.glGetUniformLocation(programId, name);
        gl20.glUniform2f(uniformLocation, vect.x, vect.y);
    }

    public void updateUniform(GL2 gl20, Vector3f vect, String name) {
        int uniformLocation = gl20.glGetUniformLocation(programId, name);
        gl20.glUniform3f(uniformLocation, vect.x, vect.y, vect.z);
    }

    public void updateUniform(GL2 gl20, Vector4f vect, String name) {
        int uniformLocation = gl20.glGetUniformLocation(programId, name);
        gl20.glUniform4f(uniformLocation, vect.x, vect.y, vect.z, vect.w);
    }

    public void updateUniform(GL2 gl20, Matrix4f mat, String name) {
        FloatBuffer fb = GLBuffers.newDirectFloatBuffer(4 * 4);
        mat.get(fb);
        int uniformLocation = gl20.glGetUniformLocation(programId, name);
        gl20.glUniformMatrix4fv(uniformLocation, 1, false, fb);
    }

    public int getProgramId() {
        return programId;
    }

    public Shader getVertex() {
        return vertex;
    }

    public Shader getFragment() {
        return fragment;
    }

}
