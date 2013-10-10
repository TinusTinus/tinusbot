package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.cgi.devnobot.client.Ibiq;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.clientapi.ClientApiImpl;

/**
 * Main class. Spawns a bunch of bots.
 * 
 * @author Martijn van de Rijdt
 */
public class Tester {
    /** Number of dummies. */
    private static final int NUM_DUMMIES = 4;
    /** Colours for dummy bots. There should be at least NUM_DUMMIES colours in here. */
    private static final Color[] COLOURS = new Color[] { Color.MAGENTA, Color.RED, Color.BLACK, Color.ORANGE,
            Color.DARK_GRAY, Color.CYAN, Color.ORANGE };

    /**
     * Main method.
     * 
     * @param args
     *            commandline parameters; unused
     */
    public static void main(final String[] args) {
        String serverBaseURL = System.getProperty("devnobot.server.baseURL", "http://localhost:7080");
        ClientApi api = new ClientApiImpl(serverBaseURL);

        List<Runnable> bots = new ArrayList<>();
        for (int i = 0; i < NUM_DUMMIES; i++) {
            bots.add(new DummyBot(api, DummyBot.DEFAULT_NAME + i, COLOURS[i]));
        }
        bots.add(new InteractiveBot(api));
        bots.add(new RotatingTurret(api));
        bots.add(new Ibiq(serverBaseURL));
        bots.add(new Tinusbot(api));
        
        for (Runnable bot : bots) {
        	String name;
        	if (bot instanceof BotArtificialIntelligence) {
        		name = ((BotArtificialIntelligence)bot).getName();
        	} else {
        		name = bot.getClass().getSimpleName();
        	}
            new Thread(bot, name).start();
        }
    }
}
