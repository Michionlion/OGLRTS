package assets.game.objects.units;

import assets.ResourceManager;
import assets.game.particles.ParticleSystem;
import assets.shaders.BasicSpriteShader;
import engine.Globals;
import org.lwjgl.util.vector.Vector2f;

public class TinyShip extends Unit {
    

    public ParticleSystem engine;
    
    
    public TinyShip(Vector2f pos, float rotation) {
        super(ResourceManager.getTexture("tinyShip"), pos, rotation, new Vector2f(0,0), new Vector2f(8,8));
        engine = new ParticleSystem(new BasicSpriteShader(), ResourceManager.getTexture("particles/sphereb"), pos, new Vector2f(0,0), rotation, 4 * ((float)Math.PI/108f), 1, 2, 0.07f, 0.1f);
        Globals.add(engine);
    }

    
    @Override
    public void tick() {
        super.tick();
        engine.setPos(pos);
        engine.setRotation((float) Math.toRadians(-rotation));
        
        if(target != null) targetRotation = angleToward(target);
        doRotation();
        
        if(target != null) {
            engine.setEnabled(true);
            if(distanceSqToTarget() > 190) move(3.8f);
            else if(distanceSqToTarget() > 150) move(2.4f);
            else if(distanceSqToTarget() > 100) move(1.3f);
            else move(0.7f);
        } else {
            engine.setEnabled(false);
        }
        
        
        
    }
    
    @Override
    public boolean interpolate() {
        return false;
    }
    
}
