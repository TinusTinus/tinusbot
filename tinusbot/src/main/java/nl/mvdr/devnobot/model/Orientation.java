package nl.mvdr.devnobot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representation of the direction a tank is facing.
 * 
 * The CGI orientation uses up / right / down/ left. Here we use north / east / south / west, to avoid confusion with
 * turn directions.
 * 
 * @author Martijn van de Rijdt
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum Orientation {
    /** North (up). */
    NORTH(0, -1),
    /** East (right). */
    EAST(1, 0),
    /** South (down). */
    SOUTH(0, 1),
    /** West (left). */
    WEST(-1, 0);

    /** Multiplier for moving forward along this direction. */
    private final int xMultiplier;
    /** Multiplier for moving forward along this direction. */
    private final int yMultiplier;

    /**
     * Converts a CGI orientation to an instance of this enum.
     * 
     * @param orientation
     *            CGI orientation
     * @return orientation
     */
    public static Orientation fromAPIOrientation(com.cgi.devnobot.api.Orientation orientation) {
        Orientation result;
        if (orientation == com.cgi.devnobot.api.Orientation.UP) {
            result = NORTH;
        } else if (orientation == com.cgi.devnobot.api.Orientation.RIGHT) {
            result = EAST;
        } else if (orientation == com.cgi.devnobot.api.Orientation.DOWN) {
            result = SOUTH;
        } else if (orientation == com.cgi.devnobot.api.Orientation.LEFT) {
            result = WEST;
        } else if (orientation == null) {
            result = null;
        } else {
            throw new IllegalArgumentException("Unexpected orientation: " + orientation);
        }
        return result;
    }

    /**
     * Determines the next orientation when turning right.
     * 
     * @return next orientation
     */
    public Orientation turnRight() {
        Orientation result;
        if (this == NORTH) {
            result = EAST;
        } else if (this == EAST) {
            result = SOUTH;
        } else if (this == SOUTH) {
            result = WEST;
        } else if (this == WEST) {
            result = NORTH;
        } else {
            throw new IllegalStateException("Unexpected orientation: " + this);
        }
        return result;
    }

    /**
     * Determines the next orientation when turning left.
     * 
     * @return next orientation
     */
    public Orientation turnLeft() {
        Orientation result;
        if (this == NORTH) {
            result = WEST;
        } else if (this == EAST) {
            result = NORTH;
        } else if (this == SOUTH) {
            result = EAST;
        } else if (this == WEST) {
            result = SOUTH;
        } else {
            throw new IllegalStateException("Unexpected orientation: " + this);
        }
        return result;
    }

    /**
     * Determines the orientation after executing the given action.
     * 
     * @param action
     *            action
     * @return new orientation
     */
    public Orientation newOrientation(Action action) {
        Orientation result;
        if (action == Action.TURN_RIGHT) {
            result = turnRight();
        } else if (action == Action.TURN_LEFT) {
            result = turnLeft();
        } else {
            // All other actions preserve orientation.
            // (Although it doesn't really matter, this even seems to be true for Action.SUICIDE.)
            result = this;
        }
        return result;
    }
}
