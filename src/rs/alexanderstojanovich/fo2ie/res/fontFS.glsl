#version 110

varying vec2 uvOut;

uniform vec4 color;
uniform sampler2D colorMap;

void main() {    
	vec4 texColor = texture2D(colorMap, uvOut);
    gl_FragColor = color * texColor;
}