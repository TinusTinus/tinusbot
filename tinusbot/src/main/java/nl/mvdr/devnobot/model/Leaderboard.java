package nl.mvdr.devnobot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * Leaderboard.
 * 
 * @author Martijn van de Rijdt
 */
public class Leaderboard {
    /** Header for the name column of the leaderboard. */
    private static final String NAME_HEADER = "PLAYER";
    /** Header for the score column of the leaderboard. */
    private static final String SCORE_HEADER = "SCORE";
    /** Header for the kills column of the leaderboard. */
    private static final String KILLS_HEADER = "KILLS";
    /** Header for the deaths column of the leaderboard. */
    private static final String DEATHS_HEADER = "DEATHS";

    /** The approximate timestamp when this leaderboard was put together. */
    @Getter
    private final long creationTime;
    /** Map of the players, using their position as index. */
    private final Map<Integer, Collection<Player>> playersByPosition;

    /**
     * Constructor.
     * 
     * @param creationTime
     *            the approximate timestamp when this leaderboard was put together
     * @param players
     *            players in the game
     */
    public Leaderboard(long creationTime, Collection<Player> players) {
        this.creationTime = creationTime;

        // Create a map of players ordered by their scores
        Map<Integer, Collection<Player>> playersByScores = new HashMap<>(players.size());
        for (Player player : players) {
            int score = player.computeScore();
            if (!playersByScores.containsKey(Integer.valueOf(score))) {
                playersByScores.put(Integer.valueOf(score), new HashSet<Player>());
            }
            playersByScores.get(Integer.valueOf(score)).add(player);
        }

        // Sort scores in decreasing order
        List<Integer> scores = new ArrayList<>(playersByScores.keySet());
        Collections.sort(scores, Collections.reverseOrder());

        // Create the map of players indexed by their positions
        Map<Integer, Collection<Player>> tempPlayersByPosition = new HashMap<>(scores.size());
        int position = 1;
        for (Integer score : scores) {
            Collection<Player> playersForScore = playersByScores.get(score);
            tempPlayersByPosition.put(Integer.valueOf(position), Collections.unmodifiableCollection(playersForScore));
            position = position + playersForScore.size();
        }
        this.playersByPosition = Collections.unmodifiableMap(tempPlayersByPosition);
    }

    /**
     * Gets a collection of all players at the given position.
     * 
     * @param position
     *            position
     * @return players
     */
    public Collection<Player> retrievePlayers(int position) {
        Collection<Player> result = this.playersByPosition.get(Integer.valueOf(position));
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

    /** @return a list of all positions in increasing order */
    public List<Integer> retrievePositions() {
        List<Integer> positions = new ArrayList<>(playersByPosition.keySet());
        Collections.sort(positions);
        return positions;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        if (playersByPosition.isEmpty()) {
            result.append("No players!");
        } else {
            // First, determine layout information for the leaderboard table.
            int maxNameLength = NAME_HEADER.length();
            int maxScoreLength = SCORE_HEADER.length();
            int maxKillsLength = KILLS_HEADER.length();
            int maxDeathsLength = DEATHS_HEADER.length();
            for (Collection<Player> players : playersByPosition.values()) {
                for (Player player : players) {
                    maxNameLength = Math.max(maxNameLength, player.getName().length());
                    maxScoreLength = Math.max(maxScoreLength, ("" + player.computeScore()).length());
                    maxKillsLength = Math.max(maxKillsLength, ("" + player.getKills()).length());
                    maxDeathsLength = Math.max(maxDeathsLength, ("" + player.getDeaths()).length());
                }
            }
            List<Integer> positions = retrievePositions();
            int maxPositionLength = Math.max(("" + positions.get(positions.size() - 1)).length(), 3);

            // Table header
            result.append("Current leaderboard:\n");
            result.append(String.format("%s %s %s %s %s\n",
                    padRight(" ", maxPositionLength),
                    padRight(NAME_HEADER, maxNameLength),
                    padRight(SCORE_HEADER, maxScoreLength),
                    padRight(KILLS_HEADER, maxKillsLength),
                    padRight(DEATHS_HEADER, maxDeathsLength)));

            for (Integer position : positions) {
                Collection<Player> players = playersByPosition.get(position);
                for (Player player : players) {
                    result.append(String.format("%s %s %s %s %s\n", 
                            padLeft(position.intValue(), maxPositionLength),
                            padRight(player.getName(), maxNameLength),
                            padLeft(player.computeScore(), maxScoreLength),
                            padLeft(player.getKills(), maxKillsLength),
                            padLeft(player.getDeaths(), maxDeathsLength)));
                }
            }
        }
        return result.toString();
    }

    /**
     * Pads the given string on the right.
     * 
     * @param s
     *            string to be padded
     * @param n
     *            number of positions
     * @return padded string
     */
    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    /**
     * Converts the given int to a string and pads it on the left.
     * 
     * @param i
     *            int to be padded
     * @param n
     *            number of positions
     * @return padded string
     */
    private static String padLeft(int i, int n) {
        return String.format("%1$" + n + "s", Integer.valueOf(i));
    }

    /**
     * Given a player name, returns the player's position, or null if the player name does not occur on the leaderboard.
     * 
     * @param playerName
     *            player name; may not be null
     * @return player and their position, or null if unavailable
     */
    public PlayerAndPosition retrievePosition(String playerName) {
        PlayerAndPosition result = null;
        Iterator<Integer> positionsIterator = retrievePositions().iterator();
        while (result == null && positionsIterator.hasNext()) {
            Integer position = positionsIterator.next();
            Iterator<Player> playerIterator = this.playersByPosition.get(position).iterator();
            while (result == null && playerIterator.hasNext()) {
                Player player = playerIterator.next();
                if (playerName.equals(player.getName())) {
                    result = new PlayerAndPosition(player, position.intValue());
                }
            }
        }
        return result;
    }
}
