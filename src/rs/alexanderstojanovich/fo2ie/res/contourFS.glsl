#version 110

varying vec2 uvOut;

const vec3 LUMA = vec3(0.2126, 0.7152, 0.0722);

uniform float unit;
uniform vec4 outlineColor;
uniform vec4 color;
uniform sampler2D colorMap;

float check(vec2 v_texCoords, float offset) {
	float chk = (
		texture2D(colorMap, vec2(v_texCoords.x + offset, v_texCoords.y)).a +
		texture2D(colorMap, vec2(v_texCoords.x, v_texCoords.y - offset)).a +
		texture2D(colorMap, vec2(v_texCoords.x - offset, v_texCoords.y)).a +
		texture2D(colorMap, vec2(v_texCoords.x, v_texCoords.y + offset)).a + 
		
		texture2D(colorMap, vec2(v_texCoords.x + offset, v_texCoords.y + offset)).a +
		texture2D(colorMap, vec2(v_texCoords.x + offset, v_texCoords.y - offset)).a +
		texture2D(colorMap, vec2(v_texCoords.x - offset, v_texCoords.y + offset)).a +
		texture2D(colorMap, vec2(v_texCoords.x - offset, v_texCoords.y - offset)).a
	) / 8.0;
	return chk;
}


void main() {    
	vec4 texColor = texture2D(colorMap, uvOut);
	float check = check(uvOut, unit);
	
	if (texColor.a == 1.0) {
		gl_FragColor = outlineColor * vec4(vec3(dot(LUMA, color.rgb * texColor.rgb)), color.a);
	} else if (texColor.a < 1.0 && check > 0.0) {
		gl_FragColor = outlineColor;
	} else {
		gl_FragColor = color * texColor;	
	}    
}