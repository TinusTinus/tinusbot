package nl.mvdr.devnobot.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.cgi.devnobot.api.GameObstacle;

/**
 * Representation of a wall / obstacle.
 * 
 * @author Martijn van de Rijdt
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Wall extends GameObject {
    /**
     * Constructor.
     * 
     * @param obstacle
     *            game object as received from the client api
     */
    public Wall(GameObstacle obstacle) {
        super(obstacle);
    }
    
    /**
     * Constructor. Intended for use in unit tests.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @param width width
     * @param height height
     */
    Wall(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
