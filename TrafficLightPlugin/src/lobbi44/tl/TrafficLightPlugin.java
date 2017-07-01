package lobbi44.tl;


import lobbi44.kt.command.CommandFramework;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

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
        ConfigurationSerialization.registerClass(TrafficLight.class, "TrafficLighht");
        ConfigurationSerialization.registerClass(FrameLight.class, "FrameLight");
    }

    private CommandFramework framework;
    private List<IStateChangeObject> traffigLights = null;
    private File customConfigFile = new File(getDataFolder(), "TrafficLights.yml");
    private FileConfiguration lightSave = null;


    @Override
    public void onEnable() {
        getLogger().info("onEnable() called");
        initCommandFramework();

        lightSave = YamlConfiguration.loadConfiguration(customConfigFile);
        traffigLights = (List<IStateChangeObject>) lightSave.getList("activeLights");
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
        framework.registerCommands(new LightCommands(this, traffigLights));
        framework.registerHelpTopic("Ampel");
    }

    void saveLights() throws IOException {
        lightSave.set("activeLights", traffigLights);
        lightSave.save(customConfigFile);
    }

    void updateAllLights() {
        for (IStateChangeObject tl :
                traffigLights) {
            tl.update();
        }
    }


}
