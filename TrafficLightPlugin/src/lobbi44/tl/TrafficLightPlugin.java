package lobbi44.tl;


import lobbi44.kt.command.CommandEvent;
import lobbi44.kt.command.CommandFramework;
import lobbi44.kt.command.annotations.Command;
import lobbi44.tl.util.Selector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 */
public class TrafficLightPlugin extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(IStateChangeObject.class);
        ConfigurationSerialization.registerClass(TrafficLight.class, "TrafficLighht");
        ConfigurationSerialization.registerClass(FrameLight.class, "FrameLight");
    }

    private CommandFramework framework;
    private List<IStateChangeObject> traffigLights = null;
    private File customConfigFile = new File(getDataFolder(), "TrafficLights.yml");
    private FileConfiguration config = null;
    private Selector<IStateChangeObject> selectedLights = new Selector<>();

    @Override
    public void onEnable() {
        getLogger().info("onEnable() called");
        initCommandFramework();

        config = YamlConfiguration.loadConfiguration(customConfigFile);
        traffigLights = (List<IStateChangeObject>) config.getList("activeLights");
        if (traffigLights == null)
            traffigLights = new ArrayList<>();
        updateAllLights();
    }

    @Override
    public void onDisable() {
        try {
            saveLights();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDisable();
    }

    private void initCommandFramework() {
        framework = new CommandFramework(this, this.getLogger());
        framework.registerCommands(this);
        framework.registerHelpTopic("Ampel");
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
        config.set("activeLights", traffigLights);

        try {
            saveLights();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Command(name = "tl.updateAll", description = "updates all lights. Use when something is broken")
    public boolean updateLightsCommand(CommandEvent event) {
        updateAllLights();
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
        getLogger().info("Block = " + block);
        //todo: Write better search algorithm PLEASE!!!
        Location blockLocation = block.getLocation();
        getLogger().info("Location = " + blockLocation.toString());
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

    private void saveLights() throws IOException {
        config.save(customConfigFile);
    }

    private void updateAllLights() {
        for (IStateChangeObject tl :
                traffigLights) {
            tl.update();
        }
    }


}
