#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord);
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114)); // Luminance formula

    float darknessFactor = 0.3; // Adjust this value (0.0 = black, 1.0 = original)
    gray *= darknessFactor;
    gl_FragColor = vec4(vec3(gray), color.a);
}
