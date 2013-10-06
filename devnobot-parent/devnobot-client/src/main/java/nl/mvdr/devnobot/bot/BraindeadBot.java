package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.Collection;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
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
    protected Action determineNextAction(Collection<Wall> obstacles, GameState state) {
        return null;
    }
}
