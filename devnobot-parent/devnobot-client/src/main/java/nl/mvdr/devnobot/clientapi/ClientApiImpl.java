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
package nl.mvdr.devnobot.clientapi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    private final String baseURL;

    /** {@inheritDoc} */
    @Override
    public Collection<Wall> readLevel() {
        // Call REST service
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
    public boolean createPlayer(String name, String webColor, String id) {
        GamePlayer player = new GamePlayer();
        player.setColor(webColor);
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

    /** {@inheritDoc} */
    @Override
    public Collection<Player> readPlayers() {
        // Call REST service
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
        log.info("Making a REST call to add action " + action + " to player " + playerId);
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/player/" + playerId);
        Response response = resteasyWebTarget.request().put(Entity.entity(action.toCGIAction(), MediaType.APPLICATION_JSON));
        boolean result = (response.getStatus() == HttpStatus.SC_NO_CONTENT);
        response.close();
        resteasyClient.close();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public GameState readWorldStatus() {
        // Call REST service
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
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(this.baseURL + "/devnobot/rest/player/" + playerId);
        Response response = resteasyWebTarget.request().delete();
        resteasyClient.close();

        return response.getStatus() == HttpStatus.SC_NO_CONTENT;
    }
}
