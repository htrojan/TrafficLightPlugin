package lobbi44.tl.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Selects game objects per player
 * @param <T> The type of objects to select
 */
public class Selector<T> {

    private Map<Player, T> selected = new HashMap<>();

    public T getSelected(Player player){
        return selected.get(player);
    }

    public void select(Player player, T object){
        selected.put(player, object);
    }

    public void clear(@NotNull Player player){
        selected.remove(player);
    }

    public void clearAll(){
        selected = new HashMap<>();
    }

}
