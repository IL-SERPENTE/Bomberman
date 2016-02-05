package fr.azuxul.bomberman.event;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.map.CaseMap;
import fr.azuxul.bomberman.player.PlayerBomberman;
import net.samagames.api.games.Status;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

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
        if (gameManager.getStatus().equals(Status.IN_GAME)) {

            Player player = event.getPlayer();
            PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

            if (gameManager.getInGamePlayers().values().contains(playerBomberman) && !event.getFrom().getBlock().equals(event.getTo().getBlock())) {

                CaseMap caseMap = playerBomberman.getCaseMap();

                if (caseMap != null)
                    caseMap.getPlayers().remove(playerBomberman);

                caseMap = gameManager.getMapManager().getCaseAtWorldLocation(event.getTo());

                if (caseMap != null) {
                    playerBomberman.setCaseMap(caseMap);
                    caseMap.getPlayers().add(playerBomberman);
                }
            }
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

                block.setType(Material.TNT);

                gameManager.getBombManager().spawnBomb(location, playerBomberman);
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
            else
                event.setDeathMessage(deathMessageBase + " viens de se faire exploser par " + killer.getName());

            playerBomberman.setSpectator();

            if (gameManager.getConnectedPlayers() <= 1)
                gameManager.endGame();
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (Double.compare(event.getDamage(), 777.77) == 0) {
            event.setCancelled(true);
        }
    }
}
