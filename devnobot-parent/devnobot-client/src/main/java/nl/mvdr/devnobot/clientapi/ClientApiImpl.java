package nl.mvdr.devnobot.clientapi;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Player;
import nl.mvdr.devnobot.model.Wall;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.cgi.devnobot.api.GameObstacle;
import com.cgi.devnobot.api.GamePlayer;
import com.cgi.devnobot.api.World;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/** Implements the REST/JSON Api of the server. Based on the contest's example implementation. */
@Slf4j
@RequiredArgsConstructor
public class ClientApiImpl implements ClientApi {
    /** Base URL. */
    @NonNull
    private final String baseURL;

    /** {@inheritDoc} */
    @Override
    public Collection<Wall> readLevel() {
        // Call REST service
        if (log.isDebugEnabled()) {
            log.debug("Making a REST call to read the level");
        }
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/level");
        Response response = resteasyWebTarget.request().get();
        String levelAsString = response.readEntity(String.class);
        response.close();
        resteasyClient.close();

        // Convert result from JSON to API object
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<GameObstacle>>() {
            // anonymous inner class to create type token
        }.getType();
        List<GameObstacle> obstacles = gson.fromJson(levelAsString, collectionType);

        // Convert to data model
        List<Wall> result = new ArrayList<>(obstacles.size());
        for (GameObstacle obstacle : obstacles) {
            result.add(new Wall(obstacle));
        }

        return Collections.unmodifiableCollection(result);
    }

    /** {@inheritDoc} */
    @Override
    public boolean createPlayer(String name, Color color, String id) {
        if (log.isDebugEnabled()) {
            log.debug("Making a REST call to create a player with name = {}, color = {}, id = {}", 
                    new Object[] { name, color, id });
        }
        GamePlayer player = new GamePlayer();
        player.setColor(toHexString(color));
        player.setName(name);
        player.setId(id);

        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/player");
        Response response = resteasyWebTarget.request().post(Entity.entity(player, MediaType.APPLICATION_JSON));
        boolean result = (response.getStatus() == HttpStatus.SC_NO_CONTENT);
        response.close();
        resteasyClient.close();

        return result;
    }
    
    /**
     * Return the colour as a hex String.
     * 
     * @param color color to be converted
     * 
     * @return String representation of the color as accepted by the client API
     */
    private String toHexString(Color color) {
        return "#" + Integer.toHexString((color.getRGB() & 0xffffff) | 0x1000000).substring(1);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Player> readPlayers() {
        // Call REST service
        if (log.isDebugEnabled()) {
            log.debug("Making a REST call to read players");
        }
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/players");
        Response response = resteasyWebTarget.request().get();
        String playersAsString = response.readEntity(String.class);
        response.close();
        resteasyClient.close();
        
        // Convert from JSON to API objects
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<GamePlayer>>() {
            // anonymous inner class to create type token
        }.getType();
        List<GamePlayer> players = gson.fromJson(playersAsString, collectionType);
        
        // Convert to model
        Collection<Player> result = new ArrayList<>(players.size());
        for (GamePlayer player: players) {
            result.add(new Player(player));
        }

        return Collections.unmodifiableCollection(result);
    }

    /** {@inheritDoc} */
    @Override
    public boolean addAction(final Action action, final String playerId) {
        if (log.isDebugEnabled()) {
            log.debug("Making a REST call to add action " + action + " to player " + playerId);
        }
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/player/" + playerId);
        Response response = resteasyWebTarget.request().put(Entity.entity(action.toAPIAction(), MediaType.APPLICATION_JSON));
        boolean result = (response.getStatus() == HttpStatus.SC_NO_CONTENT);
        response.close();
        resteasyClient.close();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public GameState readWorldStatus() {
        // Call REST service
        if (log.isDebugEnabled()) {
            log.debug("Making a REST call to read the world status");
        }
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/world");
        Response response = resteasyWebTarget.request().get();
        String worldStatusAsString = response.readEntity(String.class);
        response.close();
        resteasyClient.close();
        
        // Convert from JSON to API object
        Gson gson = new Gson();
        World world = gson.fromJson(worldStatusAsString, World.class);
        
        // Convert to data model
        GameState result;
        if (world != null) {
            result = new GameState(world);
        } else {
            result = null;
        }
        
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean suicide(String playerId) {
        if (log.isDebugEnabled()) {
            log.debug("Making a REST call to suicide tank with player id " + playerId);
        }
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/player/" + playerId);
        Response response = resteasyWebTarget.request().delete();
        resteasyClient.close();

        return response.getStatus() == HttpStatus.SC_NO_CONTENT;
    }
}
