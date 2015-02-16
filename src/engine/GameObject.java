package engine;

import assets.sprites.MovingSprite;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class GameObject extends MovingSprite {

    public GameObject(Texture tex, Vector2f pos, float rotation, Vector2f delta, Vector2f size) {
        super(tex, pos, rotation, delta, size, 1);
    }
    
    public void turnTowards(float x, float y) {
        double a = Math.atan2(y - pos.y, x - pos.x);
        setRotation((float) Math.toDegrees(a));
    }
    
    public void move(float distance) {
        move(rotation, distance);
    }
    
    public void move(float angle, float distance) {
        
        double radians = Math.toRadians(angle);
        Vector2f dVec = new Vector2f((float)(Math.cos(radians) * distance), (float)(Math.sin(radians) * distance));
        Vector2f.add(pos, dVec, pos);
    }
    
    public void move(Vector2f move) {
        Vector2f.add(pos, move, pos);
    }
    
    
}
