package assets.game.objects.units;

import assets.game.objects.TurningSprite;
import engine.Globals;
import java.util.Collection;
import java.util.HashMap;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class Unit extends TurningSprite {
    
    private static int UNIT_ID_COUNTER = 10;
    protected static HashMap<Integer,Unit> units  = new HashMap<>();
    
    
    protected int UID = ++UNIT_ID_COUNTER; // ++ before means that UID will be set to one above the current value of UNIT_ID_COUNTER, and after this statement UNIT_ID_COUNTER will be incremented so the it is equal to UID
    protected Vector2f target;
    protected boolean atTarget = false;
    protected float moveThreshold = 2f;

    public Unit(Texture tex, Vector2f pos, float rotation, Vector2f delta, Vector2f size) {
        super(tex, pos, rotation, delta, size, 1);
    }
    
    public Unit(Texture tex, Vector2f pos, Vector2f size) {
        this(tex, pos, 0, new Vector2f(0,0), size);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if(distanceSqToTarget() < delta.lengthSquared() || distanceSqToTarget() < moveThreshold) target = null;
        
    }
    
    
    
    
    
    protected void setTarget(Vector2f t) {
        target = t;
    }
    
    public int getUID() {
        return UID;
    }
    
    // UTIL METHODS
    
    protected float distanceToTarget() {
        if(target == null) return 0;
        float x = pos.x - target.x;
        float y = pos.y - target.y;
        
        return (float) Math.sqrt(x*x + y*y);
    }
    
    protected float distanceSqToTarget() {
        if(target == null) return 0;
        float x = pos.x - target.x;
        float y = pos.y - target.y;
        
        return x*x + y*y;
        
    }
    
    //  STATIC METHODS
    
    public static Collection<Unit> getAllUnits() {
        return units.values();
    }
    
    public static int getUID(Unit u) {
        return u.getUID();
    }
    
    public static void addUnit(Unit u) {
        units.put(u.UID, u);
        Globals.add(u);
    }
    
    public static Unit getUnit(int UID) {
        return units.get(UID);
    }
    
    public static void destroyUnit(int UID) {
        units.get(UID).destroy();
    }
    
    public static void destroyUnit(Unit u) {
        u.destroy();
    }
    
    public final void destroy() {
        units.remove(UID);
        Globals.remove(this);
    }
}
