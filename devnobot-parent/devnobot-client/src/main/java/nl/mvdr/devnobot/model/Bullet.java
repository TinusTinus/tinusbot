package nl.mvdr.devnobot.model;

import lombok.Getter;
import lombok.ToString;

import com.cgi.devnobot.api.GameBullet;

/**
 * Representation of a bullet. Bullets are spawned by firing, disappear when they hit a wall or a tank.
 * 
 * @author Martijn van de Rijdt
 */
@ToString(callSuper = true)
public class Bullet extends GameObject {
    /**
     * Width of a bullet.
     * 
     * Seems to be 4 in all of my tests so that is the initial value. Updated whenever a new bullet is instantiated.
     */
    @Getter
    private static int bulletWidth = 4;
    /**
     * Width of a bullet.
     * 
     * Seems to be 4 in all of my tests so that is the initial value. Updated whenever a new bullet is instantiated.
     */
    @Getter
    private static int bulletHeight = 4;
    
    /** Updates the static bullet size fields based on this bullet's size. */
    private void updateBulletSize() {
        bulletWidth = getWidth();
        bulletHeight = getHeight();
    }
    
    /**
     * Constructor.
     * 
     * @param bullet
     *            game object as received from the client api
     */
    public Bullet(GameBullet bullet) {
        super(bullet);
        updateBulletSize();
    }
    
    /**
     * Constructor.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     */
    Bullet(int x, int y, int width, int height) {
        super(x, y, width, height);
        updateBulletSize();
    }
}
