package assets.game.objects.units;

import engine.Globals;
import engine.interfaces.TickObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public class MoveOrder implements TickObject {
    
    protected HashSet<Unit> units;
    
    public MoveOrder(Collection<Unit> units) {
        this.units = new HashSet<>(units);
    }
    
    public MoveOrder(Unit... units) {
        this.units = new HashSet<>(Arrays.asList(units));
    }

    @Override
    public void tick() {
        
        if(Mouse.isButtonDown(0)) {
//            System.out.println("processing");
            Vector2f center = Globals.getMousePos();
            float spacing = 8f; //in pixelspace, +5.6 every circle
            double rot = Math.acos(((2*spacing*spacing) - 144)/(2*spacing*spacing)); //in radians, +0.5235 every turn
            
            for(Unit u : units) {
                if(!Unit.units.containsKey(u.UID)) {
                    removeUnit(u);
                    continue;
                }
                
                if(rot > Math.PI*2.1) {
                    spacing += 8f;
                    rot -= Math.PI*2;
                }
                
                u.setTarget(new Vector2f(center.x + (float)Math.cos(rot)*spacing, center.y + (float)Math.sin(rot)*spacing));
                
                rot+= Math.acos(((2*spacing*spacing) - 64)/(2*spacing*spacing));
                System.out.println(Math.acos(((2*spacing*spacing) - 144)/(2*spacing*spacing)));
            }
        }
        
        
    }
    
    
    
    //   add/remove
    
    public void addUnit(Unit u) {
        units.add(u);
    }
    
    public void removeUnit(Unit u) {
        units.remove(u);
    }
    public void addUnit(int UID) {
        units.add(Unit.getUnit(UID));
    }
    
    public void removeUnit(int UID) {
        units.remove(Unit.getUnit(UID));
    }
}
