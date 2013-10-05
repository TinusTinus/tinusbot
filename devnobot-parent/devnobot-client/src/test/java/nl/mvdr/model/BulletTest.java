package nl.mvdr.model;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Bullet}.
 * 
 * @author Martijn van de Rijdt
 */
@Slf4j
public class BulletTest {
    /** Test method for {@link Bullet#toString()}. */
    @Test
    public void testToString() {
        Bullet bullet = new Bullet(0, 1, 2, 3);
        
        String string = bullet.toString();
        
        log.info(string);
        Assert.assertNotNull(string);
        Assert.assertNotEquals("", string);
    }
}
