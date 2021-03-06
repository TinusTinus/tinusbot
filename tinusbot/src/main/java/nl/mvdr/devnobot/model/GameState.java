package nl.mvdr.devnobot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import com.cgi.devnobot.api.GameBot;
import com.cgi.devnobot.api.GameBullet;
import com.cgi.devnobot.api.World;

/**
 * Representation of the current game state.
 * 
 * Only contains dynamic objects, that is, the objects that can actually appear, disappear and move: tanks and bullets.
 * Walls are not included.
 * 
 * @author Martijn van de Rijdt
 */
@ToString
@Getter
@Slf4j
public class GameState {
    /** Tanks in the game world. */
    private final Collection<Tank> tanks;
    /** Bullets. */
    private final Collection<Bullet> bullets;

    /**
     * Constructor.
     * 
     * @param world
     *            world as received from the client api
     */
    public GameState(World world) {
        super();

        Collection<Tank> tempTanks = new ArrayList<>(world.getBots().size());
        for (GameBot gameBot : world.getBots()) {
            tempTanks.add(new Tank(gameBot));
        }
        this.tanks = Collections.unmodifiableCollection(tempTanks);

        Collection<Bullet> tempBullets = new ArrayList<>(world.getBullets().size());
        for (GameBullet gameBullet : world.getBullets()) {
            tempBullets.add(new Bullet(gameBullet));
        }
        this.bullets = Collections.unmodifiableCollection(tempBullets);
    }

    /**
     * Constructor. Intended for use in unit tests.
     * 
     * @param tanks
     *            tanks
     * @param bullets
     *            bullets
     */
    GameState(Collection<Tank> tanks, Collection<Bullet> bullets) {
        super();
        this.tanks = Collections.unmodifiableCollection(tanks);
        this.bullets = Collections.unmodifiableCollection(bullets);
    }

    /**
     * Convenience constructor. Intended for use in unit tests.
     * 
     * @param tanks
     *            tanks
     */
    GameState(Collection<Tank> tanks) {
        this(tanks, Collections.<Bullet>emptyList());
    }

    /**
     * Retrieves the given player's tank.
     * 
     * @param name
     *            player name; should be non-null and unique; if not unique, the first tank matching the player name is
     *            returned
     * @return tank belonging to the player with the given name
     * @throws IllegalArgumentException
     *             if there is no matching tank
     */
    public Tank retrieveTankForPlayerName(String name) {
        Tank result = null;
        Iterator<Tank> iterator = tanks.iterator();
        while (result == null && iterator.hasNext()) {
            Tank tank = iterator.next();
            if (tank.belongsToPlayer(name)) {
                result = tank;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Unknown player name: " + name);
        }

        return result;
    }

    /**
     * Retrieves the given player's enemies.
     * 
     * @param name
     *            player name; should be non-null and unique
     * @return tank belonging to the player with the given name
     */
    public Collection<Tank> retrieveEnemies(String name) {
        Collection<Tank> result;

        if (tanks.isEmpty()) {
            log.warn("Game state does not contain any tanks.");
            result = Collections.emptyList();
        } else {
            int expectedNumberOfEnemies = tanks.size() - 1;
            result = new ArrayList<>(expectedNumberOfEnemies);
            for (Tank tank : tanks) {
                if (!tank.belongsToPlayer(name)) {
                    result.add(tank);
                }
            }

            if (result.size() < expectedNumberOfEnemies) {
                log.warn("Expected {} enemy tank(s) but found {}. Possible duplicate player names. Actual enemies: {}",
                        new Object[] { "" + expectedNumberOfEnemies, "" + result.size(), result });
            } else if (expectedNumberOfEnemies < result.size()) {
                log.warn("Expected {} enemy tank(s) but found {}. Possibly the given player name does not own a tank. "
                        + "Actual enemies: {}",
                        new Object[] { "" + expectedNumberOfEnemies, "" + result.size(), result });
            }

            result = Collections.unmodifiableCollection(result);
        }

        return result;
    }

    /**
     * Determines which, if any, game object the given tank would hit if it fired right now.
     * 
     * @param tank
     *            tank which would fire
     * @param enemies
     *            tank's enemies
     * @param walls
     *            walls / obstacles in the level
     * @param boundary
     *            level boundary
     * @return game object which would be hit, or null if there is none
     */
    public GameObject wouldHit(Tank tank, Collection<Tank> enemies, Collection<Wall> walls, LevelBoundary boundary) {
        // Build a collection of objects that the bullet cannot pass through. That is, all enemy tanks and walls.
        Collection<GameObject> objects = new ArrayList<>(tanks.size() - 1 + walls.size());
        objects.addAll(enemies);
        objects.addAll(walls);

        // Follow the trajectory of the bullet until it hits an object or goes flying out of bounds.
        GameObject result = null;
        Bullet bullet = tank.computeBulletSpawnLocation();
        while (result == null 
                && boundary.getX() < bullet.getX() + bullet.getWidth() && bullet.getX() < boundary.computeMaxX()
                && boundary.getY() < bullet.getY() + bullet.getWidth() && bullet.getY() < boundary.computeMaxY()) {
            for (GameObject object : objects) {
                if (bullet.overlaps(object)) {
                    result = object;
                }
            }
            bullet = bullet.moveBulletLength(tank.getLastKnownOrientation());
        }

        return result;
    }

    /**
     * Determines which, if any, game object the given player's tank would hit if it fired right now.
     * 
     * @param playerName
     *            player name; should be non-null and unique; if not unique, the first tank matching the player name is
     *            returned
     * @param walls
     *            walls / obstacles in the level
     * @return game object which would be hit, or null if there is none
     * @throws IllegalArgumentException
     *             if there is no matching tank for the given player name
     */
    public GameObject wouldHit(String playerName, Collection<Wall> walls) {
        Tank tank = retrieveTankForPlayerName(playerName);
        Collection<Tank> enemies = retrieveEnemies(tank.getPlayer());
        LevelBoundary boundary = LevelBoundary.buildLevelBoundary(walls, tanks);
        return wouldHit(tank, enemies, walls, boundary);
    }

    /**
     * Determines whether the given tank would hit an enemy if it fired right now.
     * 
     * @param tank
     *            tank
     * @param walls
     *            walls / obstacles in the level
     * @param boundary
     *            level boundary
     * @return game object which would be hit, or null if there is none
     * @throws IllegalArgumentException
     *             if there is no matching tank for the given player name
     */
    public boolean wouldHitEnemy(Tank tank, Collection<Tank> enemies, Collection<Wall> walls, LevelBoundary boundary) {
        return wouldHit(tank, enemies, walls, boundary) instanceof Tank;
    }

    /**
     * Determines whether the given tank would hit an enemy if it fired right now.
     * 
     * @param tank
     *            tank
     * @param walls
     *            walls / obstacles in the level
     * @return game object which would be hit, or null if there is none
     * @throws IllegalArgumentException
     *             if there is no matching tank for the given player name
     */
    public boolean wouldHitEnemy(Tank tank, Collection<Tank> enemies, Collection<Wall> walls) {
        return wouldHit(tank, enemies, walls, LevelBoundary.buildLevelBoundary(walls, tanks)) instanceof Tank;
    }
}
