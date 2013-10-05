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

import java.util.Collection;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Player;
import nl.mvdr.devnobot.model.Wall;

/** Abstract superclass for bot implementations. */
@Slf4j
@RequiredArgsConstructor
abstract class BotArtificialIntelligence implements Runnable {
    /** Sleep duration after every execution of the main game loop in milliseconds. */
    private static final int THREAD_SLEEP_DURATION = 1000;
    /**
     * The approximate number of milliseconds between logging the leaderboard.
     * 
     * The bot periodically logs the leaderboards but not in every iteration of the game loop.
     */
    private static final int LEADERBOARD_INTERVAL = 5000;

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
        Collection<Wall> walls = this.api.readLevel();

        // Generate pseudorandom id string.
        String id = name + '-' + UUID.randomUUID().toString();

        // Register at the server.
        api.createPlayer(name, color, id);

        // Main game loop.
        gameLoop(walls, id);
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
        long leaderboardTimestamp = 0L;
        while (true) {
            try {
                // Optionally log the leaderboard.
                if (leaderboardTimestamp + LEADERBOARD_INTERVAL < System.currentTimeMillis()) {
                    Player.logLeaderboard(api.readPlayers());
                    leaderboardTimestamp = System.currentTimeMillis();
                }

                // Retrieve a current view of the world
                GameState state = api.readWorldStatus();
                if (state != null) {
                    log.info(state.toString());

                    Action action = determineNextAction(walls, state);

                    perform(id, action);
                } else {
                    log.info("No World information available.");
                }

                // Actions take multiple seconds to perform (see GameBot) and the readWorldStatus is only updated every
                // half a second. Therefore it is best to sleep for a short time.
                // TODO adjust sleep time?
                Thread.sleep(THREAD_SLEEP_DURATION);
            } catch (Exception e) {
                // Log the exception, but don't crash the thread; try to keep going.
                log.error("Unexpected exception!", e);
            }
        }
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
     */
    private void perform(String id, Action action) {
        if (action == Action.SUICIDE) {
            api.suicide(id);
        } else {
            api.addAction(action, id);
        }
    }

    /**
     * Determines the next action to take.
     * 
     * @param obstacles
     *            obstacles
     * @param state
     *            game state
     * @return action to be taken
     */
    protected abstract Action determineNextAction(Collection<Wall> obstacles, GameState state);

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }
}
