package assets.game.objects;

import assets.Loader;
import assets.sprites.MovingSprite;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class TestMover extends MovingSprite {

    public TestMover(Texture tex, Vector2f pos, Vector2f size) {
        super(tex, pos, 0, new Vector2f(0,0), size, 1);
    }

    @Override
    public void tick() {
        
        if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            rotate(1);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
            rotate(-1);
        }
        
        
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            translate(0, -2f);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
            translate(0, 2f);
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            translate(2f, 0);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            translate(-2f, 0);
        }
        
        
    }
    
    @Override
    public boolean interpolate() {
        return Keyboard.isKeyDown(Keyboard.KEY_SPACE);
    }
    
}
