package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.Collection;
import java.util.Random;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Wall;

/**
 * Dummy bot that just takes random actions.
 * 
 * Functionally equal to the example dummy bot provided by CGI.
 * 
 * @author Martijn van de Rijdt
 */
public class DummyBot extends BotArtificialIntelligence {
    /** Default name for bots of this type. */
    public static final String DEFAULT_NAME = "Dummy";
    /** Sleep duration after every execution of the main game loop in milliseconds. */
    private static final int THREAD_SLEEP_DURATION = 1000;
    
    /** Random generator. */
    private final Random randomGenerator;
    /** Actions that this bot can perform. */
    // all except suicide
    private Action[] ACTIONS = new Action[] { Action.FORWARD, Action.BACKWARD, Action.TURN_LEFT, Action.TURN_RIGHT,
            Action.FIRE };

    /**
     * Constructor.
     * 
     * @param api
     *            api
     * @param name
     *            name
     * @param color
     *            color
     */
    public DummyBot(ClientApi api, String name, Color color) {
        super(api, name, color, THREAD_SLEEP_DURATION);
        this.randomGenerator = new Random();
    }
    
    /**
     * Constructor.
     * 
     * @param api
     *            api
     * @param color
     *            color
     */
    public DummyBot(ClientApi api, Color color) {
        this(api, DEFAULT_NAME, color);
    }

    /** {@inheritDoc} */
    @Override
    protected Action determineNextAction(Collection<Wall> walls, GameState state) {
        return ACTIONS[randomGenerator.nextInt(ACTIONS.length)];
    }
}
