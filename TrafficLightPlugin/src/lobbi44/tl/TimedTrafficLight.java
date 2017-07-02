package lobbi44.tl;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public abstract class TimedTrafficLight implements IStateChangeObject{

    public final static String RED = "red";
    public final static String GREEN = "green";
    /**
     * The current state (red or green) of the light. True equals green, false equals red
     */
    protected boolean currentState = false;
    /**
     * The time the light is green/red before it switches states
     */
    private int greenTime = 2, redTime = 1;
    /**
     * The time elapsed since the last stateChange
     */
    private int timer;
    /**
     * If this is true the light will be unaffected by state switches normally occurring due to timings
     */
    private boolean paused = false;

    /**
     * The location where the light is build
     */
    protected Location location;

    /**
     * This is only used during deserialization for restoring the previously used timer
     * @param timer The elapsed time to use
     */
    protected TimedTrafficLight(Location location, boolean currentState, int timer){
        this.timer = timer;
        this.location = location;
        this.currentState = currentState;
    }

    /**
     * Switches states. Update() needs to be called to make the changes visible in the world
     */
    @Override
    public void nextState() {
        currentState = !currentState;
    }

    public boolean isGreen(){
        return currentState;
    }

    public boolean isRed(){
        return !currentState;
    }

    @Override
    public void setState(String state) {
        switch (state) {
            case RED:
                currentState = false;
            case GREEN:
                currentState = true;
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }

    /**
     * This method is called once every second for every IStateChangeObject.
     * Used for switching the state in time intervals
     */
    @Override
    public void tick() {
        if (paused)
            return;
        ++timer;
        //Green
        if (currentState) {
            if (timer >= greenTime){
                nextState(); update(); timer = 0;
            }
        } else { //Red
            if (timer >= redTime){
                nextState(); update(); timer = 0;
            }
        }
    }

    protected int getTimer(){
        return timer;
    }

    /**
     * Creates a Map representation of this class.
     * This class must provide a method to restore this class, as defined in
     * the {@link ConfigurationSerializable} interface javadocs.
     *
     * @return Map containing the current state of this class
     */
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("state", currentState);
        map.put("timer", getTimer());
        return map;
    }
}
