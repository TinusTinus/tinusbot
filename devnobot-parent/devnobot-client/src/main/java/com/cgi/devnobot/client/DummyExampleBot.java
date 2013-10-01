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
import com.cgi.devnobot.api.GameBot;
import com.cgi.devnobot.api.GameObstacle;
import com.cgi.devnobot.api.GamePlayer;
import com.cgi.devnobot.api.World;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * DummyExampleBot.
 *
 * This bot does not contain any useful logic (it does random actions) but does illustrate the API usage. It should
 * be used as a guide of how to use the API to implement a bot. The easiest way to implement your own bot is
 * to copy this one and add some clever algorithm/tactics in the game loop.
 *
 *
 */
public class DummyExampleBot implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(DummyExampleBot.class.getName());
    public static final int THREAD_SLEEP_DURATION = 1000;
    private final ClientApi api;
    private final String name;
    private final String color;
    private final Random randomGenerator = new Random();

    /**
     * Specialized constructor.
     *
     * @param host  -
     * @param name  -
     * @param color -
     */
    public DummyExampleBot(final String host, final String name, final String color) {

        this.api = new ClientApi(host);
        this.name = name;
        this.color = color;
        LOGGER.info("Name: " + name + " Colour: " + color + " (" + Color.decode(color).toString() + ")");
    }

    @Override
    public void run() {
    	/**
    	 * first we read the level contents to get a view of the positions of the walls on this map.
    	 * Maps are static, so we only read this at startup
    	 */
        List<GameObstacle> obstacles = api.readLevel();
        for (GameObstacle gameObstacle: obstacles) {
            LOGGER.info("Found obstacle :" + gameObstacle.toString() );
        }

        /**
         * Then we register ourselves at the server.
         * Preferably we use a unique nickname and color to identify our selves.
         * The id is used to prevent others to 'accidentally' send actions on your behalf, it is best practice
         * to fill it with a password-like or random value.
         */
        String id = name;
        api.createPlayer(name, color, id);

        /**
         * We read a list of currently joined players, we might also do this every once in a while in the game loop
         */
        List<GamePlayer> players = api.readPlayers();
        for (GamePlayer gamePlayer: players) {
            LOGGER.info("Found player :" + gamePlayer.toString());
        }

        /** GAME LOOP **/
        while (true) {

        	/** we gather a current view of the world **/
            World world = api.readWorldStatus();
            if (world != null) {
                for(GameBot bot: world.getBots()){
                    LOGGER.info(bot.getPlayer()+" "+bot.getLastKnownOrientation());
                }
            } else {
                LOGGER.info("No World information available");
            }

            /**
             * <INSERT SMART ALGORITHM HERE>
             *
             * in this case we just use a random move
             */

            Action action = Action.values()[randomGenerator.nextInt(Action.values().length)];
            /**
             * </INSERT SMART ALGORITHM HERE>
             */

            /**
             * Add action to the tank-action-queue on the server.
             * Actions are polled from the queue, and empty queue means it will be processed directly.
             * When actions are added faster then they are processed, this will create a 'unresponsive tank'
             * Therefore we let the game loop sleep for about (half) a second.
             *
             */
            api.addAction(action, id);

            /**
             * Actions take multiple seconds to perform {@Link GameBot} and the readWorldStatus is only update every half a second.
             * Therefore it is best to sleep for a short time.
             */
            try {
                Thread.sleep(THREAD_SLEEP_DURATION);
            } catch (InterruptedException iex) {
                LOGGER.warning("INTERRUPTED");
            }
        }
    }

}
