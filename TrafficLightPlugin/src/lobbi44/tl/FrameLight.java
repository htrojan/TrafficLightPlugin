package lobbi44.tl;

import lobbi44.tl.util.Util;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SerializableAs("FrameLight")
public class FrameLight implements IStateChangeObject {

    public static final String RED = "red";
    public static final String GREEN = "green";

    private boolean state;
    private Location location;
    private Block top, bottom;
    private Location frameTopLoc, frameBotLoc;
    private BlockFace facing;
    private ItemFrame frameTop, frameBot;

    public FrameLight(Location location) {
        this(false, location);
    }

    private FrameLight(boolean currentState, Location location) {
        this.location = location;
        this.state = currentState;
        this.facing = Util.yawToBlockface(location.getYaw());

        setBlockLocs(location);
        searchForItemFrames();

    }

    private void searchForItemFrames() {
        Collection<Entity> entities = this.location.getWorld().getNearbyEntities(frameBotLoc,1, 2, 1);
        for (Entity entity : entities) {
            if (entity.getType() == EntityType.ITEM_FRAME){
                if (Util.BlockLocationEquals(entity.getLocation(), frameTopLoc))
                    frameTop = (ItemFrame) entity;
                else if (Util.BlockLocationEquals(entity.getLocation(), frameBotLoc))
                    frameBot = (ItemFrame) entity;
            }
        }
    }

    private void setBlockLocs(Location location) {
        bottom = location.getBlock();
        frameBotLoc = location.add(facing.getModX(), facing.getModY(), facing.getModZ()).clone();
        location.add(-facing.getModX(), -facing.getModY(), -facing.getModZ());

        top = location.add(0, 1, 0).getBlock();
        frameTopLoc = location.add(facing.getModX(), facing.getModY(), facing.getModZ()).clone();
        location.add(-facing.getModX(), -facing.getModY(), -facing.getModZ());

        location.add(0, -1, 0);
    }

    @Override
    public void setState(String state) {
        switch (state) {
            case RED:
                this.state = false;
            case GREEN:
                this.state = true;
        }
    }

    @Override
    public void nextState() {
        state = !state;
    }

    @Override
    public void update() {
        bottom.setType(Material.STAINED_CLAY);
        bottom.setData(DyeColor.BLACK.getWoolData());
        top.setType(Material.STAINED_CLAY);
        top.setData(DyeColor.BLACK.getWoolData());

        ItemStack greenWool = new ItemStack(Material.WOOL);
        greenWool.setData(new Wool(DyeColor.GREEN));
        ItemStack redWool = new ItemStack(Material.WOOL);
        redWool.setData(new Wool(DyeColor.RED));

        renewEntities();

        // location.getWorld().enti
        if (state) {
            //green
            frameTop.setItem(null);
            frameBot.setItem(greenWool);
        } else {
            //red
            frameTop.setItem(redWool);
            frameBot.setItem(null);
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }

    private void renewEntities() {
        if (frameTop == null || frameTop.isDead())
            frameTop = (ItemFrame) location.getWorld().spawnEntity(frameTopLoc, EntityType.ITEM_FRAME);

        if (frameBot == null || frameBot.isDead())
            frameBot = (ItemFrame) location.getWorld().spawnEntity(frameBotLoc, EntityType.ITEM_FRAME);

    }

    /**
     * Creates a Map representation of this class.
     * <p>
     * This class must provide a method to restore this class, as defined in
     * the {@link ConfigurationSerializable} interface javadocs.
     *
     * @return Map containing the current state of this class
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("state", state);
        return map;
    }

    public static FrameLight deserialize(Map<String, Object> args) {
        return new FrameLight((boolean) args.get("state"), (Location) args.get("location"));
    }
}
