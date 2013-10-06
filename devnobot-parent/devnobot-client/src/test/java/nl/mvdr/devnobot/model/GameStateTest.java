package nl.mvdr.devnobot.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link GameState}.
 * 
 * @author Martijn van de Rijdt
 */
@Slf4j
public class GameStateTest {
    /** Test method for {@link GameState#toString()}. */
    @Test
    public void testToString() {
        GameState gameState = new GameState(Arrays.asList(TankTest.createNamedTank("Aad"),
                TankTest.createNamedTank("Sjaak")), Arrays.asList(new Bullet(0, 0, 4, 4), new Bullet(34, 12, 4, 4)));

        String string = gameState.toString();

        log.info(string);
        Assert.assertNotNull(string);
        Assert.assertNotEquals("", string);
    }

    /** Test method for {@link GameState#retrieveTankForPlayerName(String)}. */
    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveTankNoTanks() {
        GameState gameState = new GameState(Collections.<Tank>emptyList());

        gameState.retrieveTankForPlayerName("Aad");
    }

    /** Test method for {@link GameState#retrieveTankForPlayerName(String)}. */
    @Test(expected = IllegalArgumentException.class)
    public void testRetrieveTankNoMatchingTank() {
        GameState gameState = new GameState(Arrays.asList(TankTest.createNamedTank("Wim")));

        gameState.retrieveTankForPlayerName("Aad");
    }

    /** Test method for {@link GameState#retrieveTankForPlayerName(String)}. */
    @Test
    public void testRetrieveTank() {
        Tank tankInGameState = TankTest.createNamedTank("Wim");
        GameState gameState = new GameState(Arrays.asList(tankInGameState));

        Tank result = gameState.retrieveTankForPlayerName("Wim");

        Assert.assertSame(tankInGameState, result);
    }

    /** Test method for {@link GameState#retrieveTankForPlayerName(String)}. */
    @Test
    public void testRetrieveTankFromMultipleTanks() {
        Tank tankInGameState = TankTest.createNamedTank("Wim");
        GameState gameState = new GameState(Arrays.asList(TankTest.createNamedTank("Aad"), tankInGameState,
                TankTest.createNamedTank("Sjaak")));

        Tank result = gameState.retrieveTankForPlayerName("Wim");

        Assert.assertSame(tankInGameState, result);
    }

    /** Test method for {@link GameState#retrieveEnemies(String)}. */
    @Test
    public void testRetrieveEnemiesNoTanks() {
        GameState gameState = new GameState(Collections.<Tank>emptyList());

        Collection<Tank> result = gameState.retrieveEnemies("Aad");

        Assert.assertTrue(result.isEmpty());
    }

    /** Test method for {@link GameState#retrieveEnemies(String)}. */
    @Test
    public void testRetrieveEnemies() {
        Tank tank0 = TankTest.createNamedTank("Wim");
        Tank tank1 = TankTest.createNamedTank("Sjaak");
        GameState gameState = new GameState(Arrays.asList(tank0, tank1, TankTest.createNamedTank("Aad")));

        Collection<Tank> result = gameState.retrieveEnemies("Aad");

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(tank0));
        Assert.assertTrue(result.contains(tank1));
    }

    /** Test method for {@link GameState#retrieveEnemies(String)}. */
    @Test
    public void testRetrieveEnemiesNoMatchingTanks() {
        Tank tank0 = TankTest.createNamedTank("Wim");
        Tank tank1 = TankTest.createNamedTank("Sjaak");
        GameState gameState = new GameState(Arrays.asList(tank0, tank1));

        Collection<Tank> result = gameState.retrieveEnemies("Aad");

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(tank0));
        Assert.assertTrue(result.contains(tank1));
    }

    /** Test method for {@link GameState#retrieveEnemies(String)}. */
    @Test
    public void testRetrieveEnemiesMultipleMatchingTanks() {
        Tank tank0 = TankTest.createNamedTank("Wim");
        Tank tank1 = TankTest.createNamedTank("Sjaak");
        GameState gameState = new GameState(Arrays.asList(tank0, tank1, TankTest.createNamedTank("Aad"),
                TankTest.createNamedTank("Aad")));

        Collection<Tank> result = gameState.retrieveEnemies("Aad");

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(tank0));
        Assert.assertTrue(result.contains(tank1));
    }

    /** Test method for {@link GameState#wouldHit(String, Collection)} in case of a player that does not exist. */
    @Test(expected = IllegalArgumentException.class)
    public void testWouldHitNonexistentPlayer() {
        GameState gameState = new GameState(Collections.<Tank>emptyList());

        gameState.wouldHit("Aad", Collections.<Wall>emptyList());
    }

    /** Test method for {@link GameState#wouldHit(String, Collection)} in case of only one tank and no walls. */
    @Test
    public void testWouldHitNoWallsNoTanks() {
        GameState gameState = new GameState(Arrays.asList(TankTest.createNamedTank("Aad")));

        GameObject gameObject = gameState.wouldHit("Aad", Collections.<Wall>emptyList());

        Assert.assertNull(gameObject);
    }

    /** Test method for {@link GameState#wouldHit(String, Collection)} in case the tank would hit a wall. */
    @Test
    public void testWouldHitWall() {
        GameState gameState = new GameState(Arrays.asList(new Tank(0, 0, 10, 10, Orientation.EAST, 0, 0, "Aad", 0)));
        Wall wall = new Wall(20, 0, 1, 10);

        GameObject gameObject = gameState.wouldHit("Aad", Arrays.asList(wall));

        Assert.assertSame(wall, gameObject);
    }

    /** Test method for {@link GameState#wouldHit(String, Collection)} in case the tank would hit an enemy. */
    @Test
    public void testWouldHitEnemyTank() {
        Tank enemy = new Tank(20, 0, 10, 10, Orientation.EAST, 0, 0, "Wim", 0);
        GameState gameState = new GameState(Arrays.asList(new Tank(0, 0, 10, 10, Orientation.EAST, 0, 0, "Aad", 0),
                enemy));

        GameObject gameObject = gameState.wouldHit("Aad", Collections.<Wall>emptyList());

        Assert.assertSame(enemy, gameObject);
    }

    /** Test method for {@link GameState#wouldHit(String, Collection)} in case the tank would miss a wall. */
    @Test
    public void testWouldMissWall() {
        GameState gameState = new GameState(Arrays.asList(new Tank(0, 0, 10, 10, Orientation.EAST, 0, 0, "Aad", 0)));
        Wall wall = new Wall(20, 20, 1, 10);

        GameObject gameObject = gameState.wouldHit("Aad", Arrays.asList(wall));

        Assert.assertNull(gameObject);
    }

    /** Test method for {@link GameState#wouldHitEnemy(Tank, Collection)} in case the tank would hit a wall. */
    @Test
    public void testWouldHitEnemyWall() {
        Tank tank = new Tank(0, 0, 10, 10, Orientation.EAST, 0, 0, "Aad", 0);
        GameState gameState = new GameState(Arrays.asList(tank));
        Wall wall = new Wall(20, 0, 1, 10);

        Assert.assertFalse(gameState.wouldHitEnemy(tank, Collections.<Tank>emptyList(), Arrays.asList(wall)));
    }

    /** Test method for {@link GameState#wouldHitEnemy(Tank, Collection)} in case the tank would miss. */
    @Test
    public void testWouldHitEnemyMiss() {
        Tank tank = new Tank(0, 0, 10, 10, Orientation.EAST, 0, 0, "Aad", 0);
        GameState gameState = new GameState(Arrays.asList(tank));
        Wall wall = new Wall(20, 20, 1, 10);

        Assert.assertFalse(gameState.wouldHitEnemy(tank, Collections.<Tank>emptyList(), Arrays.asList(wall)));
    }

    /** Test method for {@link GameState#wouldHitEnemy(Tank, Collection)} in case the tank would hit an enemy. */
    @Test
    public void testWouldHitEnemy() {
        Tank tank0 = new Tank(0, 0, 10, 10, Orientation.EAST, 0, 0, "Aad", 0);
        Tank tank1 = new Tank(20, 0, 10, 10, Orientation.EAST, 0, 0, "Wim", 0);
        GameState gameState = new GameState(Arrays.asList(tank0, tank1));

        Assert.assertTrue(gameState.wouldHitEnemy(tank0, Arrays.asList(tank1), Collections.<Wall>emptyList()));
    }
}
