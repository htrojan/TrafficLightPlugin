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
        framework.registerCommands(new LightCommands(this));
        framework.registerHelpTopic("Ampel");
    }

    void saveLights() throws IOException {
        config.set("activeLights", traffigLights);
        config.save(customConfigFile);
    }

    void updateAllLights() {
        for (IStateChangeObject tl :
                traffigLights) {
            tl.update();
        }
    }


}
