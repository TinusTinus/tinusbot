package nl.mvdr.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.cgi.devnobot.api.GameBot;
import com.cgi.devnobot.api.Orientation;

/**
 * Represents a tank in the game world.
 * 
 * Contains the status of a certain bot (tank).
 *
 * - lastKnownOrientation is useful to determine which direction a tank is facing (and might possibly fire)
 * - queueLength every tank has a queue of tasks to perform. It is advisable to keep this queue close to empty. Every action
 *   is added to the queue and the game-engine will poll the items from this queue. To remain agile/flexible it is advisable to not
 *   add more then 3 items in your queue (it is not possible to clear the queue or cancel items). If you add items to fast it will
 *   result in a tank that won't respond for a while to new events (because it first will process all earlier submitted actions)
 *
 * Some fields are static (they don't change while running the game or amongst other bots):
 * - actionDurationInMs => this is the time it takes to move forward/backward/turn left/ turn right (shooting is more time consuming)
 *                         this can be used to calculate the time it takes to get somewhere.
 * - distancePerStep => the amount of 'pixels' that a tank moves for one action of type forward/backward
 *                      when the gap between your tank and the walls is smaller then this. it will drive up to the wall (no collision damage)
 *</pre>
 *
 * @author Martijn van de Rijdt
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Tank extends GameObject {
    /** Indicates which direction the tank is facing. This is also the direction it will fire bullets. */
    // TODO use custom Orientation type
    private final Orientation lastKnownOrientation;
    /** The time it takes to move forward/backward/turn left/turn right in milliseconds. Shooting is more time consuming. */
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
     * @param gameBot game object as received from the client api
     */
    public Tank(GameBot gameBot) {
        super(gameBot);
        this.lastKnownOrientation = gameBot.getLastKnownOrientation();
        this.actionDuration = gameBot.getActionDurationInMs();
        this.distancePerStep = gameBot.getDistancePerStep();
        this.player = gameBot.getPlayer();
        this.queueLength = gameBot.getQueueLength();
    }
}
