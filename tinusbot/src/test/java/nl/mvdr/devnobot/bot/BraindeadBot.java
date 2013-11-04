package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Leaderboard;
import nl.mvdr.devnobot.model.Wall;

/**
 * Bot that does not act at all.
 * 
 * @author Martijn van de Rijdt
 */
public class BraindeadBot extends BotArtificialIntelligence {
    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API for making server calls
     * @param name
     *            bot name
     * @param color
     *            tank color
     */
    public BraindeadBot(ClientApi clientApi, String name, Color color) {
        super(clientApi, name, color);
    }

    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API for making server calls
     */
    public BraindeadBot(ClientApi clientApi) {
        this(clientApi, "Braindead Bot", Color.WHITE);
    }

    /** {@inheritDoc} */
    @Override
    protected List<Action> determineNextAction(Collection<Wall> obstacles, GameState state, Leaderboard leaderboard) {
        return Collections.emptyList();
    }
}
