package nl.mvdr.devnobot.bot;

import java.util.Collection;
import java.util.Random;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Wall;

/**
 * Dummy bot that just takes random actions.
 * 
 * @author Martijn van de Rijdt
 */
public class DummyBot extends BotArtificialIntelligence {
    /** Default name for bots of this type. */
    public static final String DEFAULT_NAME = "Dummy";
    
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
    public DummyBot(ClientApi api, String name, String color) {
        super(api, name, color);
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
    public DummyBot(ClientApi api, String color) {
        this(api, DEFAULT_NAME, color);
    }

    /** {@inheritDoc} */
    @Override
    protected Action determineNextAction(Collection<Wall> walls, GameState state) {
        return ACTIONS[randomGenerator.nextInt(ACTIONS.length)];
    }
}
