package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.cgi.devnobot.client.Ibiq;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.clientapi.ClientApiImpl;
import nl.mvdr.devnobot.launcher.Launcher;
import nl.mvdr.devnobot.model.Leaderboard;
import nl.mvdr.devnobot.model.Tank;

/**
 * Main class. Spawns a bunch of bots.
 * 
 * @author Martijn van de Rijdt
 */
public class Tester {
    private static final class TinusbotExperimental extends Tinusbot {
        private final int positions;
        
        private TinusbotExperimental(ClientApi clientApi, String name, Color color, int positions) {
            super(clientApi, name, color);
            this.positions = positions;
        }

        /** {@inheritDoc} */
        @Override
        protected boolean isAThreat(Tank tank, Tank ownTank, Leaderboard leaderboard) {
            boolean result;

            if (leaderboard != null) {
                Integer enemyPosition = leaderboard.retrievePosition(tank.getPlayer());
                result = enemyPosition != null
                        && enemyPosition.intValue() < positions;
            } else {
                // no leaderboards yet; default to false
                result = false;
            }

            return result;
        }
    }

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
        String serverBaseURL = System.getProperty(Launcher.BASE_URL_SYSTEM_PROPERTY, Launcher.DEFAULT_BASE_URL);
        ClientApi api = new ClientApiImpl(serverBaseURL);

        List<Runnable> bots = new ArrayList<>();
        for (int i = 0; i < NUM_DUMMIES; i++) {
            bots.add(new DummyBot(api, DummyBot.DEFAULT_NAME + i, COLOURS[i]));
        }
        
//        bots.add(new Ibiq(serverBaseURL, "Ibiq0"));
//        bots.add(new Ibiq(serverBaseURL, "Ibiq1"));
//        bots.add(new Ibiq(serverBaseURL, "Ibiq2"));
        
        bots.add(new Tinusbot(api));
        bots.add(new TinusbotExperimental(api, "Tinusbot X2", Color.RED, 2));
        bots.add(new TinusbotExperimental(api, "Tinusbot X3", Color.ORANGE, 3));
        bots.add(new TinusbotExperimental(api, "Tinusbot X4", Color.ORANGE, 4));
        
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
