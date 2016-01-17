package fr.azuxul.bomberman.event;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.entity.Bomb;
import fr.azuxul.bomberman.player.PlayerBomberman;
import net.samagames.api.games.Status;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

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

            Player player = event.getPlayer();
            PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());
            block.setType(Material.AIR);
            Location location = block.getLocation();

            Bomb bomb = new Bomb(((CraftWorld) block.getWorld()).getHandle(), location.getX() + 0.5, location.getY() + 0.1, location.getZ() + 0.5, 60, playerBomberman.getRadius(), player);
            playerBomberman.setBombNumber(playerBomberman.getBombNumber() - 1);

            ((CraftWorld) block.getWorld()).getHandle().addEntity(bomb);
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {

        event.blockList().clear();
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true); // Cancel food level change
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {

        if (event.toWeatherState()) // If is sunny
            event.setCancelled(true); // Cancel weather change
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true); // Cancel player drop item
    }
}
