/**
 * Copyright 2011 AJG van Schie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.cgi.devnobot.client;

import com.cgi.devnobot.api.Action;
import com.cgi.devnobot.api.GameObstacle;
import com.cgi.devnobot.api.GamePlayer;
import com.cgi.devnobot.api.World;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implements the REST/JSON Api of the server
 */
public class ClientApi {

    public static final int HTTP_NO_CONTENT = 204;
    private static final Logger LOGGER = Logger.getLogger(ClientApi.class.getName());
    private final String baseURL;

    /**
     * Specialized constructor.
     *
     * @param newBaseURL -
     */
    public ClientApi(String newBaseURL) {

        super();
        this.baseURL = newBaseURL;
    }

    /**
     * Read this once for each level at startup of the game
     *
     * @return List<GameObstacle>
     */
    public List<GameObstacle> readLevel() {

        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/level");
        Response response = resteasyWebTarget.request().get();
        String levelAsString = response.readEntity(String.class);
        response.close();
        resteasyClient.close();
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<GameObstacle>>() {
        }.getType();

        return gson.fromJson(levelAsString, collectionType);
    }

    /**
     * Request the game engine to create a new {@link GamePlayer} object.
     *
     * @param name     -
     * @param webColor - Web Hex Format
     * @param id       - unique Id of the player,to prevent others from accidentally stealing your bot
     * @return boolean (success)
     */
    public boolean createPlayer(String name, String webColor, String id) {

        boolean result = true;

        GamePlayer player = new GamePlayer();
        player.setColor(webColor);
        player.setName(name);
        player.setId(id);

        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/player");
        Response response = resteasyWebTarget.request().post(Entity.entity(player, MediaType.APPLICATION_JSON));
        result = (response.getStatus() == HTTP_NO_CONTENT);
        response.close();
        resteasyClient.close();

        return result;
    }

    /**
     * Read this every now and then if you want to know the player status updates.
     * <p/>
     * Don't call this more then once a second (the results are cached at the server)
     *
     * @return List<GamePlayer>
     */
    public List<GamePlayer> readPlayers() {

        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/players");
        Response response = resteasyWebTarget.request().get();
        String playersAsString = response.readEntity(String.class);
        response.close();
        resteasyClient.close();
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<GamePlayer>>() {
        }.getType();
        List<GamePlayer> players = gson.fromJson(playersAsString, collectionType);

        return players;
    }

    /**
     * Add the given {@link Action} for {@link GamePlayer} with the given id to its queue.
     * Keep in mind that you should not add actions too fast to prevent creating a large action queue
     * {@link com.cgi.devnobot.api.GameBot}.
     *
     * @param action   -
     * @param playerId -
     * @return (success)
     */
    public boolean addAction(final Action action, final String playerId) {

        boolean result = true;
        LOGGER.info("Making a REST call to add action " + action.toString() + " to player " + playerId);
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/player/" + playerId);
        Response response = resteasyWebTarget.request().put(Entity.entity(action, MediaType.APPLICATION_JSON));
        result = (response.getStatus() == HTTP_NO_CONTENT);
        response.close();
        resteasyClient.close();

        return result;
    }

    /**
     * Read this in your game loop to see what's going on.
     * <p/>
     * Don't call this more then twice a second (the results are cached at the server)
     *
     * @return {@link World}
     */
    public World readWorldStatus() {

        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/world");
        Response response = resteasyWebTarget.request().get();
        String worldStatusAsString = response.readEntity(String.class);
        response.close();
        resteasyClient.close();
        Gson gson = new Gson();
        World world = gson.fromJson(worldStatusAsString, World.class);

        return world;
    }

    /**
     * Kill your bot. Useful when you think that you are trapped somewhere.
     *
     * @param playerId -
     * @return boolean (success)
     */
    public boolean killYourOwnBot(String playerId) {

        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/player/" + playerId);
        Response response = resteasyWebTarget.request().delete();
        resteasyClient.close();

        return response.getStatus() == HTTP_NO_CONTENT;
    }
}
