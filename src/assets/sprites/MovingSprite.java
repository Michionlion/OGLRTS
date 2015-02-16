package assets.sprites;

import engine.interfaces.Tickable;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class MovingSprite extends Sprite implements Tickable {

    protected Vector2f delta;

    public MovingSprite(Texture tex, Vector2f pos, float rotation, Vector2f delta, Vector2f size, float priority) {
        super(tex, pos, size.x, size.y, rotation, priority);
        this.delta = delta;
    }

    @Override
    public void tick() {
        super.translate(delta.x, delta.y);
    }
    
    private void log(Object s) {
        System.out.println(s);
    }

    public float getDeltaX() {
        return delta.x;
    }

    public float getDeltaY() {
        return delta.y;
    }

    public void setDelta(Vector2f delta) {
        this.delta = delta;
    }

    public void setDeltaX(float dx) {
        delta.x = dx;
    }

    public void setDeltaY(float dy) {
        delta.y = dy;
    }
}
