#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec4 texProj0;

out vec4 fragColor;
uniform float GameTime;

const mat4 SCALE_TRANSLATE = mat4(
1.0, 0.0, 0.0, 0.5,
0.0, 1.0, 0.0, 0.5,
0.0, 0.0, 1.0, 0.0,
0.0, 0.0, 0.0, 1.0
);

mat4 layer(int layer) {
    mat4 translate = mat4(
    1.0 - 0.1*layer, 0.0, 0.0, 64,
    0.0, 1.0 - 0.1*layer, 0.0, 4 * GameTime,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, layer == 0 ? 1.0 : 0.4 + layer * 0.2
    );

    mat2 rotate = mat2_rotate_z(radians(GameTime * (2000 - 1500 * layer)));

    return mat4(rotate) * translate * SCALE_TRANSLATE;
}

vec4 blend(vec4 background,vec4 cover,float blendFunc) {
    vec3 result = background.rgb * ( 1 - blendFunc) + cover.rgb * blendFunc;
    float alpha = background.a + (1 - background.a) * cover.a * blendFunc;
    return vec4(result,alpha);
}

void main() {
    vec4 color = textureProj(Sampler0,texProj0 * layer(0)).rgba;
    vec4 coverColor = textureProj(Sampler1,texProj0 * layer(1)).rgba * 0.4;
    coverColor += textureProj(Sampler1,texProj0 * layer(2)).rgba * 0.6;
    coverColor += textureProj(Sampler1,texProj0 * layer(3)).rgba * 0.8;
    coverColor += textureProj(Sampler1,texProj0 * layer(4)).rgba;
//    fragColor = vec4(color * coverColor.rgb * coverColor.a,1.0F);
    fragColor = blend(color,coverColor,coverColor.a);
}
