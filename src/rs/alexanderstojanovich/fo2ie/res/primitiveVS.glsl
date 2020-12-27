#version 110 

attribute vec2 pos;

uniform vec2 trans;
uniform float width;
uniform float height;
uniform float scale;

void main() {					
	vec2 constrPos = scale * vec2(width * pos.x, height * pos.y);
	constrPos += trans;
    gl_Position = vec4(constrPos, 0.0, 1.0);                            
}