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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

        event.setCancelled(true);

        if (block.getType().equals(Material.TNT) && gameManager.getStatus().equals(Status.IN_GAME)) {

            Location location = block.getLocation();

            if (!location.clone().add(0, -1, 0).getBlock().getType().equals(Material.STONE))
                return;

            Player player = event.getPlayer();
            PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

            if (playerBomberman.getBombNumber() > playerBomberman.getPlacedBombs()) {

                block.setType(Material.TNT);

                Bomb bomb = new Bomb(((CraftWorld) block.getWorld()).getHandle(), location.getX() + 0.5, location.getY() + 0.1, location.getZ() + 0.5, 60, playerBomberman.getRadius(), player);

                ((CraftWorld) block.getWorld()).getHandle().addEntity(bomb);
            }
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

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {

        event.setCancelled(true); // Cancel block break
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {

        GameManager gameManager = Bomberman.getGameManager();
        Player player = event.getEntity();
        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        Player killer = player.getKiller();
        final String deathMessageBase = gameManager.getCoherenceMachine().getGameTag() + " " + player.getName();

        if (killer == null)
            event.setDeathMessage(deathMessageBase + " viens d'exploser");
        else if (killer.equals(player))
            event.setDeathMessage(deathMessageBase + " viens de se faire exploser");
        else
            event.setDeathMessage(deathMessageBase + " viens de se faire exploser par " + killer.getName());

        playerBomberman.setSpectator();

        if (gameManager.getConnectedPlayers() <= 1)
            gameManager.endGame();

    }
}
