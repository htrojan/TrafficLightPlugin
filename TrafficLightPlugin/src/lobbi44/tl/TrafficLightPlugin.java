package lobbi44.tl;


import lobbi44.kt.command.CommandEvent;
import lobbi44.kt.command.CommandFramework;
import lobbi44.kt.command.annotations.Command;
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
        ConfigurationSerialization.registerClass(StateChangeObject.class);
        ConfigurationSerialization.registerClass(TrafficLight.class, "TrafficLight");
    }

    private CommandFramework framework;
    private List<StateChangeObject> states = null;
    private File customConfigFile = new File(getDataFolder(), "TrafficLights.yml");
    private FileConfiguration config = null;

    @Override
    public void onEnable() {
        getLogger().info("onEnable() called");
        initCommandFramework();

        config = YamlConfiguration.loadConfiguration(customConfigFile);
        states = (List<StateChangeObject>) config.getList("activeLights");
        if (states == null)
            states = new ArrayList<>();
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

    private void initCommandFramework(){
        framework = new CommandFramework(this, this.getLogger());
        framework.registerCommands(this);
        framework.registerHelpTopic("Ampel");
    }

    @Command(name = "tl.build", permission = "tl.build", description = "Builds a traffic light")
    public boolean buildTrafficLight(CommandEvent event){
        Player player = (Player) event.getCommandSender();
        HashSet<Material> transparent = new HashSet<>();
        transparent.add(Material.AIR);
        Block block = player.getTargetBlock(transparent, 10);
        states.add(new TrafficLight(block.getLocation()));
        return true;
    }

    @Command(name = "tl.switch", permission = "tl.switch", description = "Switches all traffic light")
    public boolean switchAllLights(CommandEvent event){
        for (StateChangeObject tl :
                states) {
            tl.nextState();
            tl.update();
        }
        return true;
    }

    @Command(name = "tl.save", description = "Saves the current state of the lights")
    public boolean saveConfigCommand(CommandEvent event){
        config.set("activeLights", states);

        try {
            saveLights();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Command(name = "tl.update", description = "updates all lights. Use when something is broken")
    public boolean updateLightsCommand(CommandEvent event){
        updateAllLights();
        return true;
    }

    private void saveLights() throws IOException {
        config.save(customConfigFile);
    }

    private void updateAllLights(){
        for (StateChangeObject tl :
                states) {
            tl.update();
        }
    }




}
