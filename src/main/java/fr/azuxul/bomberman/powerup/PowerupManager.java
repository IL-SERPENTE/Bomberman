package fr.azuxul.bomberman.powerup;

import fr.azuxul.bomberman.GameManager;
import net.samagames.tools.powerups.ActivePowerup;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Class description
 *
 * @author Azuxul
 */
public class PowerupManager {

    GameManager gameManager;
    List<ActivePowerup> powerups;

    public PowerupManager(GameManager gameManager) {

        this.gameManager = gameManager;
        this.powerups = new ArrayList<>();
    }

    public List<ActivePowerup> getPowerups() {

        return powerups;
    }

    public void spawnBoosterPowerup(Location location) {

        powerups.add(new BasicPowerup().spawn(location));
    }

    public void spawnBombPowerup(Location location) {

        powerups.add(new BombPowerup().spawn(location));
    }
}
