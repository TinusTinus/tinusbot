package nl.mvdr.devnobot.model;

import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.model.Bullet;
import nl.mvdr.devnobot.model.Wall;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Wall}.
 * 
 * @author Martijn van de Rijdt
 */
@Slf4j
public class WallTest {
    /** Test method for {@link Wall#toString()}. */
    @Test
    public void testToString() {
        Wall wall = new Wall(0, 1, 2, 3);
        
        String string = wall.toString();
        
        log.info(string);
        Assert.assertNotNull(string);
        Assert.assertNotEquals("", string);
    }
    
    /** Tests whether equals can tell that walls and bullets are not the same thing. */
    @Test
    public void testEquals() {
        Wall wall = new Wall(0, 1, 2, 3);
        Bullet bullet = new Bullet(0, 1, 2, 3);
        
        Assert.assertNotEquals(wall, bullet);
    }
}
