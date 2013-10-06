package nl.mvdr.devnobot.model;

import java.util.EnumMap;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Representation of a tank position with proper equals and hashCode implementation.
 * 
 * @author Martijn van de Rijdt
 */
@ToString
@Getter
@RequiredArgsConstructor
public class TankPosition {
    /** Movement actions. */
    private static final Action[] ACTIONS = {Action.FORWARD, Action.BACKWARD, Action.TURN_LEFT, Action.TURN_RIGHT};
    
    /** Tank. */
    @NonNull
    private final Tank tank;
    
    /**
     * Computes a map of all reachable positions from the current position, indexed by the required action.
     * 
     * @return reachable positions
     */
    public Map<Action, TankPosition> computeReachablePositions() {
        Map<Action, TankPosition> result = new EnumMap<>(Action.class);
        
        for (Action action: ACTIONS) {
            result.put(action, new TankPosition(tank.computeNextPosition(action)));
        }
        
        return result;
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + tank.getHeight();
        result = prime * result + tank.getWidth();
        result = prime * result + tank.getX();
        result = prime * result + tank.getY();
        result = prime * result + tank.getLastKnownOrientation().hashCode();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TankPosition other = (TankPosition) obj;
        if (tank.getHeight() != other.tank.getHeight())
            return false;
        if (tank.getWidth() != other.tank.getWidth())
            return false;
        if (tank.getX() != other.tank.getX())
            return false;
        if (tank.getY() != other.tank.getY())
            return false;
        if (tank.getLastKnownOrientation() != other.tank.getLastKnownOrientation())
            return false;
        return true;
    }
}
