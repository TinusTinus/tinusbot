package nl.mvdr.devnobot.clientapi;

import java.util.Collection;

import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Player;
import nl.mvdr.devnobot.model.Wall;

import com.cgi.devnobot.api.World;

/**
 * API for communicating to the game server.
 * 
 * @author Martijn van de Rijdt
 */
public interface ClientApi {

    /**
     * Retrieves all of the obstacles in the level.
     * 
     * There is no need to call this method multiple times, since the obstacles do not change throughout the game. Call
     * this method once at the start of the game.
     * 
     * @return collection of obstacles
     */
    Collection<Wall> readLevel();

    /**
     * Registers a new player for the game.
     * 
     * @param name
     *            name
     * @param color
     *            tank color, as a hex string
     * @param id
     *            unique Id of the player, to prevent others from accidentally stealing your bot
     * @return whether creation was succesful
     */
    boolean createPlayer(String name, String color, String id);

    /**
     * Retrieves a list of players, including their kill/death ratios.
     * 
     * There is no point in calling this more than once a second; the results are cached at the server.
     * 
     * @return players
     */
    abstract Collection<Player> readPlayers();

    /**
     * Add the given {@link Action} for the player with the given id to its queue.
     * 
     * Keep in mind that you should not add actions too fast to prevent creating a large action queue.
     * 
     * @param action
     *            action to be added to the queue
     * @param playerId
     *            player id, as passed in via the {@link #createPlayer(String, String, String)} method call
     * @return whether the action was succesfully added to the queue
     */
    boolean addAction(Action action, String playerId);

    /**
     * Read this in your game loop to see what's going on.
     * 
     * It is not necessary to call this more than twice a second (the results are cached at the server).
     * 
     * @return {@link World}
     */
    GameState readWorldStatus();

    /**
     * Kill your bot.
     * 
     * Useful when you think that you are trapped somewhere.
     * 
     * @param playerId player id
     *            player id, as passed in via the {@link #createPlayer(String, String, String)} method call
     * @return whether your tank was suicided succesfully
     */
    boolean suicide(String playerId);
}