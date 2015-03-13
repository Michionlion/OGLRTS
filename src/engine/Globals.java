package engine;

import engine.render.Renderer;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Vector2f;

public class Globals {

    /**
     * path in which the game is being executed
     */
    public static String JARPATH;
    
    
    public static final int WIDTH = 1080;
    public static final int HEIGHT = 720;
    
    public static final int FPS_CAP = 120;

    
    public static Renderer RENDERER;
    
    public static World CURRENT_WORLD;
    
    
    
    
    public static Rectangle viewArea = new Rectangle(0, 0, WIDTH, HEIGHT);
    
    
    
    public static void add(Object o) {
        CURRENT_WORLD.add(o);
    }
    
    public static void remove(Object o) {
        CURRENT_WORLD.remove(o);
    }
    
    /**
     * world must be new, not started yet.
     * @param toChangeTo World to change CURRENT_WORLD to
    **/
    public static void changeWorld(World toChangeTo) {
        CURRENT_WORLD.stop();
        CURRENT_WORLD = toChangeTo;
        CURRENT_WORLD.start();
    }
    
    public static void stop() {
        CURRENT_WORLD.stop();  // no need to set CURRENT_WORLD to null, already done in .stop()
    }
    
    /**
     * Get the time in milliseconds, with between 1 - 1,000,000 nanoseconds of accuracy.
     *
     * @return The system time in milliseconds
     */
    public static double getTime() {
        return System.nanoTime() / 1_000_000d;
    }
    
    private static void startGame() {
        Thread renderThread = new Thread(RENDERER, "renderThread");
        
        
        renderThread.start();
        while(!Display.isCreated()) {
            try {
            Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        CURRENT_WORLD = new World();
        CURRENT_WORLD.start();
    }
    
    /**
     * MUST USE THIS METHOD TO ENSURE CORRECT COORDINATES
     * @return the current mouse positions as a Vector2f
     */
    public static Vector2f getMousePos() {
        System.out.println(Mouse.getX() + ",  " +  -(Mouse.getY() - HEIGHT));
        return new Vector2f(Mouse.getX(), -(Mouse.getY() - HEIGHT));
    }

    public static void main(String[] args) {
        String classpath = Globals.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if(classpath.endsWith(".jar")) {
            JARPATH = classpath;
            System.out.println("adding LWJGL from path: " + new File("natives").getAbsolutePath());
        } else {
            JARPATH = new File(new File(new File(classpath).getParent()).getParent()).getAbsolutePath();
        }
        
        System.setProperty("org.lwjgl.librarypath", JARPATH + File.separator + "res" + File.separator + "natives");
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
        
        RENDERER = new Renderer(true);
        startGame();
    }
}
