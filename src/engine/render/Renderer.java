package engine.render;

import assets.Loader;
import assets.game.objects.TestMover;
import assets.game.objects.units.TinyShip;
import assets.game.objects.units.Unit;
import assets.game.objects.units.MoveOrder;
import assets.game.particles.ParticleSystem;
import assets.models.RawModel;
import assets.shaders.BasicSpriteShader;
import assets.shaders.ScreenShader;
import assets.sprites.Sprite;
import engine.Globals;
import engine.interfaces.Interpolatable;
import engine.interfaces.RenderObject;
import engine.interfaces.SpriteShader;
import engine.util.Util;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;



public class Renderer implements Runnable {

    public float interpolation;

    public static RawModel QUAD;

    public int FBO_AA;
    public RawModel boundModel;
    public Texture boundTexture;

    private double now = System.nanoTime();
    private long renders = 0;
    private boolean aaOn;
    protected boolean interpolate = true;

    BasicSpriteShader spriteShader;
    ScreenShader screenShader;

    public Renderer() {
        aaOn = true;
    }

    public Renderer(boolean aa) {
        aaOn = aa;
    }

    private void spriteViewer() {
        String fileName = JOptionPane.showInputDialog("filename of sprite? (omit ending, must be .png)", "debug");
        int sx = Integer.parseInt(JOptionPane.showInputDialog("Width of Sprite? (in pixels of original image)", "64"));
        int sy = Integer.parseInt(JOptionPane.showInputDialog("Height of Sprite? (in pixels of original image)", "64"));
        Globals.add(new TestMover(Loader.getTexture(fileName), vec2(400, 400), vec2(sx, sy)));

        JOptionPane.showMessageDialog(null, "Use WASD to move, and QE to rotate.  (May not work depending on what version is running)");
    }

    @Override
    public void run() {

        initOpenGL();

        for(int i = 0; i <= 129; i++ ) {
            Unit.addUnit(new TinyShip(vec2((float)Math.random()*Globals.WIDTH, (float)Math.random()*Globals.HEIGHT), 90));
        }

        MoveOrder b = new MoveOrder(Unit.getAllUnits());
        Globals.add(b);

        
        
        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        //render loop
        while (!Display.isCloseRequested()) {
            now = Globals.getTime();

            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                break;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
                aaOn = true;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
                aaOn = false;
            }

            if (interpolate) {
                interpolation = Math.min(1.0f, (float) ((now - Globals.CURRENT_WORLD.ticker.lastTickTime) / (TimeUnit.SECONDS.toMillis(1) / Globals.CURRENT_WORLD.ticker.targetTPS)));
            } else {
                interpolation = 1;
            }

            clearBL();

            //RENDER TO renderTexture USING FBO_AA
            {

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, FBO_AA);
                clearCL();

                // --SPRITE RENDER--
                prepareSpriteRender();
                for (RenderObject toRender : Globals.CURRENT_WORLD.renderObjects) {
                    if (toRender.isVisible() && toRender instanceof Sprite) {
                        renderSprite((Sprite) toRender, spriteShader);
                    }

                    if (toRender.isVisible() && toRender instanceof ParticleSystem) {
                        ((ParticleSystem) toRender).render();
                    }
                }
                endSpriteRender();
                // --END SPRITE RENDER--

                GL11.glFinish();
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            }

            //  RENDER renderTexture TO SCREEN IN QUAD
            {
                //start and bind quad
                screenShader.start();
                GL30.glBindVertexArray(QUAD.getVaoID());
                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);
                GL13.glActiveTexture(GL13.GL_TEXTURE0);

                screenShader.setAA(aaOn);
                screenShader.loadTransformationMatrix(Util.createSpriteTransformationMatrix(0, 0, 0, Globals.WIDTH, Globals.HEIGHT, 0));

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.getRenderTextureID());
                GL11.glDrawElements(GL11.GL_TRIANGLES, QUAD.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

                //unbind
                GL20.glDisableVertexAttribArray(0);
                GL20.glDisableVertexAttribArray(1);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                GL30.glBindVertexArray(0);

                screenShader.stop();
            }

            renders++;
            DisplayManager.updateDisplay();
            float renderTime = (float) (Globals.getTime() - now);
            //System.out.println("render " + renders + " done in " + renderTime + "ms.  FPS: " + Math.round(1/(renderTime/1_000f)));
            Display.setTitle("OGLRTS  -  TPS: " + Globals.CURRENT_WORLD.ticker.getTPS() + "  -  RenderObjects: " + Globals.CURRENT_WORLD.renderObjects.size() + "  -  Particles: " + ParticleSystem.totalNumParticles);

        }

        Globals.stop();

        spriteShader.cleanUp();

        Loader.cleanUp();
        GL30.glDeleteFramebuffers(FBO_AA);
        DisplayManager.closeDisplay();
    }

    private void initOpenGL() {
        //init  - can't do it in constructor!
        try {
            DisplayManager.createDisplay();
            Display.makeCurrent();
        } catch (LWJGLException ex) {
            System.err.println("Unable to change GLCONTEXT threads using Display.makeCurrent()!");
            Sys.alert("ERROR 2", "Unable to change GLCONTEXT threads using Display.makeCurrent()!");
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Keyboard.create();
            Mouse.create();
        } catch (LWJGLException ex) {
            System.err.println("Unable to create Input Devices!");
            Sys.alert("ERROR 3", "Unable to create Input Devices!");
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
            System.out.println("OpenGL render to FBO not supported on this platform, exiting!");
            System.exit(0);
        }

        //set OpenGL constants
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        //set up QUAD model
        float[] verts
                = {0.5f, 0.5f, 0f, //v1
                    0.5f, -0.5f, 0f, //v2
                    -0.5f, -0.5f, 0f, //v3
                    -0.5f, 0.5f, 0f};  //v4
        float[] texs = {0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f};
        int[] indices = {0, 1, 3, 3, 1, 2};

        QUAD = Loader.loadToVAO(verts, texs, indices);

        //create FBO_AA
        FBO_AA = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, FBO_AA);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, Loader.getRenderTextureID(), 0);

//        int error = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
//        if(error != GL30.GL_FRAMEBUFFER_COMPLETE) {
//            System.err.println("ERROR IN FRAME BUFFER, ERROR NUM = " + error);
//        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        spriteShader = new BasicSpriteShader();
        screenShader = new ScreenShader(aaOn);
    }

    public void setAA(boolean aaOn) {
        this.aaOn = aaOn;

    }

    public void clearBL() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    public void clearCL() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    public void prepareSpriteRender() {
        GL30.glBindVertexArray(QUAD.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    public void endSpriteRender() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
    }

    public void renderSprite(Sprite toRender, SpriteShader shader) {
        shader.start();
        Matrix4f tMatrix;
        if (toRender instanceof Interpolatable && ((Interpolatable) toRender).interpolate()) {
            //System.out.println("Interpolating " + toRender);
            Interpolatable i = (Interpolatable) toRender;
            tMatrix = Util.createSpriteTransformationMatrix(toRender.getX() + (i.getDeltaX() * interpolation), toRender.getY() + (i.getDeltaY() * interpolation), toRender.getRotation(), toRender.getWidth(), toRender.getHeight(), toRender.getPriority());
        } else {
            tMatrix = Util.createSpriteTransformationMatrix(toRender.getX(), toRender.getY(), toRender.getRotation(), toRender.getWidth(), toRender.getHeight(), toRender.getPriority());
        }

        shader.loadTransformationMatrix(tMatrix);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, toRender.getTex().getTextureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, QUAD.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        shader.stop();
    }

    private Vector2f vec2(float x, float y) {
        return new Vector2f(x, y);
    }

    public Area createAreaFromImage(String fileName, byte cutoff) {

        Texture tex = Loader.getTexture(fileName);

        byte[] data = tex.getTextureData();

        GeneralPath gp = new GeneralPath();

        int i = tex.getTextureHeight();
        boolean cont = false;
        for (int x = 0; x < i; x++) {
            for (int y = 0; y < i; y++) {
                byte alpha = data[4 * (y * i + x) + 3];

                if (alpha == -1 || alpha >= cutoff) {
                    if (cont) {
                        gp.lineTo(x, y);
                        gp.lineTo(x, y + 1);
                        gp.lineTo(x + 1, y + 1);
                        gp.lineTo(x + 1, y);
                        gp.lineTo(x, y);
                    } else {
                        gp.moveTo(x, y);
                    }
                    cont = true;
                } else {
                    cont = false;
                }
            }
            cont = false;
//            System.out.println(100*((float)x/(float)tex.getTextureWidth()) + "%");
        }

        return new Area(gp);
    }

    public Area createDetailedAreaFromImage(String fileName, byte cutoff) {

        Texture tex = Loader.getTexture(fileName);

        byte[] data = tex.getTextureData();

        Area area = new Area();
        Rectangle rectangle = new Rectangle();
        for (int x = 0; x < tex.getTextureWidth(); x++) {
            for (int y = 0; y < tex.getTextureWidth(); y++) {
                byte alpha = data[4 * (y * tex.getTextureWidth() + x) + 3];
//                System.out.println("alpha at " + x + ", " + y + " is " + alpha);
                if (alpha == -1 || alpha >= cutoff) {
                    rectangle.setBounds(x, y, 1, 1);
                    area.add(new Area(rectangle));
                }
            }
        }
        return area;
    }
}
