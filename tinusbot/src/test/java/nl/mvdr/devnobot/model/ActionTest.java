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
    /** Tests {@link Action#toAPIAction()}. */
    @Test
    public void testToAPIActionForward() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.FORWARD, Action.FORWARD.toAPIAction());
    }
    
    /** Tests {@link Action#toAPIAction()}. */
    @Test
    public void testToAPIActionBackward() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.BACKWARD, Action.BACKWARD.toAPIAction());
    }

    
    /** Tests {@link Action#toAPIAction()}. */
    @Test
    public void testToAPIActionTurnLeft() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.TURN_LEFT, Action.TURN_LEFT.toAPIAction());
    }

    
    /** Tests {@link Action#toAPIAction()}. */
    @Test
    public void testToAPIActionTurnRight() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.TURN_RIGHT, Action.TURN_RIGHT.toAPIAction());
    }

    
    /** Tests {@link Action#toAPIAction()}. */
    @Test
    public void testToAPIActionFire() {
        Assert.assertEquals(com.cgi.devnobot.api.Action.FIRE, Action.FIRE.toAPIAction());
    }

    
    /** Tests {@link Action#toAPIAction()}. */
    @Test(expected = UnsupportedOperationException.class)
    public void testToAPIActionSuicide() {
        Action.SUICIDE.toAPIAction();
    }

}
