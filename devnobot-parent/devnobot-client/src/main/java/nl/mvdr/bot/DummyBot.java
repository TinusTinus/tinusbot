package nl.mvdr.bot;

import java.util.List;
import java.util.Random;

import nl.mvdr.model.Action;

import com.cgi.devnobot.api.GameObstacle;
import com.cgi.devnobot.api.World;
import com.cgi.devnobot.client.ClientApi;

/**
 * Dummy bot that just takes random actions.
 * 
 * @author Martijn van de Rijdt
 */
public class DummyBot extends Bot {
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

    /** {@inheritDoc} */
    @Override
    protected Action determineNextAction(List<GameObstacle> obstacles, World world) {
        return ACTIONS[randomGenerator.nextInt(ACTIONS.length)];
    }
}
