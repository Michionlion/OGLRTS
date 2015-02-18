package assets.game.objects;

import assets.Loader;
import assets.game.particles.ParticleSystem;
import assets.shaders.BasicSpriteShader;
import engine.Globals;
import engine.util.Util;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public class TinyShip extends TurningSprite {
    
    private static HashSet<TinyShip> ships  = new HashSet<>();

    public ParticleSystem engine;
    
    private Vector2f target;
    
    public TinyShip(Vector2f pos, float rotation) {
        super(Loader.getTexture("tinyShip"), pos, rotation, new Vector2f(0,0), new Vector2f(8,8), 1);
        target = new Vector2f(Mouse.getX(), Mouse.getY());
        engine = new ParticleSystem(new BasicSpriteShader(), Loader.getTexture("particles/sphereb"), pos, new Vector2f(0,0), rotation, 4 * ((float)Math.PI/108f), 1, 2, 0.07f, 0.1f);
        Globals.add(engine);
        ships.add(this);
    }

    
    @Override
    public void tick() {
        System.out.println("ticked");
        target = Globals.getMousePos();
        engine.setPos(pos);
        engine.setRotation((float) Math.toRadians(-rotation));
        
//        if(distanceSqToTarget() < 2500) {
//            turnToward(target);
//            rotation+=90;
//        } else {
            targetRotation = angleToward(target);
            doRotation();
//        }
        move(1.9f);
        
        
        for(TinyShip s : ships) {
            if(this != s && Util.distanceSq(pos, s.pos) <= 20) {
                float angle = Util.angleToward(pos, s.pos);
                move(Util.distance(pos, s.pos)/3f, angle - 180);
                s.move(Util.distance(pos, s.pos)/3f, angle);
            }
        }
        
//        if(Keyboard.isKeyDown(Keyboard.KEY_Q)) rotate(2.3f);
//        if(Keyboard.isKeyDown(Keyboard.KEY_E)) rotate(-2.3f);
//        if(Keyboard.isKeyDown(Keyboard.KEY_W)) move(2.6f);
//        if(Keyboard.isKeyDown(Keyboard.KEY_S)) move(-2.6f);
        
        
        
    }
    
    protected float distanceToTarget() {
        
        float x = pos.x - target.x;
        float y = pos.y - target.y;
        
        return (float) Math.sqrt(x*x + y*y);
    }
    protected float distanceSqToTarget() {
        
        float x = pos.x - target.x;
        float y = pos.y - target.y;
        
        return x*x + y*y;
        
    }
    
    public void destroy() {
        ships.remove(this);
        Globals.remove(this);
        Globals.remove(engine);
    }
    
    @Override
    public boolean interpolate() {
        return false;
    }
    
}
