package lobbi44.tl;

import lobbi44.kt.command.CommandEvent;
import lobbi44.kt.command.annotations.Command;
import lobbi44.tl.util.Selector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * Created by HT on 25.06.2017.
 */
public class LightCommands {

    private TrafficLightPlugin plugin;
    private List<IStateChangeObject> traffigLights;
    private Selector<IStateChangeObject> selectedLights = new Selector<>();

    LightCommands(TrafficLightPlugin plugin){
        this.plugin = plugin;
    }


    @Command(name = "tl.build", permission = "tl.build", description = "Builds a traffic light")
    public boolean buildTrafficLight(CommandEvent event) {
        Player player = (Player) event.getCommandSender();
        HashSet<Material> transparent = new HashSet<>();
        transparent.add(Material.AIR);
        Block block = player.getTargetBlock(transparent, 10);
        Location loc = block.getLocation();
        //makes the light face towards the creator
        loc.setYaw((loc.getYaw() + 180f) % 360f);
        IStateChangeObject light = new FrameLight(loc);
        traffigLights.add(light);
        light.update();
        return true;
    }

    @Command(name = "tl.switchAll", permission = "tl.switchAll", description = "Switches all traffic light")
    public boolean switchAllLights(CommandEvent event) {
        for (IStateChangeObject tl :
                traffigLights) {
            tl.nextState();
            tl.update();
        }
        return true;
    }

    @Command(name = "tl.switch", permission = "tl.switch", description = "Switches the selected traffic light")
    public boolean switchLight(CommandEvent event) {
        IStateChangeObject light = selectedLights.getSelected((Player) event.getCommandSender());
        light.nextState();
        light.update();
        return true;
    }

    @Command(name = "tl.save", description = "Saves the current state of the lights")
    public boolean saveConfigCommand(CommandEvent event) {


        try {
            plugin.saveLights();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Command(name = "tl.updateAll", description = "updates all lights. Use when something is broken")
    public boolean updateLightsCommand(CommandEvent event) {
        plugin.updateAllLights();
        return true;
    }

    @Command(name = "tl.update", description = "updates the selected light")
    public boolean updateSelectedLight(CommandEvent event) {
        Player player = ((Player) event.getCommandSender());
        selectedLights.getSelected(player).update();
        return true;
    }

    @Command(name = "tl.select", description = "Selects the light pointed at")
    public boolean selectLight(CommandEvent event) {
        Player player = (Player) event.getCommandSender();
        HashSet<Material> transparent = new HashSet<>();
        transparent.add(Material.AIR);
        Block block = player.getTargetBlock(transparent, 10);
        plugin.getLogger().info("Block = " + block);
        //todo: Write better search algorithm PLEASE!!!
        Location blockLocation = block.getLocation();
        plugin.getLogger().info("Location = " + blockLocation.toString());
        for (IStateChangeObject light : traffigLights) {
            if (light.getLocation().getBlockX() == blockLocation.getBlockX() && light.getLocation().getBlockY() == blockLocation.getBlockY()
                    && light.getLocation().getBlockZ() == blockLocation.getBlockZ()) {
                selectedLights.select(player, light);
                player.sendMessage("Selected a new traffic light");
                return true;
            }
        }
        player.sendMessage("There is no light to select");
        return false;
    }
}
