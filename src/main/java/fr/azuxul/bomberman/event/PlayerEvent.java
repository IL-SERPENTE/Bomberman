package fr.azuxul.bomberman.event;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.map.CaseMap;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.api.games.Status;
import org.bukkit.GameMode;
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

        if (gameManager.getInGamePlayers().values().contains(playerBomberman) && !event.getFrom().getBlock().equals(event.getTo().getBlock()) && gameManager.getStatus().equals(Status.IN_GAME)) {

                CaseMap caseMap = playerBomberman.getCaseMap();

                if (caseMap != null)
                    caseMap.getPlayers().remove(playerBomberman);

                caseMap = gameManager.getMapManager().getCaseAtWorldLocation(event.getTo());

                if (caseMap != null) {
                    playerBomberman.setCaseMap(caseMap);
                    caseMap.getPlayers().add(playerBomberman);

                    if (playerBomberman.getPowerupTypes() != null && playerBomberman.getPowerupTypes().equals(PowerupTypes.AUTO_PLACE) && caseMap.getBomb() == null && playerBomberman.getBombNumber() > playerBomberman.getPlacedBombs())
                        gameManager.getMapManager().spawnBomb(event.getTo().getBlock().getLocation(), playerBomberman);
                } else
                    player.kickPlayer("Out of map");
            }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Block block = event.getBlock();

        event.setCancelled(true);

        if (block.getType().equals(Material.TNT) && gameManager.getStatus().equals(Status.IN_GAME)) {

            Location location = block.getLocation();

            if (!location.clone().add(0, -1, 0).getBlock().getType().equals(Material.STONE))
                return;

            Player player = event.getPlayer();
            PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

            if (playerBomberman.getBombNumber() > playerBomberman.getPlacedBombs()) {

                event.setCancelled(false);
                player.getInventory().setItem(0, new ItemStack(Material.TNT));

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
            player.teleport(gameManager.getSpecSpawn());
            player.setGameMode(GameMode.ADVENTURE);

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
