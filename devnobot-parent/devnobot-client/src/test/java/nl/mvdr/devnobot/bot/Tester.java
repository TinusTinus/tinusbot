package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.clientapi.ClientApiImpl;

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
        ClientApi api = new ClientApiImpl(serverBaseURL);

        List<BotArtificialIntelligence> bots = new ArrayList<>();
        for (int i = 0; i < NUM_DUMMIES; i++) {
            bots.add(new DummyBot(api, DummyBot.DEFAULT_NAME + i, Color.ORANGE));
        }
        bots.add(new InteractiveBot(api));

        for (BotArtificialIntelligence bot : bots) {
            new Thread(bot, bot.getName()).start();
        }
    }
}
