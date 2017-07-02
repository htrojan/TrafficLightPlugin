package lobbi44.tl;


import lobbi44.kt.command.CommandFramework;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TrafficLightPlugin extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(IStateChangeObject.class);
        ConfigurationSerialization.registerClass(TrafficLight.class, "TrafficLight");
        ConfigurationSerialization.registerClass(FrameLight.class, "FrameLight");
    }

    private CommandFramework framework;
    private List<IStateChangeObject> trafficLights = null;
    private File customConfigFile = new File(getDataFolder(), "TrafficLights.yml");
    private FileConfiguration lightSave = null;


    @Override
    public void onEnable() {
        getLogger().info("onEnable() called");

        lightSave = YamlConfiguration.loadConfiguration(customConfigFile);
        trafficLights = (List<IStateChangeObject>) lightSave.getList("activeLights");
        if (trafficLights == null) {
            trafficLights = new ArrayList<>();
        }
        initCommandFramework();
        updateAllLights();
        startLightTimer();
    }

    /**
     * Starts a synchronous task that will execute every second and update all lights
     */
    private void startLightTimer() {
        BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            for (IStateChangeObject light : trafficLights) {
                light.tick();
            }
        }, 0L, 20L);
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
        framework.registerCommands(new LightCommands(this, trafficLights));
        framework.registerHelpTopic("Ampel");
    }

    void saveLights() throws IOException {
        lightSave.set("activeLights", trafficLights);
        lightSave.save(customConfigFile);
    }

    void updateAllLights() {
        for (IStateChangeObject tl :
                trafficLights) {
            tl.update();
        }
    }


}
