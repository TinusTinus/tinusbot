package nl.mvdr.devnobot.model;

import java.util.Collection;
import java.util.Iterator;

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

    /**
     * Determines whether this game object and the given other game object overlap.
     * 
     * @param other
     *            other object
     */
    public boolean overlaps(GameObject other) {
        // horizontal overlap
        boolean result = this.getX() < other.getX() + other.getWidth();
        result = result && other.getX() < this.getX() + this.getWidth();
        // vertical overlap
        result = result && this.getY() < other.getY() + other.getHeight();
        result = result && other.getY() < this.getY() + this.getHeight();
        return result;
    }
    
    /**
     * Checks whether this object overlaps with any of the game objects in the given collection.
     * 
     * @param objects objects
     * @return true if and only if object overlaps with one or more objects in the given collection 
     */
    public boolean overlaps(Collection<? extends GameObject> objects) {
        boolean result = false;
        Iterator<? extends GameObject> iterator = objects.iterator();
        while (!result && iterator.hasNext()) {
            GameObject other = iterator.next();
            result = overlaps(other);
        }
        return result;
    }
}
