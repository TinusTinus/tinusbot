package nl.mvdr.devnobot.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Leaderboard}.
 * 
 * @author Martijn van de Rijdt
 */
@Slf4j
public class LeaderboardTest {
    /** Tests the constructor and toString method in case of an empty collection of players. */
    @Test
    public void testConstructorEmptyCollection() {
        long time = System.currentTimeMillis();

        Leaderboard leaderboard = new Leaderboard(time, Collections.<Player>emptyList());

        log.info(leaderboard.toString());
        Assert.assertEquals(time, leaderboard.getCreationTime());
    }

    /** Tests the constructor and toString method with a bunch of players. */
    @Test
    public void testLogLeaderboard() {
        Player jan = new Player("Jan", 38, 0, "color");
        Player piet = new Player("Piet", 0, 324, "color");
        Player ton = new Player("Ton", 10, 8, "color");
        Player kees = new Player("Kees", 20, 50, "color");
        Player aad = new Player("Aad", 3, 2, "color");
        Player wim = new Player("Wim", 3, 2, "color");
        Player herp = new Player("Herp", 2, 0, "color");
        Player derp = new Player("Derp", 6, 8, "color");
        Collection<Player> players = Arrays.asList(jan, piet, ton, kees, aad, wim, herp, derp);

        Leaderboard leaderboard = new Leaderboard(System.currentTimeMillis(), players);

        log.info(leaderboard.toString());
    }

    /** Tests the constructor and toString method with players with long names or large kill / death values. */
    @Test
    public void testLogLeaderboardLongStrings() {
        Collection<Player> players = new HashSet<>();
        players.add(new Player("Player with a really long name", 6, 2, "color"));
        players.add(new Player("Many kills/deaths", 33276973, 73283729, "color"));

        Leaderboard leaderboard = new Leaderboard(System.currentTimeMillis(), players);

        log.info(leaderboard.toString());
    }

    /** Tests the constructor and toString method with a bunch of players. */
    @Test
    public void testLeaderboardRetrievePlayers() {
        Player jan = new Player("Jan", 38, 0, "color");    // score:   76
        Player piet = new Player("Piet", 0, 324, "color"); // score: -324
        Player ton = new Player("Ton", 10, 8, "color");    // score:   12
        Player kees = new Player("Kees", 20, 50, "color"); // score:  -10
        Player aad = new Player("Aad", 3, 2, "color");     // score:    4
        Player wim = new Player("Wim", 3, 2, "color");     // score:    4
        Player herp = new Player("Herp", 2, 0, "color");   // score:    4
        Player derp = new Player("Derp", 6, 8, "color");   // score:    4
        Collection<Player> players = Arrays.asList(jan, piet, ton, kees, aad, wim, herp, derp);

        Leaderboard leaderboard = new Leaderboard(System.currentTimeMillis(), players);

        players = leaderboard.retrievePlayers(1);
        Assert.assertEquals(1, players.size());
        Assert.assertTrue(players.contains(jan));
        players = leaderboard.retrievePlayers(2);
        Assert.assertEquals(1, players.size());
        Assert.assertTrue(players.contains(ton));
        players = leaderboard.retrievePlayers(3);
        Assert.assertEquals(4, players.size());
        Assert.assertTrue(players.contains(aad));
        Assert.assertTrue(players.contains(wim));
        Assert.assertTrue(players.contains(herp));
        Assert.assertTrue(players.contains(derp));
        players = leaderboard.retrievePlayers(4);
        Assert.assertTrue(players.isEmpty());
        players = leaderboard.retrievePlayers(5);
        Assert.assertTrue(players.isEmpty());
        players = leaderboard.retrievePlayers(6);
        Assert.assertTrue(players.isEmpty());
        players = leaderboard.retrievePlayers(7);
        Assert.assertEquals(1, players.size());
        Assert.assertTrue(players.contains(kees));
        players = leaderboard.retrievePlayers(8);
        Assert.assertEquals(1, players.size());
        Assert.assertTrue(players.contains(piet));
        players = leaderboard.retrievePlayers(9);
        Assert.assertTrue(players.isEmpty());
    }

    /** Tests the {@link Leaderboard#retrievePosition(String)} method. */
    @Test
    public void testRetrievePosition() {
        Player jan = new Player("Jan", 38, 0, "color");    // score:   76
        Player piet = new Player("Piet", 0, 324, "color"); // score: -324
        Player ton = new Player("Ton", 10, 8, "color");    // score:   12
        Player kees = new Player("Kees", 20, 50, "color"); // score:  -10
        Player aad = new Player("Aad", 3, 2, "color");     // score:    4
        Player wim = new Player("Wim", 3, 2, "color");     // score:    4
        Player herp = new Player("Herp", 2, 0, "color");   // score:    4
        Player derp = new Player("Derp", 6, 8, "color");   // score:    4
        Collection<Player> players = Arrays.asList(jan, piet, ton, kees, aad, wim, herp, derp);

        Leaderboard leaderboard = new Leaderboard(System.currentTimeMillis(), players);

        Assert.assertEquals(new PlayerAndPosition(jan, 1), leaderboard.retrievePosition(jan.getName()));
        Assert.assertEquals(new PlayerAndPosition(ton, 2), leaderboard.retrievePosition(ton.getName()));
        Assert.assertEquals(new PlayerAndPosition(aad, 3), leaderboard.retrievePosition(aad.getName()));
        Assert.assertEquals(new PlayerAndPosition(wim, 3), leaderboard.retrievePosition(wim.getName()));
        Assert.assertEquals(new PlayerAndPosition(herp, 3), leaderboard.retrievePosition(herp.getName()));
        Assert.assertEquals(new PlayerAndPosition(derp, 3), leaderboard.retrievePosition(derp.getName()));
        Assert.assertEquals(new PlayerAndPosition(kees, 7), leaderboard.retrievePosition(kees.getName()));
        Assert.assertEquals(new PlayerAndPosition(piet, 8), leaderboard.retrievePosition(piet.getName()));
        Assert.assertNull(leaderboard.retrievePosition("sjaak"));
    }
}
