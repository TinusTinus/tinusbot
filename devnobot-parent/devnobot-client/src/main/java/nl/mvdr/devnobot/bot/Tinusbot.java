package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Tank;
import nl.mvdr.devnobot.model.Wall;

/**
 * Main bot.
 * 
 * @author Martijn van de Rijdt
 */
public class Tinusbot extends BotArtificialIntelligence {
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
    public Tinusbot(ClientApi clientApi, String name, Color color) {
        super(clientApi, name, color);
    }

    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API for making server calls
     */
    public Tinusbot(ClientApi clientApi) {
        this(clientApi, "Tinusbot 3000", Color.ORANGE);
    }

    /** {@inheritDoc} */
    @Override
    protected Action determineNextAction(Collection<Wall> obstacles, GameState state) {
        Action result;
        if (state.wouldHitEnemy(getName(), obstacles)) {
            result = Action.FIRE;
        } else if (nonDummyEnemyHasAShot(obstacles, state)) {
            // Suicide to prevent them from getting the kill.
            // TODO test if this actually helps!
            result = Action.SUICIDE;
        } else {
            // TODO move to a position where we can fire?
            result = null;
        }
        return result;
    }
    
    /**
     * Determines whether a non-dummy enemy is aiming at our tank.
     * 
     * @param obstacles
     *            walls
     * @param state
     *            game state
     * @return whether a bot is aiming at us
     */
    private boolean nonDummyEnemyHasAShot(Collection<Wall> obstacles, GameState state) {
        boolean result = false;
        Tank ownTank = state.retrieveTankForPlayerName(getName());
        Iterator<Tank> enemyIterator = state.retrieveEnemies(getName()).iterator();
        while (!result && enemyIterator.hasNext()) {
            Tank enemy = enemyIterator.next();
            result = !enemy.isProbablyADummy() && state.wouldHit(enemy.getPlayer(), obstacles) == ownTank;
        }
        return result;
    }

}
