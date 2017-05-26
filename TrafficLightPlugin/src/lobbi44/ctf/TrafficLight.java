package lobbi44.ctf;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WE on 23.05.17.
 */
@SerializableAs("TrafficLight")
public class TrafficLight extends StateChangeObject
{
    private Location location;
    private final static String RED = "red";
    private final static String GREEN = "green";
    private boolean currentState = false;
    private Block red, green;

    public TrafficLight(Location location){
        this.location = location;
        green = location.getBlock();
        location.add(0, 1, 0);
        red = location.getBlock();
        location.add(0, -1, 0);

        update();
    }


    @Override
    public void setState(String state) {
        switch (state){
            case RED:
                currentState = false;
            case GREEN:
                currentState = true;
        }
    }

    @Override
    public String toString() {
        return "TrafficLight{" + location.toString() + ",state=" + currentState + "}";
    }

    @Override
    public void nextState() {
        currentState = !currentState;
    }

    @Override
    public void update() {
        green.setType(Material.WOOL);
        red.setType(Material.WOOL);
        if (currentState){
            //Green
            green.setData(DyeColor.GREEN.getData());
            red.setData(DyeColor.GRAY.getData());

            location.getBlock().setType(Material.WOOL);
        }else{
            //Red
            green.setData(DyeColor.GRAY.getData());
            red.setData(DyeColor.RED.getData());
        }
    }


    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("currentState", currentState);
        return map;
    }

    public static TrafficLight deserialize(Map<String, Object> args){
        boolean currentState = (Boolean)args.get("currentState");
        Location location = (Location)args.get("location");
        return new TrafficLight(location)
    }
}