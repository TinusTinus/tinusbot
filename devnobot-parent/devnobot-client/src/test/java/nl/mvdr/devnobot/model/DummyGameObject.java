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
     * @param y
     * @param width
     * @param height
     */
    public DummyGameObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
