package nl.mvdr.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.cgi.devnobot.api.GamePlayer;

/**
 * Representation of a player.
 * 
 * @author Martijn van de Rijdt
 */
@ToString
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Player {
    /** Player name. */
    private final String name;
    /** Player's unique identifier. */
    private final String id;
    /** Number of time this player's tank destroyed another tank. */
    private final int kills;
    /** Number of times this player got destroyed. Includes suicides. */
    private final int deaths;
    /** Color of the tank on the screen (hex web value). */
    private final String color;
    
    /**
     * Constructor.
     * 
     * @param gamePlayer
     */
    public Player(GamePlayer gamePlayer) {
        super();
        this.name = gamePlayer.getName();
        this.id = gamePlayer.getId();
        this.kills = gamePlayer.getKills();
        this.deaths = gamePlayer.getDeads();
        this.color = gamePlayer.getColor();
    }
}
