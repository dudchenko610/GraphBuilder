precision mediump float;

uniform vec4 u_Color;
uniform bool u_HasColor;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;

void main() {

    if (u_HasColor) {
        gl_FragColor = u_Color;
    } else {
        gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
    }



}
