package nl.mvdr.devnobot.model;

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
}
