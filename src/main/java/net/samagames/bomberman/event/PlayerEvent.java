package net.samagames.bomberman.event;

import net.samagames.api.games.Status;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.Music;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.bomberman.powerup.PowerupTypes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * PlayerEvents
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerEvent implements Listener {

    private final GameManager gameManager;

    public PlayerEvent(GameManager gameManager) {

        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getItem() == null)
            return;

        Player player = event.getPlayer();
        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());
        Material material = event.getItem().getType();

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            if (material.equals(Material.GREEN_RECORD)) {

                playerBomberman.startMusic();
            } else if (material.equals(Material.RECORD_4)) {

                playerBomberman.stopMusic();
            }

        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager() != null && event.getDamager() instanceof Player && event.getDamager().equals(event.getEntity())) {

            PlayerBomberman playerBomberman = gameManager.getPlayer(event.getEntity().getUniqueId());

            if (playerBomberman.getPowerupTypes() != null && playerBomberman.getPowerupTypes().equals(PowerupTypes.SELF_INVULNERABILITY)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerHeldItem(PlayerItemHeldEvent event) {

        if (gameManager.getStatus().equals(Status.IN_GAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        if (playerBomberman == null || playerBomberman.isModerator() || playerBomberman.isSpectator()) {

            Location locTo = event.getTo();

            if (gameManager.getMapManager().getCaseAtWorldLocation(locTo.getBlockX()  , locTo.getBlockZ()) == null || locTo.getY() <= 0 || locTo.getY() >= 256)
                player.teleport(gameManager.getSpecSpawn());
        } else if (!event.getFrom().getBlock().equals(event.getTo().getBlock()) && gameManager.getStatus().equals(Status.IN_GAME))
            gameManager.getMapManager().movePlayer(player, event.getTo());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Block block = event.getBlock();

        event.setCancelled(true);

        if (block.getType().equals(Material.CARPET) && block.getData() == 8 && gameManager.getStatus().equals(Status.IN_GAME)) {

            Location location = block.getLocation();

            if (!location.clone().add(0, -1, 0).getBlock().getType().equals(Material.STONE) && !location.clone().add(0, -1, 0).getBlock().getType().equals(Material.STAINED_CLAY))
                return;

            Player player = event.getPlayer();
            PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

            if (playerBomberman.getBombNumber() > playerBomberman.getPlacedBombs() && gameManager.getMapManager().spawnBomb(location, playerBomberman)) {

                event.setCancelled(false);
                block.getLocation().add(0, 1, 0).getBlock().setType(Material.BARRIER, false);
                playerBomberman.updateInventory();
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

            if(playerBomberman == null || !playerBomberman.die()) {

                event.setDeathMessage("");
                return;
            }

            Player killer = player.getKiller();
            final String deathMessageBase = gameManager.getCoherenceMachine().getGameTag() + " " + player.getName();

            if (killer == null)
                event.setDeathMessage(deathMessageBase + " vient d'exploser");
            else if (killer.equals(player))
                event.setDeathMessage(deathMessageBase + " vient de se faire exploser");
            else {

                event.setDeathMessage(deathMessageBase + " vient de se faire exploser par " + killer.getName());

                PlayerBomberman killerBomberman = gameManager.getPlayer(killer.getUniqueId());

                killerBomberman.addCoins(5, "Meurtre de " + player.getName());
                killerBomberman.setKills(killerBomberman.getKills() + 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        if ((int) (event.getDamage() - 777.77) != 0 || !gameManager.getStatus().equals(Status.IN_GAME)) {
            event.setCancelled(true);
        }
    }
}
