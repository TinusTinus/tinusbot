package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameObject;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Leaderboard;
import nl.mvdr.devnobot.model.LevelBoundary;
import nl.mvdr.devnobot.model.PlayerAndPosition;
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
    /** Name of the tank we last fired at. Null at the start. */
    private String previousTarget;

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
        this.previousTarget = null;
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
        this.previousTarget = null;
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
        this.previousTarget = null;
        logVersion();
    }

    /** {@inheritDoc} */
    @Override
    protected List<Action> determineNextAction(Collection<Wall> obstacles, GameState state, Leaderboard leaderboard) {
        List<Action> result;

        Tank ownTank = state.retrieveTankForPlayerName(getName());
        Collection<Tank> enemies = state.retrieveEnemies(getName());
        Collection<Tank> targets = createTargets(enemies);
        LevelBoundary boundary = LevelBoundary.buildLevelBoundary(obstacles, state.getTanks());

        if (!enemies.isEmpty()) {
            GameObject target = state.wouldHit(ownTank, targets, obstacles, boundary);
            if (target instanceof Tank) {
                // Currently aiming at an enemy.
                // FIRE!
                // Even if they fire back and we die, it still nets us a point (2 for the kill minus 1 for the death).
                result = Arrays.asList(Action.FIRE);
                previousTarget = ((Tank) target).getPlayer();
                log.info("Firing at " + previousTarget);
            } else {
                // Move toward a position where we can fire.
                result = computeActionToMoveIntoFiringPosition(obstacles, state, ownTank, enemies, targets, boundary,
                        leaderboard);
            }
        } else {
            // There are no enemies in the level (yet). But they should be here soon.
            // Act like a rotating turret until then, we might get some lucky hits when enemies spawn.
            if (previousAction == Action.FIRE) {
                result = Arrays.asList(Action.TURN_RIGHT);
            } else {
                result = Arrays.asList(Action.FIRE);
            }
        }

        previousAction = result.get(result.size() - 1);

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
            result = isAThreat(enemy, leaderboard)
                    && state.wouldHit(enemy, enemiesOfEnemy, obstacles, boundary) == ownTank;
        }
        return result;
    }

    /**
     * Determines whether the tank is a threat, that is, whether it is a serious contender to win the current game.
     * 
     * @param tank
     *            enemy tank whose threat is to be assessed
     * @param leaderboard
     *            current leaderboard; may be null
     * @return whether the tank is a threat
     */
    private boolean isAThreat(Tank tank, Leaderboard leaderboard) {
        boolean result;

        if (leaderboard != null) {
            PlayerAndPosition playerAndPosition = leaderboard.retrievePosition(tank.getPlayer());
            if (playerAndPosition == null) {
                log.warn("Player does not occur on the leaderboards: " + tank.getPlayer());
                result = false;
            } else {
                // consider players in positions 1 and 2, with at least one kill, a threat
                result = playerAndPosition.getPosition() < 3 && playerAndPosition.getPlayer().getKills() != 0;
            }
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
     * @param threats
     *            all enemy tanks that may fire on our own tank
     * @param targets
     *            all enemy tanks that are to be considered a valid target; should be a subcollection of threats 
     * @param boundary
     *            bounds of the level
     * @param leaderboard
     *            current leaderboard; may be null
     * @return nonempty list of actions to be undertaken
     */
    private List<Action> computeActionToMoveIntoFiringPosition(Collection<Wall> obstacles, GameState state, Tank ownTank,
            Collection<Tank> threats, Collection<Tank> targets, LevelBoundary boundary, Leaderboard leaderboard) {
        
        List<Action> result = new ArrayList<>(2);

        TankPosition startPosition = new TankPosition(ownTank);

        // map from visited reachable tank positions to a shortest path to get there
        Map<TankPosition, List<Action>> visited = new HashMap<>();
        visited.put(startPosition, Collections.<Action>emptyList());

        Map<Action, TankPosition> neighbours = startPosition.computeReachablePositions();
        Collection<TankPosition> positions = new HashSet<>();
        for (Entry<Action, TankPosition> entry : neighbours.entrySet()) {
            if (entry.getValue().getTank().overlaps(boundary)
                    && !dangerousEnemyHasAShot(obstacles, state, entry.getValue().getTank(), threats, boundary,
                            leaderboard)
                    && (!(entry.getKey() == Action.FORWARD || entry.getKey() == Action.BACKWARD) || (!entry.getValue()
                            .getTank().overlaps(obstacles) && !entry.getValue().getTank().overlaps(threats)))) {
                visited.put(entry.getValue(), Arrays.asList(entry.getKey()));
                positions.add(entry.getValue());
            }
        }

        while (result.isEmpty() && !positions.isEmpty()) {
            Collection<TankPosition> newPositions = new HashSet<>();
            Iterator<TankPosition> positionIterator = positions.iterator();
            while (result.isEmpty() && positionIterator.hasNext()) {
                TankPosition position = positionIterator.next();
                List<Action> pathToPosition = visited.get(position);
                neighbours = position.computeReachablePositions();
                Iterator<Entry<Action, TankPosition>> directlyReachablePositionIterator = neighbours.entrySet()
                        .iterator();
                while (result.isEmpty() && directlyReachablePositionIterator.hasNext()) {
                    Entry<Action, TankPosition> directlyReachablePosition = directlyReachablePositionIterator.next();
                    if (directlyReachablePosition.getValue().getTank().overlaps(boundary)
                            && !visited.containsKey(directlyReachablePosition.getValue())
                            && (!(directlyReachablePosition.getKey() == Action.FORWARD || directlyReachablePosition
                                    .getKey() == Action.BACKWARD) || !directlyReachablePosition.getValue().getTank()
                                    .overlaps(obstacles))) {
                        List<Action> newPath = new ArrayList<>(pathToPosition);
                        newPath.add(directlyReachablePosition.getKey());
                        if (state.wouldHitEnemy(directlyReachablePosition.getValue().getTank(), targets, obstacles,
                                boundary)) {
                            result.add(newPath.get(0));
                            
                            // add second action as well, in case
                            // * it exists
                            // * first action is move forward or backward
                            // * second action is turn left or right
                            if (1 < newPath.size()
                                    && (newPath.get(0) == Action.BACKWARD || newPath.get(0) == Action.FORWARD)
                                    && (newPath.get(1) == Action.TURN_LEFT || newPath.get(1) == Action.TURN_RIGHT)) {
                                result.add(newPath.get(1));
                            }
                        }

                        visited.put(directlyReachablePosition.getValue(), newPath);
                        newPositions.add(directlyReachablePosition.getValue());
                    }
                }
            }
            positions = newPositions;
        }

        if (result.isEmpty()) {
            // Unable to reach a point where we can shoot an opponent.
            // WELP
            result.add(Action.SUICIDE);
        }

        return result;
    }

    /**
     * Constructs the collection of tanks to be considered a target.
     * 
     * If we fired at an enemy last turn, that enemy is not considered a valid target this turn. Chances are they will
     * have been destroyed by our previously fired bullet soon, so our tank should start moving towards its next target.
     * 
     * @param enemies
     *            enemies
     * @return subset of enemies to be considered a target
     */
    private Collection<Tank> createTargets(Collection<Tank> enemies) {
        Collection<Tank> targets;
        if (previousAction == Action.FIRE && previousTarget != null) {
            // Targets are all of our enemies except for our previous target.
            targets = new HashSet<>(enemies.size() - 1);
            for (Tank tank: enemies) {
                if (!previousTarget.equals(tank.getPlayer())) {
                    targets.add(tank);
                }
            }
        } else {
            // All enemies are a target.
            targets = enemies;
        }
        return targets;
    }
}
