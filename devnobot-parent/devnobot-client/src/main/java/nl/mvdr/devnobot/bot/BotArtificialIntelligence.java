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
package nl.mvdr.devnobot.bot;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.model.Action;

import com.cgi.devnobot.api.GameObstacle;
import com.cgi.devnobot.api.GamePlayer;
import com.cgi.devnobot.api.World;
import com.cgi.devnobot.client.ClientApi;

/** Abstract superclass for bot implementations. */
@Slf4j
@RequiredArgsConstructor
abstract class BotArtificialIntelligence implements Runnable {

    public static final int THREAD_SLEEP_DURATION = 1000;

    /** Client API, used to make backend calls. */
    @NonNull
    private final ClientApi api;
    /** Player name. */
    @NonNull
    @Getter
    private final String name;
    /** Colour. */
    @NonNull
    private final String color;

    /** {@inheritDoc} */
    @Override
    public void run() {
        // First we read the level contents to get a view of the positions of the walls on this map.
        // Maps are static, so we only read this at startup
        List<GameObstacle> obstacles = this.api.readLevel();

        // Generate pseudorandom id string.
        String id = name + '-' + UUID.randomUUID().toString();

        // Register at the server.
        api.createPlayer(name, color, id);

        // Retrieve and log a list of all players.
        List<GamePlayer> players = api.readPlayers();
        for (GamePlayer gamePlayer : players) {
            log.info("Found player: " + gamePlayer.toString());
        }

        // Main game loop.
        while (true) {
            // Retrieve a current view of the world
            World world = api.readWorldStatus();
            if (world != null) {
                log.info(world.toString());

                Action action = determineNextAction(obstacles, world);

                perform(id, action);
            } else {
                log.info("No World information available.");
            }

            // Actions take multiple seconds to perform (see GameBot) and the readWorldStatus is only updated every
            // half a second. Therefore it is best to sleep for a short time.
            // TODO adjust sleep time?
            try {
                Thread.sleep(THREAD_SLEEP_DURATION);
            } catch (InterruptedException iex) {
                log.warn("INTERRUPTED");
            }
        }
    }

    /** 
     * Performs the given action.
     * 
     * Actions are added to the action queue, except for {@link Action#SUICIDE}, which is executed immediately,
     * 
     * @param id player id
     * @param action action to be performed
     */
    private void perform(String id, Action action) {
        if (action == Action.SUICIDE) {
            api.killYourOwnBot(id);
        } else {
            api.addAction(action.getCGIAction(), id);
        }
    }

    /**
     * Determines the next action to take.
     * 
     * @param obstacles
     *            obstacles
     * @param world
     *            game state
     * @return action to be taken
     */
    protected abstract Action determineNextAction(List<GameObstacle> obstacles, World world);
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }
}