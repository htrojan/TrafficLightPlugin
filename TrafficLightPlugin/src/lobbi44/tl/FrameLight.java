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
public class FrameLight extends TimedTrafficLight {

    private Block top, bottom;
    private Location frameTopLoc, frameBotLoc;
    private BlockFace facing;
    private ItemFrame frameTop, frameBot;

    public FrameLight(Location location) {
        this(false, location, 0);
    }

    private FrameLight(boolean currentState, Location location, int timer) {
        super(location, currentState, timer);
        this.location = location;
        this.currentState = currentState;
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

        if (isGreen()) {
            //green
            frameTop.setItem(null);
            frameBot.setItem(greenWool);
        } else {
            //red
            frameTop.setItem(redWool);
            frameBot.setItem(null);
        }
    }


    private void renewEntities() {
        if (frameTop == null || frameTop.isDead())
            frameTop = (ItemFrame) location.getWorld().spawnEntity(frameTopLoc, EntityType.ITEM_FRAME);

        if (frameBot == null || frameBot.isDead())
            frameBot = (ItemFrame) location.getWorld().spawnEntity(frameBotLoc, EntityType.ITEM_FRAME);

    }



    public static FrameLight deserialize(Map<String, Object> args) {
        return new FrameLight((boolean) args.get("state"), (Location) args.get("location"), (int) args.get("timer"));
    }
}
