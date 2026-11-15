#version 120

uniform sampler2D u_texture;
uniform vec2      u_dir;
uniform int       u_sampleCount;
uniform float     u_offsets[32];
uniform float     u_weights[32];

void main() {
    vec2 texC = gl_TexCoord[0].st;
    vec3 sum = texture2D(u_texture, texC).rgb * u_weights[0];

    for (int i = 1; i < u_sampleCount; ++i) {
        vec2 offset = u_dir * u_offsets[i];
        sum += (texture2D(u_texture, texC + offset).rgb + texture2D(u_texture, texC - offset).rgb) * u_weights[i];
    }

    gl_FragColor = vec4(clamp(sum, 0.0, 1.0), 1.0);
}
