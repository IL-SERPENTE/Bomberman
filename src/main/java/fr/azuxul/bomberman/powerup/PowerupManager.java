package fr.azuxul.bomberman.powerup;

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

    private final List<ActivePowerup> powerups;

    public PowerupManager() {

        this.powerups = new ArrayList<>();
    }

    public List<ActivePowerup> getPowerups() {

        return powerups;
    }

    public void spawnBoosterPowerup(Location location) {

        powerups.add(new BoosterPowerup().spawn(location));
    }

    public void spawnBombPowerup(Location location) {

        powerups.add(new BombPowerup().spawn(location));
    }

    public void spawnRadiusPowerup(Location location) {

        powerups.add(new RadiusPowerup().spawn(location));
    }
}
