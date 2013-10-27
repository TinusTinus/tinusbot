package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.util.Collection;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.clientapi.ClientApiImpl;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Leaderboard;
import nl.mvdr.devnobot.model.Player;
import nl.mvdr.devnobot.model.Wall;

/** Abstract superclass for bot implementations. Based on the contest's example implementation. */
@Slf4j
@RequiredArgsConstructor
abstract class BotArtificialIntelligence implements Runnable {
    /** Default value for sleep duration. */
    private static final int DEFAULT_THREAD_SLEEP_DURATION = 550; // determined experimentally
    /**
     * The approximate number of milliseconds between logging the leaderboard.
     * 
     * The bot periodically logs the leaderboards but not in every iteration of the game loop.
     */
    private static final int LEADERBOARD_INTERVAL = 1000;

    /** Maximum number of failed actions in a row. */
    private static final int MAX_FAILED_ACTIONS = 10;

    /** Client API, used to make backend calls. */
    @NonNull
    private final ClientApi api;
    /** Player name. */
    @NonNull
    @Getter
    private final String name;
    /** Tank colour as a hex string. */
    @NonNull
    private final String color;
    /**
     * Sleep duration between executions of the main game loop in milliseconds. Always accessed through its getter so
     * subclasses can override it dynamically.
     */
    private final int threadSleepDuration;

    /** Timestamp when the bot started running. */
    private long startTime;

    
    /**
     * Return the colour as a hex String.
     * 
     * @param color color to be converted
     * 
     * @return String representation of the color as accepted by the client API
     */
    private static String toHexString(Color color) {
        return "#" + Integer.toHexString((color.getRGB() & 0xffffff) | 0x1000000).substring(1);
    }
    
    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API, used to make backend calls
     * @param name
     *            player name
     * @param color
     *            tank colour as a hex string
     */
    private BotArtificialIntelligence(ClientApi clientApi, String name, String color) {
        this(clientApi, name, color, DEFAULT_THREAD_SLEEP_DURATION);
    }
    
    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API, used to make backend calls
     * @param name
     *            player name
     * @param color
     *            tank colour as a hex string
     */
    public BotArtificialIntelligence(ClientApi clientApi, String name, Color color, int threadSleepDuration) {
        this(clientApi, name, toHexString(color), threadSleepDuration);
    }
    
    /**
     * Constructor.
     * 
     * @param clientApi
     *            client API, used to make backend calls
     * @param name
     *            player name
     * @param color
     *            tank colour
     */
    public BotArtificialIntelligence(ClientApi clientApi, String name, Color color) {
        this(clientApi, name, toHexString(color), DEFAULT_THREAD_SLEEP_DURATION);
    }

    /**
     * Constructor.
     * 
     * @param host
     *            host name / base URL for the game server
     * @param name
     *            player name
     * @param color
     *            tank color
     */
    public BotArtificialIntelligence(String host, String name, Color color) {
        this(new ClientApiImpl(host), name, toHexString(color));
    }

    /**
     * Constructor.
     * 
     * @param host
     *            host name / base URL for the game server
     * @param name
     *            player name
     * @param color
     *            tank color
     */
    public BotArtificialIntelligence(String host, String name, String color) {
        this(new ClientApiImpl(host), name, color);
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        // First we read the level contents to get a view of the positions of the walls on this map.
        // Maps are static, so we only read this once, at startup.
        Collection<Wall> walls = readLevel();
        // Connect to the server and create our bot.
        String id = generateId();
        connect(id);
        // Start the actual processing.
        gameLoop(walls, id);
    }

    /**
     * Reads the level.
     * 
     * @return level
     */
    private Collection<Wall> readLevel() {
        Collection<Wall> result = null;

        while (result == null) {
            try {
                log.info("Reading level");
                result = this.api.readLevel();
            } catch (Exception e) {
                log.error("Failed to read level.", e);
                sleep(threadSleepDuration);
            }
        }

        return result;
    }

    /**
     * Generates a pseudorandom id string.
     * 
     * @return id
     */
    private String generateId() {
        return name + '-' + UUID.randomUUID().toString();
    }

    /**
     * Connects to the server.
     * 
     * @param id
     *            id to be used
     */
    private void connect(String id) {
        boolean success = false;
        while (!success) {
            try {
                log.info("Connecting for player {}, color: {}, id: {}", new Object[] { name, color, id });
                success = api.createPlayer(name, color, id);
            } catch (Exception e) {
                log.error("Failed to connect.", e);
                sleep(threadSleepDuration);
            }
        }
    }

    /**
     * The main game loop.
     * 
     * @param walls
     *            obstacles in the level
     * @param id
     *            current player's id
     */
    private void gameLoop(Collection<Wall> walls, String id) {
        Leaderboard leaderboard = createLeaderboard(null);
        // number of failed actions in a row
        int failedActionCount = 0;
        while (true) {
            // timestamp at the start of this iteration
            long startTimestamp = System.currentTimeMillis();
            // timestamp when the next iteration should take place
            long nextTimestamp = startTimestamp + threadSleepDuration;

            try {
                if (MAX_FAILED_ACTIONS <= failedActionCount) {
                    log.warn("Attempting to reconnect after {} failed actions.", "" + failedActionCount);
                    connect(id);
                    failedActionCount = 0;
                }

                // Retrieve a current view of the world
                GameState state = api.readWorldStatus();

                if (state != null) {
                    if (log.isDebugEnabled()) {
                        log.debug(state.toString());
                    }

                    // Determine what to do!
                    Action action;
                    if (state.retrieveTankForPlayerName(name).getQueueLength() < 1) {
                        action = determineNextAction(walls, state, leaderboard);
                    } else {
                        // Already enqueued actions, no point in determining a new one.
                        // Do nothing.
                        action = null;
                    }
                    boolean success = perform(id, action);

                    if (success) {
                        failedActionCount = 0;
                    } else {
                        failedActionCount++;
                        log.warn("Action failed: {}, number of failures in a row: {}", action, "" + failedActionCount);
                        // retry immediately
                        nextTimestamp = System.currentTimeMillis();
                    }
                } else {
                    log.info("No World information available.");
                    // retry immediately
                    nextTimestamp = System.currentTimeMillis();
                }

                leaderboard = refreshLeaderboard(leaderboard, nextTimestamp);
            } catch (Exception e) {
                failedActionCount++;
                // Log the exception, but don't crash the thread; try to keep going.
                log.error("Unexpected exception!", e);
            }

            sleep(nextTimestamp - System.currentTimeMillis());
        }
    }

    /**
     * Sleeps for the given amount of time, if positive. Otherwise this method does nothing.
     * 
     * @param sleepTime
     *            wait time in milliseconds
     */
    private void sleep(long sleepTime) {
        if (0 < sleepTime) {
            if (log.isDebugEnabled()) {
                log.debug("Sleeping for {} milliseconds.", "" + sleepTime);
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.error("Unexpected InterruptedException; program will continue.", e);
            }
        }
    }

    /**
     * Optionally creates a new leaderboard and logs it.
     * 
     * There is no need to refresh the leaderboard every single iteration of the game loop, since the server doesn't
     * update it more than once per second. This method only does so if there is time to, and if it has been a while
     * since the last time the leaderboard has been refreshed.
     * 
     * @param previousLeaderboard
     *            previous leaderboard; may be null
     * @param nextTimestamp
     *            when the next action is supposed to be taken in the game; if this is too soon, logging the leaderboard
     *            is skipped
     * @return last time the leaderboard was logged; updated if necessary
     */
    private Leaderboard refreshLeaderboard(Leaderboard previousLeaderboard, long nextTimestamp) {
        Leaderboard result;
        long now = System.currentTimeMillis();
        if (now < nextTimestamp
                && (previousLeaderboard == null || previousLeaderboard.getCreationTime() + LEADERBOARD_INTERVAL < now)) {
            result = createLeaderboard(previousLeaderboard);
        } else {
            result = previousLeaderboard;
        }
        return result;
    }

    /**
     * Creates and logs the leaderboard. Any exceptions are caught and logged.
     * 
     * @param previousLeaderboard
     *            previous version of the leaderboard; null if there is none
     * @return new leaderboard, or the value of previousLeaderboard if anything went wrong
     */
    private Leaderboard createLeaderboard(Leaderboard previousLeaderboard) {
        Leaderboard result;
        try {
            Collection<Player> players = api.readPlayers();
            long now = System.currentTimeMillis();
            result = new Leaderboard(now, players);
            logTimePassed();
            log.info(result.toString());
        } catch (Exception e) {
            // Whatever, logging the leaderboard is not very important.
            log.info("Creating and logging the leaderboard failed.", e);
            result = previousLeaderboard;
        }
        return result;
    }

    /** Logs the amount of time that has passed since the bot was started. */
    private void logTimePassed() {
        long millisecondsPassed = System.currentTimeMillis() - this.startTime;
        long secondsPassed = millisecondsPassed / 1000;
        long minutesPassed = secondsPassed / 60;
        long remainder = secondsPassed % 60;
        log.info("Time passed: {} minutes {} seconds", "" + minutesPassed, "" + remainder);
    }

    /**
     * Performs the given action.
     * 
     * Actions are added to the action queue, except for {@link Action#SUICIDE}, which is executed immediately,
     * 
     * @param id
     *            player id
     * @param action
     *            action to be performed
     * @param whether
     *            the operation was succesful
     */
    private boolean perform(String id, Action action) {
        boolean result;
        log.info("Performing action: " + action);
        if (action == Action.SUICIDE) {
            result = api.suicide(id);
        } else if (action != null) {
            result = api.addAction(action, id);
        } else {
            // do nothing
            result = true;
        }
        return result;
    }

    /**
     * Determines the next action to take.
     * 
     * @param obstacles
     *            obstacles
     * @param state
     *            game state
     * @param leaderboard
     *            current leaderboard; may be null
     * @return action to be taken, or null for no action at all
     */
    protected abstract Action determineNextAction(Collection<Wall> obstacles, GameState state, Leaderboard leaderboard);

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }
}
