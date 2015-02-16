package engine;

import engine.interfaces.Collidable;
import engine.interfaces.InputListener;
import engine.util.Util;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;

public class Collider implements Collidable {
    
    protected ArrayList<InputListener> hitListeners;
    protected Area collision = new Area();
    private float rotation;
    
    public Collider(float x, float y, float rotation) {
        this(new Area(new Rectangle.Float(x, y, 20, 20)));
        this.rotation = rotation;
    }
    
    public Collider(float x, float y, float width, float height, float rotation) {
        this(new Area(new Rectangle.Float(x, y, width, height)));
        this.rotation = rotation;
    }
    
    public Collider(Area a) {
        collision = a;
        rotation = 0;
    }
    
    public void rotate(float toRot) {
        rotation+=toRot;
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
    public Area getCollision() {
        return collision;
    }
    
    @Override
    public Rectangle getBoxCollision() {
        return collision.getBounds();
    }

    @Override
    public boolean isColliding(Collider c) {
        return Util.isIntersecting(collision, c.collision);
    }
    
    public Area getIntersection(Collider c) {
        return Util.getIntersection(collision, c.collision);
    }

    @Override
    public void addListener(InputListener i) {
        hitListeners.add(i);
    }

    @Override
    public void removeListener(InputListener i) {
        hitListeners.remove(i);
    }
}