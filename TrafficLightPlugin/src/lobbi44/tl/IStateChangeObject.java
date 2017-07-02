package lobbi44.tl;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 * Created by WE on 23.05.17.
 */
@SerializableAs("StateChangeObject")
public interface IStateChangeObject extends ConfigurationSerializable{
    void setState(String state);
    void nextState();
    void update();
    /**
     * This method is called once every second for every IStateChangeObject.
     * Used for switching the state in time intervals
     */
    void tick();
    Location getLocation();
}
