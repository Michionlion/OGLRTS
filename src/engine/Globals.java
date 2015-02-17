package engine;

import engine.interfaces.RenderObject;
import engine.interfaces.TickObject;
import engine.render.Renderer;
import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.Rectangle;

public class Globals {

    public static String JARPATH;
    
    
    public static final int WIDTH = 1080;
    public static final int HEIGHT = 720;
    
    public static final int FPS_CAP = 120;

    public static Ticker TICKER;
    public static Renderer RENDERER;
    
    
    public static CopyOnWriteArrayList<GameObject> gameObjects;
    public static CopyOnWriteArrayList<RenderObject> renderObjects;
    
    public static Rectangle viewArea = new Rectangle(0, 0, WIDTH, HEIGHT);
    
    
    
    public static void add(Object o) {
        if(o instanceof TickObject) TICKER.add((TickObject) o);
        if(o instanceof GameObject) gameObjects.add((GameObject) o);
        if(o instanceof RenderObject) renderObjects.add((RenderObject) o);
    }
    
    public static void remove(Object o) {
        if(o instanceof TickObject) if(TICKER.isTicking((TickObject) o)) TICKER.remove((TickObject) o);
        if(o instanceof GameObject) if(gameObjects.contains((GameObject)o)) gameObjects.remove((GameObject) o);
        if(o instanceof RenderObject) if(renderObjects.contains((RenderObject)o)) renderObjects.remove((RenderObject) o);
    }
    
    /**
     * Get the time in milliseconds
     *
     * @return The system time in milliseconds
     */
    public static double getTime() {
        return System.nanoTime() / 1_000_000d;
    }
    
    private static void startGame() {
        Thread renderThread = new Thread(RENDERER, "renderThread");
        Thread tickThread = new Thread(TICKER, "tickThread");
        
        gameObjects = new CopyOnWriteArrayList<>();
        renderObjects = new CopyOnWriteArrayList<>();
        
        
        renderThread.start();
        while(!Display.isCreated()) {
            try {
            Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        tickThread.start();
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
        TICKER = new Ticker(30);
        startGame();
    }
}
