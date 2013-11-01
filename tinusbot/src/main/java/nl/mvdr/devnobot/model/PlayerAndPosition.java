package nl.mvdr.devnobot.model;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Container class for a {@link Player} and their current position in the leaderboard.
 * 
 * @author Martijn van de Rijdt
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PlayerAndPosition {
    /** Player. */
    private final Player player;
    /** Player's position in the leaderboard. */
    private final int position;
}
