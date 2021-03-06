package assets.sprites;

import engine.Globals;
import engine.interfaces.RenderObject;
import engine.util.Util;
import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class Sprite implements RenderObject {
    
    protected Texture tex;
    protected Vector2f pos;
    public float rotation,priority;
    protected float width,height;

    public Sprite(Texture tex, float x, float y, float rotation, float priority) {
        this(tex, new Vector2f(x,y), 50f, 50f, rotation);
        this.priority = priority;
    }
    
    public Sprite(Texture tex, Vector2f position, float width, float height, float rotation) {
        this(tex, position, width, height, rotation, 1);
    }
    public Sprite(Texture tex, Vector2f position, float width, float height, float rotation, float priority) {
        this.tex = tex;
        pos = position;
        setRotation(rotation);
        this.priority = priority;
        
        this.width = width;
        this.height = height;
    }
    
    
    public void rotate(float toRot) {
        rotation+=toRot;
        checkRotation();
    }
    
    public void turnToward(float x, float y) {
        setRotation(angleToward(x,y));
    }
    
    public void turnToward(Vector2f xy) {
        turnToward(xy.x, xy.y);
    }
    
    public float angleToward(Vector2f xy) {
        return angleToward(xy.x, xy.y);
    }
    
    public float angleToward(float x, float y) {
        double a = Math.atan2((y - pos.y), -(x - pos.x));
        return (float) Math.toDegrees(a);
    }
    
    public void move(float distance) {
        move(distance, rotation);
    }
    
    public void move(float distance, float angle) {
        translate((float)Math.cos(Math.toRadians(angle))*-distance, (float)Math.sin(Math.toRadians(angle))*distance);
    }
    
    public void translate(float x, float y) {
        pos.translate(x, y);
    }
    
    public void translate(Vector2f t) {
        translate(t.getX(), t.getY());
    }

    @Override
    public Texture getTex() {
        return tex;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2f getPos() {
        return pos;
    }

    public final void setPos(Vector2f pos) {
        this.pos = pos;
    }

    public float getRotation() {
        return rotation;
    }

    public final void setRotation(float rotation) {
        this.rotation = rotation;
        checkRotation();
    }
    
    public void checkRotation() {
        if(rotation >=360) {
            rotation-=360;
            checkRotation();
        }
        else if (rotation < 0) {
            rotation+=360;
            checkRotation();
        }
        
    }

    @Override
    public float getX() {
        return pos.x;
    }

    @Override
    public float getY() {
        return pos.y;
    }

    public float getPriority() {
        return priority;
    }

    public final void setPriority(float priority) {
        this.priority = priority;
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
