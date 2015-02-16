package engine.interfaces;

import org.newdawn.slick.opengl.Texture;

public interface RenderObject {
    
    public Texture getTex();
    public float getX();
    public float getY();
    public boolean isVisible();
}
