package nl.mvdr.devnobot.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@GameObject}.
 * 
 * @author Martijn van de Rijdt
 */
public class GameObjectTest {
    /** Tests {@link GameObject#overlaps(GameObject)} when the objects are the same. */
    @Test
    public void testOverlapsSame() {
        GameObject object = new DummyGameObject(0, 0, 1, 1);

        Assert.assertTrue(object.overlaps(object));
    }

    /**
     * Tests {@link GameObject#overlaps(GameObject)} when the objects do not overlap at all.
     * 
     * <pre>
     * 0
     *  1
     * </pre>
     */
    @Test
    public void testOverlapsNoOverlap() {
        GameObject object0 = new DummyGameObject(0, 0, 1, 1);
        GameObject object1 = new DummyGameObject(2, 2, 1, 1);

        Assert.assertFalse(object0.overlaps(object1));
        Assert.assertFalse(object1.overlaps(object0));
    }

    /**
     * Tests {@link GameObject#overlaps(GameObject)} when one object is north of the other.
     * 
     * <pre>
     * 00
     *  11
     * </pre>
     */
    @Test
    public void testOverlapsOverEachOther() {
        GameObject object0 = new DummyGameObject(0, 0, 2, 1);
        GameObject object1 = new DummyGameObject(1, 1, 2, 1);

        Assert.assertFalse(object0.overlaps(object1));
        Assert.assertFalse(object1.overlaps(object0));
    }

    /**
     * Tests {@link GameObject#overlaps(GameObject)} when one object is east of the other.
     * 
     * <pre>
     * 0
     * 01
     *  1
     * </pre>
     */
    @Test
    public void testOverlapsNextToEachOther() {
        GameObject object0 = new DummyGameObject(0, 0, 1, 2);
        GameObject object1 = new DummyGameObject(1, 1, 1, 2);

        Assert.assertFalse(object0.overlaps(object1));
        Assert.assertFalse(object1.overlaps(object0));
    }

    /**
     * Tests {@link GameObject#overlaps(GameObject)} when they do overlap in one position.
     * 
     * <pre>
     * 00
     * 0X1
     *  11
     * </pre>
     */
    @Test
    public void testOverlapsOverlap() {
        GameObject object0 = new DummyGameObject(0, 0, 2, 2);
        GameObject object1 = new DummyGameObject(1, 1, 2, 2);

        Assert.assertTrue(object0.overlaps(object1));
        Assert.assertTrue(object1.overlaps(object0));
    }
}
