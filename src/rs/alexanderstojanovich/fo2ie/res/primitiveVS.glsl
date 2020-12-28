#version 110 

attribute vec2 pos;

uniform mat4 modelMatrix;

void main() {						
    gl_Position = modelMatrix * vec4(pos, 0.0, 1.0);                            
}