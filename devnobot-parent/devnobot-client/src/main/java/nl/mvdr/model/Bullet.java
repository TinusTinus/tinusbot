package nl.mvdr.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.cgi.devnobot.api.GameBullet;

/**
 * Representation of a bullet. Bullets are spawned by firing, disappear when they hit a wall or a tank.
 * 
 * @author Martijn van de Rijdt
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Bullet extends GameObject {
    /**
     * Constructor.
     * 
     * @param bullet
     *            game object as received from the client api
     */
    public Bullet(GameBullet bullet) {
        super(bullet);
    }
    
    /**
     * Constructor. Intended for use in unit tests.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     */
    Bullet(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
