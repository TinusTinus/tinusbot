package nl.mvdr.devnobot.model;

/**
 * Possible actions which a robot can take in the game.
 * 
 * @author Martijn van de Rijdt
 */
public enum Action {
    /** Moves the bot forward. */
    FORWARD,
    /** Moves the bot backward. */
    BACKWARD,
    /** Turns the bot clockwise. */
    TURN_RIGHT,
    /** Turns the bot counterclockwise. */
    TURN_LEFT,
    /** Fires a bullet. */
    FIRE,
    /** Destroys the bot, causing it to respawn in a random location. */
    SUICIDE;

    /**
     * Returns the corresponding {@link com.cgi.devnobot.api.Action}.
     * 
     * Not a valid operation for the SUICIDEvalue, since there is no corresponding {@link com.cgi.devnobot.api.Action}.
     * 
     * @return corresponding {@link com.cgi.devnobot.api.Action} value.
     * 
     * @throws UnsupportedOperationException
     *             if invoked on SUICIDE
     */
    public com.cgi.devnobot.api.Action toCGIAction() {
        com.cgi.devnobot.api.Action result;
        if (this == FORWARD) {
            result = com.cgi.devnobot.api.Action.FORWARD;
        } else if (this == BACKWARD) {
            result = com.cgi.devnobot.api.Action.BACKWARD;
        } else if (this == TURN_RIGHT) {
            result = com.cgi.devnobot.api.Action.TURN_RIGHT;
        } else if (this == TURN_LEFT) {
            result = com.cgi.devnobot.api.Action.TURN_LEFT;
        } else if (this == FIRE) {
            result = com.cgi.devnobot.api.Action.FIRE;
        } else if (this == SUICIDE) {
            throw new UnsupportedOperationException("No corresponding com.cgi.devnobot.api.Action for SUICIDE");
        } else {
            throw new IllegalArgumentException("Unexpected Action: " + this);
        }
        return result;
    }
}
