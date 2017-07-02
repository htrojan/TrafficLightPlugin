package lobbi44.tl;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
@SerializableAs("TrafficLight")
public class TrafficLight extends TimedTrafficLight {


    private Block red, green;


    public TrafficLight(Location location) {
        this(location, true, 0);
    }

    /**
     * This is only used during deserialization for restoring the previously used timer
     *
     * @param timer The elapsed time to use
     */
    private TrafficLight(Location location, boolean currentState, int timer) {
        super(location, currentState, timer);
        this.currentState = currentState;
        green = location.getBlock();
        location.add(0, 1, 0);
        red = location.getBlock();
        location.add(0, -1, 0);
    }



    public static TrafficLight deserialize(Map<String, Object> args) {
        boolean currentState = (Boolean) args.get("state");
        Location location = (Location) args.get("location");
        int timer = (int) args.get("timer");
        return new TrafficLight(location, currentState, timer);
    }

    @Override
    public String toString() {
        return "TrafficLight{" + location.toString() + ",state=" + currentState + "}";
    }

    @Override
    public void update() {
        green.setType(Material.WOOL);
        red.setType(Material.WOOL);
        if (currentState) {
            //Green
            red.setData(DyeColor.GRAY.getWoolData());
            green.setData(DyeColor.LIME.getWoolData());

        } else {
            //Red
            red.setData(DyeColor.RED.getWoolData());
            green.setData(DyeColor.GRAY.getWoolData());
        }
    }
}
