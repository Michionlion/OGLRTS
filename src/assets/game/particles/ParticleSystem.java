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
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class ParticleSystem implements RenderObject, TickObject {

    public static int totalNumParticles;
    
    /**
     * draw but not create particles when enabled = false?
     */
    private boolean drawWhenDisabled = true;
    /**
     * emit particles and render them?
     */
    private boolean enabled = true;
    
    /**
     * shader used to draw particles, must extend BasicSpriteShader
     */
    BasicSpriteShader shader;
    /**
     * texture of the particles
     */
    Texture particleTex;
    /**
     * the position of the emitter?
     */
    private Vector2f pos;
    
    /**
     * what is the initial delta of the particles?
     */
    private Vector2f particleDelta = new Vector2f(0,0);
    
    /**
     * rotation of the cone and/or circle?
     */
    /**
     * rotation of the cone and/or circle?
     */
    float rotation = 2;
    
    /**
     * radius of the exhaust cone in radians? (2*PI = full circle)
     */
    float coneRad = (float) (3 * (Math.PI/180f));
    
    
    /**
     * how many particles are added per render cycle?
     */
    int fillRate = 1;
    
    /**
     * what size do the particles start at?
     */
    float startingSize = 2;
    /**
     * what is the initial speed of the particles?
     */
    float speed = 1.7f;
    /**
     * by how much are the above values (if applicable) allowed to deviate? (0-1)
     */
    float varience = 0.1f;
    
    Random random = new Random();
    
    int lifetime;
    
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();

    public ParticleSystem(BasicSpriteShader shader, Texture particleTex, Vector2f pos, Vector2f particleDelta, float rotation, float coneRad, int fillRate, float startingSize, float speed, float varience) {
        this.shader = shader;
        this.particleTex = particleTex;
        this.pos = pos;
        this.particleDelta = particleDelta;
        this.rotation = rotation;
        this.coneRad = coneRad;
        this.fillRate = fillRate;
        this.startingSize = startingSize;
        this.speed = speed;
        this.varience = varience;
    }

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
    public ParticleSystem(Vector2f pos, BasicSpriteShader shader, Texture tex) {
        this.pos = pos;
        this.shader = shader;
        particleTex = tex;
    }

    public ParticleSystem(Vector2f pos, Texture tex) {
        this.pos = pos;
        shader = new BasicSpriteShader();
        particleTex = tex;
    }
    
    public void createParticle() {
        double pRot =  rotation - (coneRad/2f) + random.nextDouble()*coneRad;
        float pSpeed = speed + ((random.nextFloat()*(speed*varience*2)) - (speed*varience));
        
        float dX  = (float) (Math.cos(pRot)*pSpeed);
        float dY  = (float) (Math.sin(pRot)*pSpeed);
        particles.add(new Particle(dX + particleDelta.getX(), dY + particleDelta.getY()));
    }

    public void render() {
        if(!enabled && !drawWhenDisabled) return;
        
        if(enabled) for(int b = 0; b < 1; b++) {
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
        lifetime++;
        
        
    }
    
    // GETTERS AND SETTERS
    public boolean isDrawWhenDisabled() {
        return drawWhenDisabled;
    }

    public void setDrawWhenDisabled(boolean drawWhenDisabled) {
        this.drawWhenDisabled = drawWhenDisabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public BasicSpriteShader getShader() {
        return shader;
    }

    public void setShader(BasicSpriteShader shader) {
        this.shader = shader;
    }

    public Texture getParticleTex() {
        return particleTex;
    }

    public void setParticleTex(Texture particleTex) {
        this.particleTex = particleTex;
    }
    
    public void setPos(float x, float y) {
        pos.set(x, y);
    }

    public Vector2f getPos() {
        return pos;
    }

    public void setPos(Vector2f pos) {
        this.pos = pos;
    }

    public Vector2f getParticleDelta() {
        return particleDelta;
    }

    public void setParticleDelta(Vector2f particleDelta) {
        this.particleDelta = particleDelta;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getConeRad() {
        return coneRad;
    }

    public void setConeRad(float coneRad) {
        this.coneRad = coneRad;
    }

    public int getFillRate() {
        return fillRate;
    }

    public void setFillRate(int fillRate) {
        this.fillRate = fillRate;
    }

    public float getStartingSize() {
        return startingSize;
    }

    public void setStartingSize(float startingSize) {
        this.startingSize = startingSize;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getVarience() {
        return varience;
    }

    public void setVarience(float varience) {
        this.varience = varience;
    }
    
    // PARTICLE CLASS
    
    class Particle {

        float x, y, dX, dY;
        float size = startingSize;
        int ticks = 1;

        public Particle(float dX, float dY) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.dX = dX;
            this.dY = dY;
            totalNumParticles++;
        }
        protected void tick() {
            x += dX;
            y += dY;
            size *=(ticks+19f)/(ticks+20f);
            if(size < 1.3f) {
                size *=(ticks+9f)/(ticks+10f);
            }
            if(size <= 0.25f || bounds()) {
               destroy();
            }
            ticks++;
        }
        
        protected void destroy() {
            particles.remove(this);
            totalNumParticles--;
        }
        
        protected boolean bounds() {
            return x > Globals.WIDTH + size || x < -size || y > Globals.HEIGHT + size || y < -size;
        }

    }

}
