package lobbi44.tl.util;

import org.bukkit.block.BlockFace;

/**
 * Created by HT on 02.06.2017.
 */
public class Util {

    private static BlockFace[] directions = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public static BlockFace yawToBlockface(float yaw){
        if (yaw == 0f)
            return BlockFace.SOUTH;
        return directions[Math.round((yaw%360)/90f)];
    }
}
