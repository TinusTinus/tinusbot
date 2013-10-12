package nl.mvdr.devnobot.bot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;

import nl.mvdr.devnobot.clientapi.ClientApi;
import nl.mvdr.devnobot.model.Action;
import nl.mvdr.devnobot.model.GameState;
import nl.mvdr.devnobot.model.Wall;

/**
 * Not an artificial intelligence at all; this bot opens up a Swing user interface so the user can manually control the
 * robot.
 * 
 * @author Martijn van de Rijdt
 */
public class InteractiveBot extends BotArtificialIntelligence {
    /** Actions. */
    private final BlockingQueue<Action> actionQueue;
    
    /**
     * Constructor.
     * 
     * @param clientApi client api
     */
    public InteractiveBot(ClientApi clientApi) {
        super(clientApi, "Interactive Bot", Color.YELLOW);
        
        this.actionQueue = new LinkedBlockingQueue<>();
        
        JFrame frame = new JFrame(getName());
        frame.setLayout(new FlowLayout());
        frame.setSize(new Dimension(100, 300));
        for (Action action: Action.values()) {
            frame.add(createActionButton(action));
        }
        frame.setVisible(true);
    }

    /**
     * Creates a button. Pressing the button places the given action in the action queue.
     * 
     * @param action action corresponding to the button
     * @return button
     */
    private JButton createActionButton(final Action action) {
        javax.swing.Action swingAction = new javax.swing.Action() {
            /** {@inheritDoc} */
            @Override
            public void actionPerformed(ActionEvent e) {
                actionQueue.add(action);
            }

            /** {@inheritDoc} */
            @Override
            public void setEnabled(boolean b) {
                throw new UnsupportedOperationException();
            }
            
            /** {@inheritDoc} */
            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) {
                // do nothing
            }
            
            /** {@inheritDoc} */
            @Override
            public void putValue(String key, Object value) {
                throw new UnsupportedOperationException();
            }
            
            /** {@inheritDoc} */
            @Override
            public boolean isEnabled() {
                return true;
            }
            
            /** {@inheritDoc} */
            @Override
            public Object getValue(String key) {
                Object result;
                if (javax.swing.Action.NAME.equals(key)) {
                    result = action.toString();
                } else if (javax.swing.Action.SHORT_DESCRIPTION.equals(key) || javax.swing.Action.LONG_DESCRIPTION.equals(key)) {
                    result = "Add this action to the queue: " + action.toString();
                } else {
                    result = null;
                }
                return result;
            }
            
            /** {@inheritDoc} */
            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) {
                // do nothing
            }
        };
        
        return new JButton(swingAction);
    }
    
    /** {@inheritDoc} */
    @Override
    protected Action determineNextAction(Collection<Wall> obstacles, GameState state) {
        Action action;
        try {
            action = actionQueue.take();
        } catch (InterruptedException e) {
            // thow runtime exception so it can be handled by the game loop
            throw new IllegalStateException("Unexpected exception", e);
        }
        return action;
    }

    
    
}
