package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.clientapi.ClientApiImpl;
import nl.mvdr.devnobot.launcher.Launcher;
import nl.mvdr.devnobot.model.LeaderboardDisplay;

import com.cgi.devnobot.client.Ibiq;

/**
 * Main class. Spawns a bunch of bots.
 * 
 * @author Martijn van de Rijdt
 */
public class Tester {
    /** Number of dummies. */
    private static final int NUM_DUMMIES = 4;
    /** Colours for dummy bots. There should be at least NUM_DUMMIES colours in here. */
    private static final Color[] COLOURS = new Color[] { Color.MAGENTA, Color.RED, Color.BLACK, Color.GREEN,
            Color.DARK_GRAY, Color.CYAN, Color.YELLOW };

    /**
     * Main method.
     * 
     * @param args
     *            commandline parameters; unused
     */
    public static void main(final String[] args) {
        String serverBaseURL = System.getProperty(Launcher.BASE_URL_SYSTEM_PROPERTY, Launcher.DEFAULT_BASE_URL);
        ClientApi api = new ClientApiImpl(serverBaseURL);

        List<Runnable> bots = new ArrayList<>();

        // dummies
        for (int i = 0; i < NUM_DUMMIES; i++) {
            bots.add(new DummyBot(api, DummyBot.DEFAULT_NAME + i, COLOURS[i]));
        }

        // Ibiqs
        bots.add(new Ibiq(serverBaseURL, "Ibiq0"));
        bots.add(new Ibiq(serverBaseURL, "Ibiq1"));
//        bots.add(new Ibiq(serverBaseURL, "Ibiq2"));

        // Tinusbot
        bots.add(new Tinusbot(api));
        bots.add(new Tinusbot(api, "Tinusbot minKills = 5", Color.RED, 5));

        for (Runnable bot : bots) {
            String name;
            if (bot instanceof BotArtificialIntelligence) {
                name = ((BotArtificialIntelligence) bot).getName();
            } else {
                name = bot.getClass().getSimpleName();
            }
            new Thread(bot, name).start();
        }
        
        new LeaderboardDisplay(api).start();
    }
}
