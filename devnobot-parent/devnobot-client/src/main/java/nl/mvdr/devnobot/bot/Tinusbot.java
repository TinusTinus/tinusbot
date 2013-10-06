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

        Tank ownTank = state.retrieveTankForPlayerName(getName());
        Collection<Tank> enemies = state.retrieveEnemies(getName());

        if (!enemies.isEmpty()) {
            if (state.wouldHitEnemy(ownTank, enemies, obstacles)) {
                // FIRE!
                // Even if they fire back and we die, it still nets us a point (2 for the kill minus 1 for the death).
                // TODO prevent firing multiple bullets at far-away enemies?
                result = Action.FIRE;
            } else if (nonDummyEnemyHasAShot(obstacles, state, ownTank, enemies)) {
                // EVASIVE MANEUVERS!
                // Our tank is most likely too slow to get out of the way.
                // Suicide to prevent the enemy from getting the kill.
                // TODO test if this actually helps!
                result = Action.SUICIDE;
            } else {
                // Move toward a position where we can fire.
                result = computeActionToMoveIntoFiringPosition(obstacles, state, ownTank, enemies);
            }
        } else {
            // There are no enemies in the level (yet).
            // TODO move towards the center of the map?
            result = null;
        }
        return result;
    }

    /**
     * Determines whether a non-dummy enemy is aiming at our tank.
     * 
     * If a dummy is aiming at our tank it doesn't really matter. There's only a 1 in 5 chance that it will actually
     * fire. Even if it does, it doesn't matter that it gets the kill, since they're not a serious contender. The only
     * downside is the death which only costs one point.
     * 
     * @param obstacles
     *            walls
     * @param state
     *            game state
     * @param ownTank
     *            own tank
     * @param enemies
     *            all enemy tanks
     * @return whether a bot is aiming at us
     */
    private boolean nonDummyEnemyHasAShot(Collection<Wall> obstacles, GameState state, Tank ownTank,
            Collection<Tank> enemies) {
        boolean result = false;
        Iterator<Tank> enemyIterator = enemies.iterator();
        while (!result && enemyIterator.hasNext()) {
            Tank enemy = enemyIterator.next();
            result = !enemy.isProbablyADummy() && state.wouldHit(enemy.getPlayer(), obstacles) == ownTank;
        }
        return result;
    }

    /**
     * Determines the first move of a shortest path to a firing position.
     * 
     * @param obstacles
     *            walls
     * @param state
     *            game state
     * @param ownTank
     *            own tank
     * @param enemies
     *            all enemy tanks
     * @return action
     */
    private Action computeActionToMoveIntoFiringPosition(Collection<Wall> obstacles, GameState state, Tank ownTank,
            Collection<Tank> enemies) {
        return null; // TODO implement
    }
}
