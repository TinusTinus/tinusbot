package nl.mvdr.devnobot.model;

/**
 * Dummy subclass of {@link GameObject} for use in {@link GameObjectTest} and other tests.
 * 
 * @author Martijn van de Rijdt
 */
public class DummyGameObject extends GameObject {
    /**
     * Constructor.
     * 
     * @param x
     *            x coordinate of the object's location
     * @param y
     *            y coordinate of the object's location
     * @param width
     *            object's width
     * @param height
     *            object's height
     */
    public DummyGameObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
