package com.cgi.devnobot.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cgi.devnobot.api.Action;
import com.cgi.devnobot.api.GameBot;
import com.cgi.devnobot.api.GameObstacle;
import com.cgi.devnobot.api.GamePlayer;
import com.cgi.devnobot.api.Orientation;
import com.cgi.devnobot.api.World;

/**
 * Very first step: fire! Then wait a bit to make sure the World is updated by
 * the server.
 */
public class Ibiq implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Ibiq.class.getName());
    private static final String NAME = "Ibiq";
    private static final String COLOR = "#BF00FF";
    private static final String ID = UUID.randomUUID().toString();
    private static final long MAX_WAIT = 500;
    private static final Action[] actions = new Action[] {Action.FORWARD, Action.FORWARD, Action.FORWARD, Action.FORWARD
        ,Action.BACKWARD,Action.BACKWARD,Action.BACKWARD,Action.BACKWARD,
        Action.TURN_LEFT, Action.TURN_RIGHT};

    private final ClientApi api;
    private List<GameObstacle> obstacles;
    private String name;
    private String id;

    public Ibiq(final String host, String name) {
        this.api = new ClientApi(host);
        this.name = name;
        this.id = name + "_" + ID;
    }

    public Ibiq(final String host) {
        this.api = new ClientApi(host);
        this.name = NAME;
        this.id = ID;
    }

    @Override
    public void run() {

        // Register myself to the war.
        registerPlayer();

        // First fire a bullet. Who knows what may happen?
        fire();

        // Then wait a bit to make sure the World is updated by the server for
        // the first time.
        // That is my theory at least.
        sleep(System.currentTimeMillis());

        // Get the level.
        readLevel();
        
        int scoreBoardCounter = 0;

        // Battlefield loop.
        while (true) {
            
            // Begin of game loop.
            long beginTime = System.currentTimeMillis();
            
            // The try-catch will prevent NPE's in 'world' and 'me'.
            try {

                // Plays one step in the game.
                play();
            } catch (NullPointerException ex) {
                LOGGER.log(Level.SEVERE, "NullPointerException found.", ex);
            }
            
            if (scoreBoardCounter++ >= 20) {
                logScoreBoard();
                scoreBoardCounter = 0;
            }

            // Wait a little while before next step.
            sleep(beginTime);
        }

    }

    private void logScoreBoard() {
        List<GamePlayer> players = api.readPlayers();
        Collections.sort(players, new Comparator<GamePlayer>(){

            @Override
            public int compare(GamePlayer one, GamePlayer two) {
                Integer scoreOne = 2 * one.getKills() - one.getDeads();
                Integer scoreTwo = 2 * two.getKills() - two.getDeads();
                return scoreTwo.compareTo(scoreOne);
            }

            });
        StringBuilder sb = new StringBuilder("----=[ SCORE BOARD ]=----\n");
        for (GamePlayer player : players) {
            sb.append(player.getName() + " " + player.getKills() + "/" + player.getDeads() + ": " + (2 * player.getKills() - player.getDeads()) + "\n");
        }
        sb.append("-------------------------");
        LOGGER.info(sb.toString());
    }

    private void play() {
        // Get the status of the battlefield.
        World world = readWorld();

        // My bot
        GameBot me = getMyBot(world);

        int queueLength = me.getQueueLength();
        LOGGER.info("me.getQueueLength() = " + queueLength);

        // Do not play when the queue is not empty.
        // This will keep my tank from not responding.
        if (queueLength > 0) {
            return;
        }
        
        // TODO Construct an better image of my world.

        // Shoot when a tank is in sight.
        if (hasLineOfSight(world, me)) {
            fire();
            return;
        }

        // Turn and shoot if applicable.
        if (hasLineOfSightWhenTurned(world, me, Action.TURN_LEFT)) {
            turnLeft();
            fire();
            return;
        }
        if (hasLineOfSightWhenTurned(world, me, Action.TURN_RIGHT)) {
            turnRight();
            fire();
            return;
        }
        if (hasLineOfSightWhenTurnedAround(world, me)) {
            turnLeft();
            turnLeft();
            fire();
            return;
        }
        
        // TODO Random step
        doAction(actions[(int) (Math.random() * actions.length)]);
    }

    private void doAction(Action action) {
        api.addAction(action, id);
    }

    private boolean hasLineOfSightWhenTurnedAround(World world, GameBot me) {
        Orientation orientation = null;
        if (me.getLastKnownOrientation().equals(Orientation.UP)) {
            orientation = Orientation.DOWN;
        }
        if (me.getLastKnownOrientation().equals(Orientation.RIGHT)) {
            orientation = Orientation.LEFT;
        }
        if (me.getLastKnownOrientation().equals(Orientation.DOWN)) {
            orientation = Orientation.UP;
        }
        if (me.getLastKnownOrientation().equals(Orientation.LEFT)) {
            orientation = Orientation.RIGHT;
        }
        for (GameBot other : world.getBots()) {
            if (!other.getPlayer().equals(me.getPlayer())) {
                if (hasLineOfSight(me, other, orientation)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasLineOfSightWhenTurned(World world, GameBot me, Action direction) {
        Orientation orientation = null;
        if (me.getLastKnownOrientation().equals(Orientation.UP)) {
            if (direction.equals(Action.TURN_LEFT)) {
                orientation = Orientation.LEFT;
            }
            if (direction.equals(Action.TURN_RIGHT)) {
                orientation = Orientation.RIGHT;
            }
        }
        if (me.getLastKnownOrientation().equals(Orientation.LEFT)) {
            if (direction.equals(Action.TURN_LEFT)) {
                orientation = Orientation.DOWN;
            }
            if (direction.equals(Action.TURN_RIGHT)) {
                orientation = Orientation.UP;
            }
        }
        if (me.getLastKnownOrientation().equals(Orientation.DOWN)) {
            if (direction.equals(Action.TURN_LEFT)) {
                orientation = Orientation.RIGHT;
            }
            if (direction.equals(Action.TURN_RIGHT)) {
                orientation = Orientation.LEFT;
            }
        }
        if (me.getLastKnownOrientation().equals(Orientation.RIGHT)) {
            if (direction.equals(Action.TURN_LEFT)) {
                orientation = Orientation.UP;
            }
            if (direction.equals(Action.TURN_RIGHT)) {
                orientation = Orientation.DOWN;
            }
        }
        for (GameBot other : world.getBots()) {
            if (!other.getPlayer().equals(me.getPlayer())) {
                if (hasLineOfSight(me, other, orientation)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Does 'me' has a line of sight with any other bots?
     */
    private boolean hasLineOfSight(World world, GameBot me) {
        for (GameBot other : world.getBots()) {
            if (!other.getPlayer().equals(me.getPlayer())) {
                if (hasLineOfSight(me, other, null)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Does 'me' has a line of sight to 'other'?
     * Orientation is optional.
     */
    private boolean hasLineOfSight(GameBot me, GameBot other, Orientation orientation) {
        Orientation myOrientation = orientation != null ? orientation : me.getLastKnownOrientation();
        int myCenterX = me.getX() + me.getWidth() / 2;
        int myCenterY = me.getY() + me.getHeight() / 2;
        if (Orientation.UP.equals(myOrientation)) {
            for (int y = me.getY(); y >= 0; y -= 4) {
                if (hitWall(myCenterX, y)) {
                    return false;
                }
                if (hitBot(other, myCenterX, y)) {
                    return true;
                }
            }
        }
        if (Orientation.RIGHT.equals(myOrientation)) {
            for (int x = me.getX(); x < 1_000_000; x += 4) {
                if (hitWall(x, myCenterY)) {
                    return false;
                }
                if (hitBot(other, x, myCenterY)) {
                    return true;
                }
            }
        }
        if (Orientation.DOWN.equals(myOrientation)) {
            for (int y = me.getY(); y < 1_000_000; y += 4) {
                if (hitWall(myCenterX, y)) {
                    return false;
                }
                if (hitBot(other, myCenterX, y)) {
                    return true;
                }
            }
        }
        if (Orientation.LEFT.equals(myOrientation)) {
            for (int x = me.getX(); x >= 0; x -= 4) {
                if (hitWall(x, myCenterY)) {
                    return false;
                }
                if (hitBot(other, x, myCenterY)) {
                    return true;
                }
            }
        }
        return false;
    }

    private GameBot getMyBot(World world) {
        for (GameBot bot : world.getBots()) {
            if (bot.getPlayer().equals(name)) {
                return bot;
            }
        }
        return null;
    }

    private World readWorld() {
        World world = api.readWorldStatus();
        return world;
    }

    private void readLevel() {
        obstacles = api.readLevel();
    }

    private void registerPlayer() {
        boolean success = api.createPlayer(name, COLOR, id);
        LOGGER.info("Creating player " + name + " was " + (success ? "" : "not ") + "succesfull.");
    }

    private void fire() {
        api.addAction(Action.FIRE, id);
    }

    private void turnLeft() {
        api.addAction(Action.TURN_LEFT, id);
    }

    private void turnRight() {
        api.addAction(Action.TURN_RIGHT, id);
    }

    private boolean hitBot(GameBot bot, int x, int y) {
        if (x >= bot.getX() && x < bot.getX() + bot.getWidth()) {
            if (y >= bot.getY() && y < bot.getY() + bot.getHeight()) {
                return true;
            }
        }
        return false;
    }

    private boolean hitOtherBot(World world, int x, int y) {
        for (GameBot bot : world.getBots()) {
            if (!bot.getPlayer().equals(name)) {
                if (x >= bot.getX() && x < bot.getX() + bot.getWidth()) {
                    if (y >= bot.getY() && y < bot.getY() + bot.getHeight()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hitWall(int x, int y) {
        for (GameObstacle obstacle : obstacles) {
            if (x >= obstacle.getX() && x < obstacle.getX() + obstacle.getWidth()) {
                if (y >= obstacle.getY() && y < obstacle.getY() + obstacle.getHeight()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sleep(long beginTime) {
        long endTime = System.currentTimeMillis();
        long deltaTime = endTime - beginTime;
        if (deltaTime < MAX_WAIT) {
            long sleepTime = MAX_WAIT - deltaTime;
            try {
                LOGGER.info("Sleeping for " + sleepTime + " ms.");
                Thread.sleep(sleepTime);
            } catch (InterruptedException iex) {
                // So?
            }
        }
    }

}
