package fr.azuxul.bomberman.event;

import fr.azuxul.bomberman.Bomberman;
import fr.azuxul.bomberman.GameManager;
import net.samagames.api.games.Status;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        if (gameManager.getStatus().equals(Status.IN_GAME)) {
            Location location = event.getBlock().getLocation();
        }
    }
}
