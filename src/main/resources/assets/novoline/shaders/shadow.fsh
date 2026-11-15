#version 120

uniform sampler2D u_texture;
uniform vec2 u_dir;
uniform int u_sampleCount;
uniform float u_offsets[32];
uniform float u_weights[32];
uniform float u_gamma;
uniform vec3 u_color;

void main() {
    vec2 uv = gl_TexCoord[0].st;
    float alpha = texture2D(u_texture, uv).a * u_weights[0];

    for (int i = 1; i < u_sampleCount; ++i) {
        vec2 offset = u_dir * u_offsets[i];
        alpha += (texture2D(u_texture, uv + offset).a + texture2D(u_texture, uv - offset).a) * u_weights[i];
    }

    float invGamma = 1.0 / max(0.001, u_gamma);
    gl_FragColor = vec4(pow(u_color, vec3(invGamma)), pow(clamp(alpha, 0.0, 1.0), invGamma));
}
