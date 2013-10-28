package nl.mvdr.devnobot.launcher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.bot.Tinusbot;

/**
 * Main launcher for the application.
 * 
 * @author Martijn van de Rijdt
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Launcher {
    /** Name of the system property containing the server base URL. */
    public static final String BASE_URL_SYSTEM_PROPERTY = "devnobot.server.baseURL";
    /** Default value for base URL. */
    public static final String DEFAULT_BASE_URL = "http://localhost:7080";

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
        launch();
    }

    /**
     * Starts a single instance of {@link Tinusbot} on the main thread.
     * 
     * The bot will connect to a server specified using the system property "devnobot.server.baseURL", or to
     * http://localhost:7080 if the system property is not specified.
     * 
     * The bot does not terminate on its own, its thread must be killed externally.
     */
    public static void launch() {
        log.info("Determining Devnobot server base URL. "
                + "Default is \"{}\". Set system property \"{}\" to override this default.", DEFAULT_BASE_URL,
                BASE_URL_SYSTEM_PROPERTY);
        String serverBaseURL = System.getProperty(BASE_URL_SYSTEM_PROPERTY, DEFAULT_BASE_URL);
        log.info("Base URL: " + serverBaseURL);
        Tinusbot bot = new Tinusbot(serverBaseURL, Tinusbot.retrieveDefaultName(), "#FFC800");
        log.info("Starting bot {} on its own thread!", bot.getName());
        new Thread(bot, bot.getName()).start();
    }
}
