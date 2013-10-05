package nl.mvdr.devnobot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.cgi.devnobot.api.GameBot;
import com.cgi.devnobot.api.GameBullet;

/**
 * Representation of the current game state.
 * 
 * Only contains dynamic objects, that is, the objects that can actually appear, disappear and move: tanks and bullets.
 * Walls are not included.
 * 
 * @author Martijn van de Rijdt
 */
@ToString
@Getter
@EqualsAndHashCode
public class World {
    /** Tanks in the game world. */
    private final Collection<Tank> tanks;
    /** Bullets. */
    private final Collection<Bullet> bullets;
    
    /**
     * Constructor.
     * 
     * @param world world as received from the client api
     */
    public World(com.cgi.devnobot.api.World world) {
        super();
        
        Collection<Tank> tempTanks = new ArrayList<>(world.getBots().size());
        for (GameBot gameBot: world.getBots()) {
            tempTanks.add(new Tank(gameBot));
        }
        this.tanks = Collections.unmodifiableCollection(tempTanks);
        
        Collection<Bullet> tempBullets = new ArrayList<>(world.getBullets().size());
        for (GameBullet gameBullet: world.getBullets()) {
            tempBullets.add(new Bullet(gameBullet));
        }
        this.bullets = Collections.unmodifiableCollection(tempBullets);
    }
}
