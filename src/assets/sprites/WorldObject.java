/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assets.sprites;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class WorldObject extends MovingSprite {

    public WorldObject(Texture tex, Vector2f pos, float rotation, Vector2f delta, Vector2f size, float priority) {
        super(tex, pos, rotation, delta, size, priority);
    }
    
    public void setLocation(float x, float y) {
        setPos(new Vector2f(x,y));
    }
}
