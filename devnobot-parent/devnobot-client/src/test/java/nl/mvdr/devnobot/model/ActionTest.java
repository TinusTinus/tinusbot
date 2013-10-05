package nl.mvdr.devnobot.model;

import nl.mvdr.devnobot.model.Action;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Action}.
 * 
 * @author Martijn van de Rijdt
 */
public class ActionTest {
    /** Tests {@link Action#toCGIAction()}. */
    @Test
    public void testGetCGIActionForward() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.FORWARD, Action.FORWARD.toCGIAction());
    }
    
    /** Tests {@link Action#toCGIAction()}. */
    @Test
    public void testGetCGIActionBackward() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.BACKWARD, Action.BACKWARD.toCGIAction());
    }

    
    /** Tests {@link Action#toCGIAction()}. */
    @Test
    public void testGetCGIActionTurnLeft() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.TURN_LEFT, Action.TURN_LEFT.toCGIAction());
    }

    
    /** Tests {@link Action#toCGIAction()}. */
    @Test
    public void testGetCGIActionTurnRight() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.TURN_RIGHT, Action.TURN_RIGHT.toCGIAction());
    }

    
    /** Tests {@link Action#toCGIAction()}. */
    @Test
    public void testGetCGIActionFire() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.FIRE, Action.FIRE.toCGIAction());
    }

    
    /** Tests {@link Action#toCGIAction()}. */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetCGIActionSuicide() {
        Action.SUICIDE.toCGIAction();
    }

}
