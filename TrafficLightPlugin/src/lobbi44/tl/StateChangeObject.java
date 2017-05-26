package lobbi44.tl;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 * Created by WE on 23.05.17.
 */
@SerializableAs("StateChangeObject")
public abstract class StateChangeObject implements ConfigurationSerializable{
    abstract void setState(String state);
    abstract void nextState();
    abstract void update();

}
