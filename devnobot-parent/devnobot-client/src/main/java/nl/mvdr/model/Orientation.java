package nl.mvdr.model;

/**
 * Representation of the direction a tank is facing.
 * 
 * The CGI orientation uses up / right / down/ left. Here we use north / east / south / west, to avoid confusion with
 * turn directions.
 * 
 * @author Martijn van de Rijdt
 */
public enum Orientation {
    /** North (up). */
    NORTH,
    /** East (right). */
    EAST,
    /** South (down). */
    SOUTH,
    /** West (left). */
    WEST;

    /**
     * Converts a CGI orientation to an instance of this enum.
     * 
     * @param orientation
     *            CGI orientation
     * @return orientation
     */
    public static Orientation fromCGIOrientation(com.cgi.devnobot.api.Orientation orientation) {
        Orientation result;
        if (orientation == com.cgi.devnobot.api.Orientation.UP) {
            result = NORTH;
        } else if (orientation == com.cgi.devnobot.api.Orientation.RIGHT) {
            result = EAST;
        } else if (orientation == com.cgi.devnobot.api.Orientation.DOWN) {
            result = SOUTH;
        } else if (orientation == com.cgi.devnobot.api.Orientation.LEFT) {
            result = WEST;
        } else {
            throw new IllegalArgumentException("Unexpected orientation: " + orientation);
        }
        return result;
    }
}
