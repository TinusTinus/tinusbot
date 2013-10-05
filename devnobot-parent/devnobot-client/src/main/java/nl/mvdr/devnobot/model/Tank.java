package nl.mvdr.devnobot.model;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class Tank extends GameObject {
    /** Indicates which direction the tank is facing. This is also the direction it will fire bullets. */
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
}