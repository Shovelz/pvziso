attribute vec4 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;  // Required by LibGDX's SpriteBatch
varying vec2 v_texCoord;

void main() {
    v_texCoord = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}
