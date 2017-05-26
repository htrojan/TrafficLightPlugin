package lobbi44.ctf;


import lobbi44.kt.command.CommandEvent;
import lobbi44.kt.command.CommandFramework;
import lobbi44.kt.command.annotations.Command;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 */
public class TrafficLightPlugin extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(StateChangeObject.class);
        ConfigurationSerialization.registerClass(TrafficLight.class);
    }

    private CommandFramework framework;
    private List<StateChangeObject> states = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("onEnable() called");
        initCommandFramework();
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




}
