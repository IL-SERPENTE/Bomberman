package fr.azuxul.bomberman.event;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.entity.Bomb;
import net.samagames.api.games.Status;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * PlayerEvents
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerEvent implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        GameManager gameManager = Bomberman.getGameManager();
        Block block = event.getBlock();

        if (block.getType().equals(Material.TNT) && gameManager.getStatus().equals(Status.IN_GAME)) {

            if (!block.getLocation().add(0, -1, 0).getBlock().getType().equals(Material.STONE)) {
                event.setCancelled(true);
                return;
            }

            block.setType(Material.AIR);
            Location location = block.getLocation();

            Bomb bomb = new Bomb(((CraftWorld) block.getWorld()).getHandle(), location.getX(), location.getY() + 0.3, location.getZ(), 2, 2, event.getPlayer());

            ((CraftWorld) block.getWorld()).getHandle().addEntity(bomb);
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {

        event.blockList().clear();
    }
}
