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

import java.awt.Color;
import java.util.logging.Logger;

/**
 * This class fires up some dummy bots.
 * It could be changed to also start your own bot and host your own local match.
 *
 * The real match will contain 8 bots, 4 of the dummy type, 4 made by participants of the JFall.
 */
public final class Tester {

    private static final Logger LOG = Logger.getLogger(Tester.class.getName());
    private static final Color[] COLOURS = new Color[]{Color.MAGENTA, Color.RED, Color.BLACK, Color.ORANGE, Color.DARK_GRAY, Color.CYAN, Color.ORANGE};

    private static final String USAGE = "Tester [-Ddevnobot.server.baseURL=<baseURL>]";
    
    
    /**
     * Only static methods here, so private constructor.
     */
    private Tester() {

    }

    /**
     * Main class, usage: {@value #USAGE}.
     *
     * @param args Not used.
     */
    public static void main(final String[] args) {

        String serverBaseURL = System.getProperty("devnobot.server.baseURL", "http://localhost:7080");

        if (serverBaseURL == null) {
            LOG.severe("Usage: " + USAGE);
        } else {
            for (int i = 0; i < 7; i++) {
                new Thread(new DummyExampleBot(serverBaseURL, "Dummy"+i, colour(i))).start();
            }
        }
    }

    /**
     * Return the given colour as a String.
     *
     * @param index -
     * @return String
     */
    public static String colour(final int index) {
        return "#" + Integer.toHexString((COLOURS[index].getRGB() & 0xffffff) | 0x1000000).substring(1);
    }
}
