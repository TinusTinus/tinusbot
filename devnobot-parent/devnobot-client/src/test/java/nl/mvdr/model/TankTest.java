package nl.mvdr.model;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.cgi.devnobot.api.Orientation;

/**
 * Test class for {@link Tank}.
 * 
 * @author Martijn van de Rijdt
 */
@Slf4j
public class TankTest {
    /** Tests {@link Tank#toString()}. */
    @Test
    public void testToString() {
        Tank tank = new Tank(0, 1, 2, 3, Orientation.UP, 4, 5, "name", 6);
        
        String string = tank.toString();
        
        log.info(string);
        Assert.assertNotNull(string);
        Assert.assertNotEquals("", string);
    }
    
    /** Tests {@link Tank#isProbablyADummy()}. */
    @Test
    public void testNotADummy() {
        Tank tank = new Tank(0, 1, 2, 3, Orientation.UP, 4, 5, "name", 6);
        
        Assert.assertFalse(tank.isProbablyADummy());
    }
    
    /** Tests {@link Tank#isProbablyADummy()}. */
    @Test
    public void testDummy() {
        Tank tank = new Tank(0, 1, 2, 3, Orientation.UP, 4, 5, "Dummy", 6);
        
        Assert.assertTrue(tank.isProbablyADummy());
    }
    
    /** Tests {@link Tank#isProbablyADummy()}. */
    @Test
    public void testNumberedDummy() {
        Tank tank = new Tank(0, 1, 2, 3, Orientation.UP, 4, 5, "Dummy2", 6);
        
        Assert.assertTrue(tank.isProbablyADummy());
    }
    
    /** Tests {@link Tank#isProbablyADummy()}. */
    @Test
    @Ignore // TODO fix!
    public void testDummyCaseSensitive() {
        Tank tank = new Tank(0, 1, 2, 3, Orientation.UP, 4, 5, "dummy0", 6);
        
        Assert.assertTrue(tank.isProbablyADummy());
    }
}
