package nl.mvdr.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Action}.
 * 
 * @author Martijn van de Rijdt
 */
public class ActionTest {
    /** Tests {@link Action#getCGIAction()}. */
    @Test
    public void testGetCGIActionForward() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.FORWARD, Action.FORWARD.getCGIAction());
    }
    
    /** Tests {@link Action#getCGIAction()}. */
    @Test
    public void testGetCGIActionBackward() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.BACKWARD, Action.BACKWARD.getCGIAction());
    }

    
    /** Tests {@link Action#getCGIAction()}. */
    @Test
    public void testGetCGIActionTurnLeft() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.TURN_LEFT, Action.TURN_LEFT.getCGIAction());
    }

    
    /** Tests {@link Action#getCGIAction()}. */
    @Test
    public void testGetCGIActionTurnRight() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.TURN_RIGHT, Action.TURN_RIGHT.getCGIAction());
    }

    
    /** Tests {@link Action#getCGIAction()}. */
    @Test
    public void testGetCGIActionFire() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.FIRE, Action.FIRE.getCGIAction());
    }

    
    /** Tests {@link Action#getCGIAction()}. */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetCGIActionSuicide() {
        Action.SUICIDE.getCGIAction();
    }

}
