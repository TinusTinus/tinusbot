package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.Collection;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Leaderboard;
import nl.mvdr.devnobot.model.Wall;

/**
 * Alternately fires and turns.
 * 
 * @author Martijn van de Rijdt
 */
public class RotatingTurret extends BotArtificialIntelligence {
    /** Previous action. */
    private Action previousAction;

    /**
     * Constructor.
     * 
     * @param clientApi
     *            api
     */
    public RotatingTurret(ClientApi clientApi) {
        super(clientApi, "Rotating Turret", Color.LIGHT_GRAY);
        previousAction = null;
    }

    /** {@inheritDoc} */
    @Override
    protected Action determineNextAction(Collection<Wall> obstacles, GameState state, Leaderboard leaderboard) {
        if (previousAction == Action.FIRE) {
            previousAction = Action.TURN_RIGHT;
        } else {
            previousAction = Action.FIRE;
        }
        return previousAction;
    }

}
