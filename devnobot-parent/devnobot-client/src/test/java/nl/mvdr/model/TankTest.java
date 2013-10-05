package nl.mvdr.model;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Test;

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
        Tank tank = createNamedTank("name");
        
        String string = tank.toString();
        
        log.info(string);
        Assert.assertNotNull(string);
        Assert.assertNotEquals("", string);
    }
    
    /** Tests {@link Tank#isProbablyADummy()}. */
    @Test
    public void testNotADummy() {
        Tank tank = createNamedTank("name");
        
        Assert.assertFalse(tank.isProbablyADummy());
    }

    /** Tests {@link Tank#isProbablyADummy()}. */
    @Test
    public void testDummy() {
        Tank tank = createNamedTank("Dummy");
        
        Assert.assertTrue(tank.isProbablyADummy());
    }
    
    /** Tests {@link Tank#isProbablyADummy()}. */
    @Test
    public void testNumberedDummy() {
        Tank tank = createNamedTank("Dummy2");
        
        Assert.assertTrue(tank.isProbablyADummy());
    }
    
    /** Tests {@link Tank#isProbablyADummy()}. */
    @Test
    public void testDummyCaseSensitive() {
        Tank tank = createNamedTank("dummy0");
        
        Assert.assertTrue(tank.isProbablyADummy());
    }
    
    /**
     * Creates a tank with the given name and dummy values for all other fields. 
     * 
     * @param name name
     * @return tank
     */
    private Tank createNamedTank(String name) {
        return new Tank(0, 1, 2, 3, Orientation.NORTH, 4, 5, name, 6);
    }
}
