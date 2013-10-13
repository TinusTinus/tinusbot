package nl.mvdr.devnobot.model;

import lombok.Getter;
import lombok.ToString;
import nl.mvdr.devnobot.bot.DummyBot;

import com.cgi.devnobot.api.GameBot;

/**
 * Represents a tank in the game world.
 * 
 * @author Martijn van de Rijdt
 */
@Getter
@ToString(callSuper = true)
public class Tank extends GameObject {
    /**
     * Indicates the last known direction the tank was facing. This is also the direction it will fire bullets. Note
     * that this field may be null (at the start of the game).
     */
    private final Orientation lastKnownOrientation;
    /**
     * The time it takes to move forward/backward/turn left/turn right in milliseconds. Shooting is more time consuming.
     */
    private final int actionDuration;
    /** The distance a tank moves when moving forward or backward. */
    private final int distancePerStep;
    /** Name of the player corresponding to this tank. */
    private final String player;
    /** Current length of the action queue. */
    private final int queueLength;

    /**
     * Constructor.
     * 
     * @param gameBot
     *            game object as received from the client api
     */
    public Tank(GameBot gameBot) {
        super(gameBot);
        this.lastKnownOrientation = Orientation.fromCGIOrientation(gameBot.getLastKnownOrientation());
        this.actionDuration = gameBot.getActionDurationInMs();
        this.distancePerStep = gameBot.getDistancePerStep();
        this.player = gameBot.getPlayer();
        this.queueLength = gameBot.getQueueLength();
    }

    /**
     * Constructor. Intended for use in unit tests.
     * 
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     * @param width
     *            width
     * @param height
     *            height
     * @param orientation
     *            orientation
     * @param actionDuration
     *            action duration in milliseconds
     * @param distancePerStep
     *            distance per step
     * @param player
     *            player name
     * @param queueLength
     *            action queue length
     */
    Tank(int x, int y, int width, int height, Orientation orientation, int actionDuration, int distancePerStep,
            String player, int queueLength) {
        super(x, y, width, height);
        this.lastKnownOrientation = orientation;
        this.actionDuration = actionDuration;
        this.distancePerStep = distancePerStep;
        this.player = player;
        this.queueLength = queueLength;
    }

    /**
     * Guesses whether this tank is controlled by a dummy artificial intelligence, which is to say, an AI that randomly
     * performs moves.
     * 
     * In the provided example code, dummies are named "Dummy0", "Dummy1", ..., "Dummy7". In the final competition, four
     * players and four dummies will compete at once. Assuming the dummies are started using the same code as in the
     * example code, they can be recognised based on their player names. So this method returns true if and only if the
     * dummy's name starts with "Dummy" (case-insensitive).
     * 
     * Of course other players are free to name their bots something that starts with "Dummy" and CGI is free to name
     * their dummy bots something else on the day of the competition. Therefore the result of this method is not 100%
     * reliable. Hence the "probably" in the method name.
     * 
     * @return whether this tank is likely a dummy
     */
    public boolean isProbablyADummy() {
        return this.player != null && this.player.toLowerCase().startsWith(DummyBot.DEFAULT_NAME.toLowerCase());
    }

    /**
     * Determines whether this tank belongs to the player with the given name.
     * 
     * @param playerName
     *            player name
     * @return whether this tank belongs to the player
     */
    public boolean belongsToPlayer(String playerName) {
        return player.equals(playerName);
    }

    /**
     * Computes the location of a bullet if this tank were to fire right now.
     * 
     * @return hypothetical bullet
     */
    public Bullet computeBulletSpawnLocation() {
        int bulletWidth = Bullet.getBulletWidth();
        int bulletHeight = Bullet.getBulletHeight();
        int x = getX() + getWidth() / 2 - bulletWidth / 2;
        int y = getY() + getHeight() / 2 - bulletHeight / 2;
        return new Bullet(x, y, bulletWidth, bulletHeight);
    }

    /**
     * Determines the next location of the tank when executing the given action.
     * 
     * @param action
     *            action; may not be SUICIDE
     * @return hypothetical tank
     */
    public Tank computeNextPosition(Action action) {
        int newX;
        int newY;
        int newWidth;
        int newHeight;
        if (action == Action.TURN_LEFT || action == Action.TURN_RIGHT) {
            newX = getX() + getWidth() / 2 - getHeight() / 2;
            newY = getY() + getHeight() / 2 - getWidth() / 2;
            newWidth = getHeight();
            newHeight = getWidth();
        } else if (action == Action.FORWARD || action == Action.BACKWARD) {
            newX = getX() + distancePerStep * lastKnownOrientation.getXMultiplier() * action.getDirection();
            newY = getY() + distancePerStep * lastKnownOrientation.getYMultiplier() * action.getDirection();
            newWidth = getWidth();
            newHeight = getHeight();
        } else if (action == null || action == Action.FIRE) {
            newX = getX();
            newY = getY();
            newWidth = getWidth();
            newHeight = getHeight();
        } else if (action == Action.SUICIDE) {
            throw new IllegalArgumentException("Unable to determine new location. "
                    + "Suicide leads to the tank being respawned in a random location.");
        } else {
            throw new IllegalArgumentException("Unexpected action: " + action);
        }
        Orientation newOrientation = getLastKnownOrientation().newOrientation(action);

        return new Tank(newX, newY, newWidth, newHeight, newOrientation, actionDuration, distancePerStep, player,
                queueLength);
    }
}
