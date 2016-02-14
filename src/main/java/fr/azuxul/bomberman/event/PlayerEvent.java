package fr.azuxul.bomberman.event;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.player.PlayerBomberman;
import net.samagames.api.games.Status;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * PlayerEvents
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerEvent implements Listener {

    GameManager gameManager;

    public PlayerEvent(GameManager gameManager) {

        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        if (playerBomberman.isModerator() || playerBomberman.isSpectator()) {

            Location locTo = event.getTo();

            if (gameManager.getMapManager().getCaseAtWorldLocation(locTo) == null || locTo.getY() <= 0 || locTo.getY() >= 256)
                player.teleport(gameManager.getSpecSpawn());
        } else if (!event.getFrom().getBlock().equals(event.getTo().getBlock()) && gameManager.getStatus().equals(Status.IN_GAME))
            gameManager.getMapManager().movePlayer(player, event.getTo());

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Block block = event.getBlock();

        event.setCancelled(true);

        if (block.getType().equals(Material.CARPET) && block.getData() == 8 && gameManager.getStatus().equals(Status.IN_GAME)) {

            Location location = block.getLocation();

            if (!location.clone().add(0, -1, 0).getBlock().getType().equals(Material.STONE))
                return;

            Player player = event.getPlayer();
            PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

            if (playerBomberman.getBombNumber() > playerBomberman.getPlacedBombs()) {

                event.setCancelled(false);
                ItemStack bomb = new ItemStack(Material.CARPET, 1, (short) 8);
                ItemMeta itemMeta = bomb.getItemMeta();
                itemMeta.setDisplayName(ChatColor.GOLD + "Bomb");
                bomb.setItemMeta(itemMeta);
                player.getInventory().setItem(0, bomb);

                gameManager.getMapManager().spawnBomb(location, playerBomberman);
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

        if (gameManager.getStatus().equals(Status.IN_GAME)) {

            Player player = event.getEntity();
            PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

            Player killer = player.getKiller();
            final String deathMessageBase = gameManager.getCoherenceMachine().getGameTag() + " " + player.getName();

            if (killer == null)
                event.setDeathMessage(deathMessageBase + " viens d'exploser");
            else if (killer.equals(player))
                event.setDeathMessage(deathMessageBase + " viens de se faire exploser");
            else {

                event.setDeathMessage(deathMessageBase + " viens de se faire exploser par " + killer.getName());
                gameManager.getPlayer(killer.getUniqueId()).addCoins(5, "Meurtre de " + player.getName());
            }

            playerBomberman.setSpectator();

            if (gameManager.getConnectedPlayers() <= 1)
                gameManager.endGame();
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        if ((int) (event.getDamage() - 777.77) != 0 || !gameManager.getStatus().equals(Status.IN_GAME)) {
            event.setCancelled(true);
        }
    }
}
