package assets.game.objects;

import assets.sprites.MovingSprite;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class TurningSprite extends MovingSprite {
    
    protected float targetRotation;
    protected float turnRate = 7.4f;
    

    public TurningSprite(Texture tex, Vector2f pos, float rotation, Vector2f delta, Vector2f size, float priority) {
        super(tex, pos, rotation, delta, size, priority);
    }
    
    protected void doRotation() {
        
        float b = targetRotation;
        float a = rotation;
        
        if(Math.abs(targetRotation - rotation) <= turnRate) {
            setRotation(targetRotation);
            return;
        }
        
        if (b < 0.0d) {
            b += 360;
        }

        boolean clockwise = true;
        if (a < b && b - a > 180) {
            clockwise = false;
        }
        if (a > b && a - b <= 180) {
            clockwise = false;
        }

        if (clockwise) {
            rotate(turnRate);
        } else {
            rotate(-turnRate);
        }
    }
}
