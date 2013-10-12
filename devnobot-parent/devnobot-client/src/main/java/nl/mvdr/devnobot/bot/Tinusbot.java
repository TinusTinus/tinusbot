package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Tank;
import nl.mvdr.devnobot.model.TankPosition;
import nl.mvdr.devnobot.model.Wall;

/**
 * Main bot.
 * 
 * @author Martijn van de Rijdt
 */
@Slf4j
public class Tinusbot extends BotArtificialIntelligence {
    /** Indicates whether this bot should suicide when another, non-dummy,  bot is aiming at it. */
    private final boolean suicideToEvade;
    
    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API for making server calls
     * @param name
     *            bot name
     * @param color
     *            tank color
     * @param suicideToEvade
     *            whether this bot should suicide when another, non-dummy, bot is aiming at it
     */
    public Tinusbot(ClientApi clientApi, String name, Color color, boolean suicideToEvade) {
        super(clientApi, name, color);
        this.suicideToEvade = suicideToEvade;
    }
    
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
        this(clientApi, name, color, false);
    }

    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API for making server calls
     */
    public Tinusbot(ClientApi clientApi) {
        this(clientApi, "Tinusbot 3000", Color.ORANGE, false);
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
                result = Action.FIRE;
                log.info("Firing at an enemy.");
            } else if (suicideToEvade && nonDummyEnemyHasAShot(obstacles, state, ownTank, enemies)) {
                // EVASIVE MANEUVERS!
                // Our tank is most likely too slow to get out of the way.
                // Suicide to prevent the enemy from getting the kill.
                result = Action.SUICIDE;
                log.info("Suiciding to evade enemy fire.");
            } else {
                // Move toward a position where we can fire.
                result = computeActionToMoveIntoFiringPosition(obstacles, state, ownTank, enemies);
                log.info("Moving toward firing position: " + result);
            }
        } else {
            // There are no enemies in the level (yet). But they should be here soon.
            // Fire in case someone spawns close by.
            result = Action.FIRE;
            log.info("Nobody's here (yet?). Firing blindly.");
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
            Collection<Tank> enemiesOfEnemy = new HashSet<>(enemies);
            enemiesOfEnemy.remove(enemy);
            enemiesOfEnemy.add(ownTank);
            result = !enemy.isProbablyADummy() && state.wouldHit(enemy, enemiesOfEnemy, obstacles) == ownTank;
        }
        return result;
    }

    /**
     * Determines the first move of a shortest path to a firing position. Uses a variation of Dijkstra's shortest path
     * algorithm.
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

        Action result = null;

        TankPosition startPosition = new TankPosition(ownTank);

        // map from visited reachable tank positions to the first action on a shortest path to get there
        Map<TankPosition, Action> visited = new HashMap<>();
        visited.put(startPosition, null);

        Map<Action, TankPosition> neighbours = startPosition.computeReachablePositions();
        Collection<TankPosition> positions = new HashSet<>();
        for (Entry<Action, TankPosition> entry : neighbours.entrySet()) {
            if (!nonDummyEnemyHasAShot(obstacles, state, entry.getValue().getTank(), enemies)
                    && !entry.getValue().getTank().overlaps(obstacles) && !entry.getValue().getTank().overlaps(enemies)) {
                visited.put(entry.getValue(), entry.getKey());
                positions.add(entry.getValue());
            }
        }

        while (result == null && !positions.isEmpty()) {
            Collection<TankPosition> newPositions = new HashSet<>();
            Iterator<TankPosition> positionIterator = positions.iterator();
            while (result == null && positionIterator.hasNext()) {
                TankPosition position = positionIterator.next();
                Action firstAction = visited.get(position);
                neighbours = position.computeReachablePositions();
                Iterator<TankPosition> directlyReachablePositionIterator = neighbours.values().iterator();
                while (result == null && directlyReachablePositionIterator.hasNext()) {
                    TankPosition directlyReachablePosition = directlyReachablePositionIterator.next();
                    if (!visited.containsKey(directlyReachablePosition)
                            && !directlyReachablePosition.getTank().overlaps(obstacles)) {
                        if (state.wouldHitEnemy(directlyReachablePosition.getTank(), enemies, obstacles)) {
                            result = firstAction;
                        }
                        visited.put(directlyReachablePosition, firstAction);
                        newPositions.add(directlyReachablePosition);
                    }
                }
            }
            positions = newPositions;
        }

        if (result == null) {
            // Unable to reach a point where we can shoot an opponent.
            // WELP
            result = Action.SUICIDE;
        }

        return result;
    }
}
