#version 110 

attribute vec2 pos;
attribute vec2 uv;

varying vec2 uvOut;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;

void main() {						
    gl_Position = projectionMatrix * modelMatrix * vec4(pos, 0.0, 1.0);                        
    uvOut = uv;    
}