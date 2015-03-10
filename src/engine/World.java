package engine;

import engine.interfaces.RenderObject;
import engine.interfaces.TickObject;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * World in which Objects (not just WorldObjects) can exist
 * @author Saejin
 */
public class World implements TickObject {
    private static int widc = 100;
    public static final HashMap<Integer, World> WORLDS = new HashMap<>();
    
    public CopyOnWriteArrayList<GameObject> gameObjects;
    public CopyOnWriteArrayList<RenderObject> renderObjects;
    public Ticker ticker;
    
    protected Thread tickThread;
    
    public final int worldID = ++widc;
    
    
    public World() {
        gameObjects = new CopyOnWriteArrayList<>();
        renderObjects = new CopyOnWriteArrayList<>();
        ticker = new Ticker(30);
        WORLDS.put(worldID, this);
    }
    
    
    
    
    
    @Override
    public void tick() {
        
    }
    
    public void start() {
        tickThread = new Thread(ticker, "tickThread-world" + worldID);
        tickThread.start();
    }
    
    public void stop() {
        ticker.end();
        ticker = null;
        WORLDS.remove(worldID);
        // careful with this part, not sure if we need to set it null yet
        Globals.CURRENT_WORLD = null;
    }
    
    public void setPause(boolean paused) {
        ticker.setPaused(paused);
    }
    
    public boolean isPaused() {
        return ticker.isPaused();
    }
    
    public void add(Object o) {
        if(o instanceof TickObject) ticker.add((TickObject) o);
        if(o instanceof GameObject) gameObjects.add((GameObject) o);
        if(o instanceof RenderObject) renderObjects.add((RenderObject) o);
    }
    
    public void remove(Object o) {
        if(o instanceof TickObject) if(ticker.isTicking((TickObject) o)) ticker.remove((TickObject) o);
        if(o instanceof GameObject) if(gameObjects.contains((GameObject)o)) gameObjects.remove((GameObject) o);
        if(o instanceof RenderObject) if(renderObjects.contains((RenderObject)o)) renderObjects.remove((RenderObject) o);
    }
}
