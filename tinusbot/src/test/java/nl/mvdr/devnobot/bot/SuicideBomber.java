package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.Collection;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Leaderboard;
import nl.mvdr.devnobot.model.Wall;

/**
 * Suicide bomber artificial intelligence. Alternately fires and suicides.
 * 
 * @author Martijn van de Rijdt
 */
public class SuicideBomber extends BotArtificialIntelligence {
    /** Previous action. */
    private Action previousAction;
    
    /**
     * Constructor.
     * 
     * @param clientApi api
     */
    public SuicideBomber(ClientApi clientApi) {
        super(clientApi, "Suicide Bomber", Color.RED);
        previousAction = null;
    }

    /** {@inheritDoc} */
    @Override
    protected Action determineNextAction(Collection<Wall> obstacles, GameState state, Leaderboard leaderboard) {
        if (previousAction == Action.FIRE) {
            previousAction = Action.SUICIDE;
        } else {
            previousAction = Action.FIRE;
        }
        return previousAction;
    }

}
