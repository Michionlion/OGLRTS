#version 400 core

in vec2 pass_textureCoords;

out vec4 outColor;


uniform sampler2D textureSampler;
uniform bool antiAliasingOn;

vec4 color = texture(textureSampler, pass_textureCoords);
vec2 size = 1f/textureSize(textureSampler, 0);

bool isEdge();
vec4 blur();

void main() {

    if(!antiAliasingOn) {
        outColor = color;
        return;
    }


    if(isEdge()) outColor = blur();
    else outColor = color;
    
}

bool isEdge() {
    //how many pixels away samples should reach
    int samples = 1;
    
    float threshold = 0.10f; //clamp(color.a + 0.01f, 0.01f, 1f);
    bool anyClear = false;
    bool anyNotClear = false;

    if(color.a >= threshold) {

        int i,j;
        for(i=-samples; i<=samples; i++) {
            for(j=-samples; j<=samples; j++) {
                vec2 coord = pass_textureCoords + (vec2(i, j)*size);
                float a = texture(textureSampler, coord).a;
                //isClear
                if(a < threshold) {
                    anyClear = true;
                    return true;
                } else {
                    anyNotClear = true;
                    
                }

            }
        }

        return false;
    }

    int i,j;
    for(i=-samples; i<=samples; i++) {
        for(j=-samples; j<=samples; j++) {
            vec2 coord = pass_textureCoords + (vec2(i, j)*size);
            float a = texture(textureSampler, coord).a;
            //isClear
            if(a < threshold) {
                anyClear = true;
                
            } else {
                anyNotClear = true;
                return true;
            }

        }
    }

    return false;
}

vec4 blur() {
    
    int i,j;
    int samples;
    int dis = 2;
    vec4 sum = vec4(0);
    vec4 toAdd;
    for(i=-dis; i<=dis; i++) {
        for(j=-dis; j<=dis; j++) {
            vec2 offset = vec2(i, j)*size;
            toAdd = texture(textureSampler, pass_textureCoords + offset);
            sum += toAdd;
            samples++;
        }
    }

    return (sum/samples) + color/4.75f;
}