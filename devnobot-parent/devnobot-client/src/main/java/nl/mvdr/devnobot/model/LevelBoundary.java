package nl.mvdr.devnobot.model;

import java.util.ArrayList;
import java.util.Collection;

import lombok.ToString;

/**
 * Representation of the boundaries of the playing field.
 * 
 * @author Martijn van de Rijdt
 */
@ToString(callSuper = true)
public class LevelBoundary extends GameObject {
    /**
     * Constructs the level boundaries.
     * 
     * In the sample code the playing field is surrounded by walls, but we won't make the assumption that that will be
     * the case in every level and risk getting stuck in an infinite loop. Since we don't know the size of the screen,
     * we compute the minimum and maximum coordinates of the objects.
     * 
     * @param walls
     *            walls in the level
     * @param tanks
     *            current tanks in the level
     * @return level boundaries
     */
    public static LevelBoundary buildLevelBoundary(Collection<Wall> walls, Collection<Tank> tanks) {
        // build a single collection containing both tanks and walls
        Collection<GameObject> objects = new ArrayList<>(tanks.size() - 1 + walls.size());
        objects.addAll(tanks);
        objects.addAll(walls);

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (GameObject object : objects) {
            minX = Math.min(minX, object.getX());
            maxX = Math.max(maxX, object.computeMaxX());
            minY = Math.min(minY, object.getY());
            maxY = Math.max(maxY, object.computeMaxY());
        }

        return new LevelBoundary(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Constructor.
     * 
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     * @param width
     *            width
     * @param height
     *            height
     */
    private LevelBoundary(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
