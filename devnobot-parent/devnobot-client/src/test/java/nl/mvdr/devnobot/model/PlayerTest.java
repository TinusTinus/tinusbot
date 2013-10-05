package nl.mvdr.devnobot.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Player}.
 * 
 * @author Martijn van de Rijdt
 */
public class PlayerTest {
    /** Test method for {@link Player#computeScore()} in case of 0 kills and 0 deaths. */
    @Test
    public void testComputeScore00() {
        Player player = createPlayer(0, 0);

        Assert.assertEquals(0, player.computeScore());
    }

    /** Test method for {@link Player#computeScore()} in case of 1 kill and 0 deaths. */
    @Test
    public void testComputeScore10() {
        Player player = createPlayer(1, 0);

        Assert.assertEquals(2, player.computeScore());
    }

    /** Test method for {@link Player#computeScore()} in case of 0 kills and 1 death. */
    @Test
    public void testComputeScore01() {
        Player player = createPlayer(0, 1);

        Assert.assertEquals(-1, player.computeScore());
    }

    /** Test method for {@link Player#computeScore()} in case of 1 kill and 1 death. */
    @Test
    public void testComputeScore11() {
        Player player = createPlayer(1, 1);

        Assert.assertEquals(1, player.computeScore());
    }

    /** Test method for {@link Player#computeScore()} in case of 9 kills and 4 deaths. */
    @Test
    public void testComputeScore94() {
        Player player = createPlayer(9, 4);

        Assert.assertEquals(14, player.computeScore());
    }

    /**
     * Creates a player with the given number of kills and deaths. All other fields get dummy values.
     * 
     * @param kills
     *            kills
     * @param deaths
     *            deaths
     * @return player
     */
    private Player createPlayer(int kills, int deaths) {
        return new Player("name", kills, deaths, "color");
    }

    /** Test method for {@link Player#logLeaderboard(Collection)} in case of an empty collection of players. */
    @Test
    public void testLogLeaderboardEmptyCollection() {
        Player.logLeaderboard(Collections.<Player>emptySet());
    }

    /** Test method for {@link Player#logLeaderboard(Collection)}. */
    @Test
    public void testLogLeaderboard() {
        Collection<Player> players = new HashSet<>();
        players.add(new Player("Jan", 38, 0, "color"));
        players.add(new Player("Piet", 0, 324, "color"));
        players.add(new Player("Ton", 10, 8, "color"));
        players.add(new Player("Kees", 20, 50, "color"));
        players.add(new Player("Aad", 3, 2, "color"));
        players.add(new Player("Wim", 3, 2, "color"));
        players.add(new Player("Herp", 2, 0, "color"));
        players.add(new Player("Derp", 6, 8, "color"));
        Player.logLeaderboard(players);
    }
    
    /** Test method for {@link Player#logLeaderboard(Collection)}. */
    @Test
    public void testLogLeaderboardLongStrings() {
        Collection<Player> players = new HashSet<>();
        players.add(new Player("Player with a really long name", 6, 2, "color"));
        players.add(new Player("Many kills/deaths", 33276973, 73283729, "color"));
        Player.logLeaderboard(players);
    }
}
