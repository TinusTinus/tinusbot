package nl.mvdr.devnobot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Abstract superclass for objects in the game.
 * 
 * @author Martijn van de Rijdt
 */
@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GameObject {
    /** X coordinate (increases left-to-right). */
    private final int x;
    /** Y coordinate (increases up-down). */
    private final int y;
    /** Width. */
    private final int width;
    /** Height. */
    private final int height;

    /**
     * Constructor.
     * 
     * @param object
     *            original game object as received via the api
     */
    protected GameObject(com.cgi.devnobot.api.GameObject object) {
        super();
        this.x = object.getX();
        this.y = object.getY();
        this.width = object.getWidth();
        this.height = object.getHeight();
    }
}
