#version 110

varying vec2 uvOut;

const vec3 LUMA = vec3(0.2126, 0.7152, 0.0722);

uniform float unit;
uniform vec4 outlineColor;
uniform vec4 color;
uniform sampler2D colorMap;
uniform float GameTime;

vec3 cvet(vec2 texCoords, vec3 inCol) {
	vec3 outCol = inCol;
	float v = (texCoords.y - unit) / (1.0 - unit);
	v += 1.0 - mod(1.75 * GameTime, 2.0);
	if(v > 1.0) {
		v = 2.0 - v;
	} else if(v < 0.0) {
		v = -v;
	}
	outCol += vec3(v) * 0.5 - 0.25;
	
	return outCol;
}

float check(vec2 texCoords, float offset) {
	float chk = (
		texture2D(colorMap, vec2(texCoords.x + offset, texCoords.y)).a +
		texture2D(colorMap, vec2(texCoords.x, texCoords.y - offset)).a +
		texture2D(colorMap, vec2(texCoords.x - offset, texCoords.y)).a +
		texture2D(colorMap, vec2(texCoords.x, texCoords.y + offset)).a + 
		
		texture2D(colorMap, vec2(texCoords.x + offset, texCoords.y + offset)).a +
		texture2D(colorMap, vec2(texCoords.x + offset, texCoords.y - offset)).a +
		texture2D(colorMap, vec2(texCoords.x - offset, texCoords.y + offset)).a +
		texture2D(colorMap, vec2(texCoords.x - offset, texCoords.y - offset)).a
	) / 8.0;
	
	return chk;
}


void main() {    
	vec4 texColor = texture2D(colorMap, uvOut);
	float check = check(uvOut, unit);	
	
	if (texColor.a == 1.0) {
		gl_FragColor.rgb = cvet(uvOut, outlineColor.rgb * vec3(dot(LUMA, color.rgb * texColor.rgb)));
		gl_FragColor.a = outlineColor.a;
	} else if (texColor.a < 1.0 && check > 0.0) {
		gl_FragColor.rgb = cvet(uvOut, outlineColor.rgb);
		gl_FragColor.a = outlineColor.a;
	} else {
		gl_FragColor = color * texColor;	
	}    	
		
}