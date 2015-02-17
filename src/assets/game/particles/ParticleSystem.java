package assets.game.particles;

import assets.Loader;
import assets.shaders.BasicSpriteShader;
import engine.Globals;
import engine.interfaces.RenderObject;
import engine.interfaces.TickObject;
import static engine.render.Renderer.QUAD;
import engine.util.Util;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class ParticleSystem implements RenderObject, TickObject {

    BasicSpriteShader shader;

    Texture particleTex;

    Random random = new Random();
    
    int lifetime;
    
    private Vector2f pos;
    private Vector2f particleDelta = new Vector2f(0, 0);
    private float startingSize = 4;
    private float speed = 1.7f;
    private float varience = 0.1f;

    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();

    public ParticleSystem(Vector2f pos, BasicSpriteShader shader) {
        this.pos = pos;
        this.shader = shader;
        particleTex = Loader.getTexture("particles/sphereb");
    }

    public ParticleSystem(Vector2f pos) {
        this.pos = pos;
        shader = new BasicSpriteShader();
        particleTex = Loader.getTexture("particles/sphereb");
    }
    
    public void createParticle() {
        double pRot =  random.nextDouble()*(2f*Math.PI);
        float pSpeed = speed + ((random.nextFloat()*(speed*varience*2)) - (speed*varience));
        
        float dX  = (float) (Math.cos(pRot)*pSpeed);
        float dY  = (float) (Math.sin(pRot)*pSpeed);
        particles.add(new Particle(dX + particleDelta.getX(), dY + particleDelta.getY()));
    }

    public void render() {
        
        for(int b = 0; b < 30; b++) {
            createParticle();
        }
        
        
        
        
        shader.start();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, particleTex.getTextureID());

        Matrix4f tMatrix;
        
        for (int i = particles.size() - 1; i > 0; i--) {
            Particle p = particles.get(i);
            tMatrix = Util.createSpriteTransformationMatrix(p.x, p.y, 0, p.size, p.size, 1);

            shader.loadTransformationMatrix(tMatrix);

            GL11.glDrawElements(GL11.GL_TRIANGLES, QUAD.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            
            p.tick();
        }
        
        shader.stop();
    }
    
    public int getNumParticles() {
        return particles.size();
    }

    @Override
    public Texture getTex() {
        return particleTex;
    }

    @Override
    public float getX() {
        return pos.getX();
    }

    @Override
    public float getY() {
        return pos.getY();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void tick() {
        //lifetime++;
        
        //if(lifetime%30 == 0) {
            //System.gc();
        //}
    }

    class Particle {

        float x, y, dX, dY;
        float size = startingSize;
        int ticks = 1;

        public Particle(float dX, float dY) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.dX = dX;
            this.dY = dY;
        }
        protected void tick() {
            x += dX;
            y += dY;
            dY+=0.008f;
            //size *=(ticks+99f)/(ticks+100f);
            if(size < 2) {
                size *=(ticks+49f)/(ticks+50f);
            }
            if(size <= 0.4f || bounds()) {
               particles.remove(this);
            }
        }
        
        protected boolean bounds() {
            return x > Globals.WIDTH + size || x < -size || y > Globals.HEIGHT + size || y < -size;
        }

    }

}
