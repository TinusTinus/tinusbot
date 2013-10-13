package nl.mvdr.devnobot.launcher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import nl.mvdr.devnobot.bot.Tinusbot;
import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.clientapi.ClientApiImpl;

/**
 * Main launcher for the application.
 * 
 * @author Martijn van de Rijdt
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Launcher {
    /**
     * Main method.
     * 
     * Starts a single instance of {@link Tinusbot} on the main thread.
     * 
     * The bot will connect to a server specified using the system property "devnobot.server.baseURL", or to
     * http://localhost:7080 if the system property is not specified.
     * 
     * The bot does not terminate on its own, its process must be killed externally.
     * 
     * @param args
     *            command-line parameters; these are ignored
     */
    public static void main(String[] args) {
        String serverBaseURL = System.getProperty("devnobot.server.baseURL", "http://localhost:7080");
        ClientApi api = new ClientApiImpl(serverBaseURL);
        Tinusbot bot = new Tinusbot(api);
        bot.run();
    }
}
