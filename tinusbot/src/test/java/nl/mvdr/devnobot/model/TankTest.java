package nl.mvdr.devnobot.model;

import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.model.Orientation;
import nl.mvdr.devnobot.model.Tank;

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
    
    /**
     * Creates a tank with the given name and dummy values for all other fields. 
     * 
     * @param name name
     * @return tank
     */
    static Tank createNamedTank(String name) {
        return new Tank(0, 1, 2, 3, Orientation.NORTH, 4, 5, name, 6);
    }
}
