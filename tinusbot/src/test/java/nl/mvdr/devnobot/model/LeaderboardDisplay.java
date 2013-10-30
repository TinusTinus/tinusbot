package nl.mvdr.devnobot.model;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.Timer;

import lombok.extern.slf4j.Slf4j;
import nl.mvdr.devnobot.clientapi.ClientApi;

/**
 * A Runnable that opens a Swing frame and uses it to display a continuously updated leaderboard.
 * 
 * Uses Swing, because although JavaFX is awesome, it's not really worth the classpath hassle for this.
 * 
 * @author Martijn van de Rijdt
 */
@Slf4j
public class LeaderboardDisplay {
    /** Devnobot client API. */
    private final ClientApi api;
    /** Text component that contains the leaderboard text. */
    private final JTextArea textComponent;

    /**
     * Constructor.
     * 
     * @param api
     *            Devnobot client API
     */
    public LeaderboardDisplay(ClientApi api) {
        super();
        
        this.textComponent = new JTextArea("No leaderboard information available yet!");
        this.textComponent.setFont(new Font("Courier New", Font.BOLD, 14));
        this.textComponent.setEditable(false);
        
        this.api = api;

        updateLeaderboard();
    }

    /** Initialises and shows the application's frame and starts the timer that periodically updates the leaderboard. */
    public void start() {
        JFrame frame = new JFrame("Leaderboard");
        frame.setSize(new Dimension(400, 300));
        frame.add(textComponent);
        frame.setVisible(true);

        new Timer(1000, new ActionListener() {
            /** {@inheritDoc} */
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLeaderboard();
            }
        }).start();
    }

    /** Retrieves the latest leaderboard info and updates the label. */
    private void updateLeaderboard() {
        try {
            Collection<Player> players = api.readPlayers();
            Leaderboard leaderboard = new Leaderboard(System.currentTimeMillis(), players);
            textComponent.setText(leaderboard.toString());
            log.info("Updated the leaderboard.");
        } catch (RuntimeException exception) {
            log.warn("Unexpected exception while updating leaderboard.", exception);
        }
    }
}
