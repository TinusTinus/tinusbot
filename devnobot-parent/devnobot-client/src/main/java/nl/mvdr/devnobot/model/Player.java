package nl.mvdr.devnobot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import com.cgi.devnobot.api.GamePlayer;

/**
 * Representation of a player.
 * 
 * @author Martijn van de Rijdt
 */
@ToString
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class Player {
    /** Header for the name column of the leaderboard. */
    private static final String NAME_HEADER = "PLAYER";
    /** Header for the score column of the leaderboard. */
    private static final String SCORE_HEADER = "SCORE";
    /** Header for the kills column of the leaderboard. */
    private static final String KILLS_HEADER = "KILLS";
    /** Header for the deaths column of the leaderboard. */
    private static final String DEATHS_HEADER = "DEATHS";

    /** Player name. */
    private final String name;
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
        this.kills = gamePlayer.getKills();
        this.deaths = gamePlayer.getDeads();
        this.color = gamePlayer.getColor();
    }

    /**
     * Computes the score for this player based on the kills and deaths. According to the rules, a kill is worth two
     * points whereas a death costs one.
     * 
     * @return score
     */
    public int computeScore() {
        return kills * 2 - deaths;
    }

    /**
     * Logs the current leaderboards.
     * 
     * @param players
     *            players
     * @deprecated use {@link Leaderboard}
     */
    @Deprecated
    public static void logLeaderboard(Collection<Player> players) {
        if (players.isEmpty()) {
            log.info("No players!");
        } else {
            // Create a map of players indexed by their scores.
            // Also determine layout information for the leaderboard table.
            Map<Integer, Collection<Player>> playersByScores = new HashMap<>(players.size());
            int maxNameLength = NAME_HEADER.length();
            int maxScoreLength = SCORE_HEADER.length();
            int maxKillsLength = KILLS_HEADER.length();
            int maxDeathsLength = DEATHS_HEADER.length();
            int maxPositionLength = Math.max(("" + players.size()).length(), 3);
            for (Player player : players) {
                int score = player.computeScore();
                if (!playersByScores.containsKey(Integer.valueOf(score))) {
                    playersByScores.put(Integer.valueOf(score), new HashSet<Player>());
                }
                playersByScores.get(Integer.valueOf(score)).add(player);
                maxNameLength = Math.max(maxNameLength, player.getName().length());
                maxScoreLength = Math.max(maxScoreLength, ("" + score).length());
                maxKillsLength = Math.max(maxKillsLength, ("" + player.getKills()).length());
                maxDeathsLength = Math.max(maxDeathsLength, ("" + player.getDeaths()).length());
            }

            // Sort scores in decreasing order
            List<Integer> scores = new ArrayList<>(playersByScores.keySet());
            Collections.sort(scores, Collections.reverseOrder());

            // Table header
            StringBuffer logMessage = new StringBuffer("Current leaderboard:\n\n");
            logMessage.append(String.format("%s %s %S %s %s\n",
                    padRight(" ", maxPositionLength),
                    padRight(NAME_HEADER, maxNameLength),
                    padRight(SCORE_HEADER, maxScoreLength),
                    padRight(KILLS_HEADER, maxKillsLength),
                    padRight(DEATHS_HEADER, maxDeathsLength)));

            int position = 1;
            for (Integer score : scores) {
                Collection<Player> playersForScore = playersByScores.get(score);
                for (Player player : playersForScore) {
                    logMessage.append(String.format("%s %s %S %s %s\n", 
                            padLeft(position, maxPositionLength),
                            padRight(player.getName(), maxNameLength),
                            padLeft(score.intValue(), maxScoreLength),
                            padLeft(player.getKills(), maxKillsLength),
                            padLeft(player.getDeaths(), maxDeathsLength)));
                }
                position = position + playersForScore.size();
            }
            log.info(logMessage.toString());
        }
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
}
