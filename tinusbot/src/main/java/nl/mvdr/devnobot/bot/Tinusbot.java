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
import nl.mvdr.devnobot.model.Leaderboard;
import nl.mvdr.devnobot.model.LevelBoundary;
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
    /** Action taken during the previous turn. Null at the start. */
    private Action previousAction;
    
    /**
     * Returns the version number from the jar manifest file.
     * 
     * @return version number, or null if it cannot be determined
     */
    private static String retrieveVersion() {
        String result;
        Package p = Tinusbot.class.getPackage();
        if (p != null) {
            result = p.getImplementationVersion();
        } else {
            result = null;
        }
        return result;
    }

    /** @return default name for the Tinusbot */
    public static String retrieveDefaultName() {
        String result = "Tinusbot";

        String version = retrieveVersion();
        if (version != null) {
            result = result + " " + version;
        }

        return result;
    }
    
    /** Logs the version number. */
    private static void logVersion() {
        String version = retrieveVersion();
        if (version != null) {
            log.info("Tinusbot version " + version);
        } else {
            log.warn("Version information unavailable.");
        }
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
        super(clientApi, name, color);
        this.previousAction = null;
        logVersion();
    }

    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API for making server calls
     */
    public Tinusbot(ClientApi clientApi) {
        this(clientApi, retrieveDefaultName(), Color.ORANGE);
    }
    
    /**
     * Constructor.
     * 
     * @param host
     *            host name / base URL for the game server
     * @param name
     *            player name
     * @param color
     *            tank color
     */
    public Tinusbot(String host, String name, Color color) {
        super(host, name, color);
        this.previousAction = null;
        logVersion();
    }

    /**
     * Constructor.
     * 
     * @param host
     *            host name / base URL for the game server
     * @param name
     *            player name
     * @param color
     *            tank color
     */
    public Tinusbot(String host, String name, String color) {
        super(host, name, color);
        this.previousAction = null;
        logVersion();
    }
    
    /** {@inheritDoc} */
    @Override
    protected Action determineNextAction(Collection<Wall> obstacles, GameState state, Leaderboard leaderboard) {
        Action result;

        Tank ownTank = state.retrieveTankForPlayerName(getName());
        Collection<Tank> enemies = state.retrieveEnemies(getName());
        LevelBoundary boundary = LevelBoundary.buildLevelBoundary(obstacles, state.getTanks());

        if (!enemies.isEmpty()) {
            if (state.wouldHitEnemy(ownTank, enemies, obstacles, boundary) && previousAction != Action.FIRE) {
                // FIRE!
                // Even if they fire back and we die, it still nets us a point (2 for the kill minus 1 for the death).
                result = Action.FIRE;
            } else {
                // Move toward a position where we can fire.
                result = computeActionToMoveIntoFiringPosition(obstacles, state, ownTank, enemies, boundary,
                        leaderboard);
            }
        } else {
            // There are no enemies in the level (yet). But they should be here soon.
            // Act like a rotating turret until then, we might get some lucky hits when enemies spawn.
            if (previousAction == Action.FIRE) {
                result = Action.TURN_RIGHT;
            } else {
                result = Action.FIRE;
            }
        }
        
        previousAction = result;
        
        return result;
    }

    /**
     * Determines whether a dangerous enemy is aiming at our tank.
     * 
     * If a dummy, or other low-ranked enemy, is aiming at our tank it doesn't really matter. There's only a 1 in 5
     * chance that a dummy will actually fire. Even if it does, it doesn't matter that it gets the kill, since they're
     * not a serious contender. The only downside is the death which only costs one point.
     * 
     * @param obstacles
     *            walls
     * @param state
     *            game state
     * @param ownTank
     *            own tank
     * @param enemies
     *            all enemy tanks
     * @param boundary
     *            bounds of the level
     * @param leaderboard
     *            current leaderboard; may be null
     * @return whether a bot is aiming at us
     */
    private boolean dangerousEnemyHasAShot(Collection<Wall> obstacles, GameState state, Tank ownTank,
            Collection<Tank> enemies, LevelBoundary boundary, Leaderboard leaderboard) {
        boolean result = false;
        Iterator<Tank> enemyIterator = enemies.iterator();
        while (!result && enemyIterator.hasNext()) {
            Tank enemy = enemyIterator.next();
            Collection<Tank> enemiesOfEnemy = new HashSet<>(enemies);
            enemiesOfEnemy.remove(enemy);
            enemiesOfEnemy.add(ownTank);
            result = isAThreat(enemy, ownTank, leaderboard)
                    && state.wouldHit(enemy, enemiesOfEnemy, obstacles, boundary) == ownTank;
        }
        return result;
    }

    /**
     * Determines whether the tank is a threat, that is, whether it is a serious contender to win the current game.
     * 
     * @param tank
     *            enemy tank whose threat is to be assessed
     * @param ownTank
     *            our own tank
     * @param leaderboard
     *            current leaderboard; may be null
     * @return whether the tank is a threat
     */
    // TODO make private again
    protected boolean isAThreat(Tank tank, Tank ownTank, Leaderboard leaderboard) {
        boolean result;

        if (leaderboard != null) {
            Integer ownPosition = leaderboard.retrievePosition(ownTank.getPlayer());
            Integer enemyPosition = leaderboard.retrievePosition(tank.getPlayer());
            result = ownPosition != null && enemyPosition != null
                    && enemyPosition.intValue() < ownPosition.intValue() + 2;
        } else {
            // no leaderboards yet; default to false
            result = false;
        }

        return result;
    }

    /**
     * Determines the first move of a shortest path to a firing position.
     * 
     * This method uses a variation of Dijkstra's shortest path algorithm. The game state is seen as a complete,
     * directed graph. The nodes are positions (including orientation) that our own tank can reach. The edges are the
     * following actions that move the tank: forward, backward, turn left and turn right.
     * 
     * @param obstacles
     *            walls
     * @param state
     *            game state
     * @param ownTank
     *            own tank
     * @param enemies
     *            all enemy tanks
     * @param boundary
     *            bounds of the level
     * @param leaderboard
     *            current leaderboard; may be null
     * @return action
     */
    private Action computeActionToMoveIntoFiringPosition(Collection<Wall> obstacles, GameState state, Tank ownTank,
            Collection<Tank> enemies, LevelBoundary boundary, Leaderboard leaderboard) {

        Action result = null;

        TankPosition startPosition = new TankPosition(ownTank);

        // map from visited reachable tank positions to the first action on a shortest path to get there
        Map<TankPosition, Action> visited = new HashMap<>();
        visited.put(startPosition, null);

        Map<Action, TankPosition> neighbours = startPosition.computeReachablePositions();
        Collection<TankPosition> positions = new HashSet<>();
        for (Entry<Action, TankPosition> entry : neighbours.entrySet()) {
            if (entry.getValue().getTank().overlaps(boundary)
                    && !dangerousEnemyHasAShot(obstacles, state, entry.getValue().getTank(), enemies, boundary,
                            leaderboard)
                    && (!(entry.getKey() == Action.FORWARD || entry.getKey() == Action.BACKWARD) || (!entry.getValue()
                            .getTank().overlaps(obstacles) && !entry.getValue().getTank().overlaps(enemies)))) {
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
                Iterator<Entry<Action, TankPosition>> directlyReachablePositionIterator = neighbours.entrySet()
                        .iterator();
                while (result == null && directlyReachablePositionIterator.hasNext()) {
                    Entry<Action, TankPosition> directlyReachablePosition = directlyReachablePositionIterator.next();
                    if (directlyReachablePosition.getValue().getTank().overlaps(boundary)
                            && !visited.containsKey(directlyReachablePosition.getValue())
                            && (!(directlyReachablePosition.getKey() == Action.FORWARD || directlyReachablePosition
                                    .getKey() == Action.BACKWARD) || !directlyReachablePosition.getValue().getTank()
                                    .overlaps(obstacles))) {
                        if (state.wouldHitEnemy(directlyReachablePosition.getValue().getTank(), enemies, obstacles,
                                boundary)) {
                            result = firstAction;
                        }
                        visited.put(directlyReachablePosition.getValue(), firstAction);
                        newPositions.add(directlyReachablePosition.getValue());
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
