package assets.models;

import org.newdawn.slick.opengl.Texture;

public class TexturedModel {
    
    private RawModel rawModel;
    private Texture texture;
    
    public TexturedModel(RawModel model, Texture tex) {
        this.rawModel = model;
        this.texture = tex;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public Texture getTexture() {
        return texture;
    }
    
    
    
}
