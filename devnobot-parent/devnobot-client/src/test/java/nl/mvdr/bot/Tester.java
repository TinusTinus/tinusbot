package nl.mvdr.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.cgi.devnobot.client.ClientApi;

/**
 * Main class. Spawns a bunch of bots.
 * 
 * @author Martijn van de Rijdt
 */
public class Tester {
    /** Number of dummies. */
    private static final int NUM_DUMMIES = 7;

    /**
     * Main method.
     * 
     * @param args
     *            commandline parameters; unused
     */
    public static void main(final String[] args) {
        String serverBaseURL = System.getProperty("devnobot.server.baseURL", "http://localhost:7080");
        ClientApi api = new ClientApi(serverBaseURL);

        List<Bot> bots = new ArrayList<>();
        for (int i = 0; i < NUM_DUMMIES; i++) {
            bots.add(new DummyBot(api, "Dummy" + i, toHexString(Color.ORANGE)));
        }

        for (Bot bot : bots) {
            new Thread(bot, bot.getName()).start();
        }
    }

    /**
     * Return the given colour as a hex String.
     * 
     * @param color
     *            color
     * @return String representation of the color as accepted by the client API
     */
    public static String toHexString(Color color) {
        return "#" + Integer.toHexString((color.getRGB() & 0xffffff) | 0x1000000).substring(1);
    }
}
