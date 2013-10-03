package nl.mvdr.bot;

import java.util.List;
import java.util.Random;

import com.cgi.devnobot.api.Action;
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
        return Action.values()[randomGenerator.nextInt(Action.values().length)];
    }
}
