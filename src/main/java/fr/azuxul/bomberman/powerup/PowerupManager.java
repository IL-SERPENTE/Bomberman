package fr.azuxul.bomberman.powerup;

import fr.azuxul.bomberman.entity.Powerup;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * Class description
 *
 * @author Azuxul
 */
public class PowerupManager {

    private final List<Powerup> powerups;

    public PowerupManager() {

        this.powerups = new ArrayList<>();
    }

    public List<Powerup> getPowerups() {

        return powerups;
    }

    public Powerup spawnPowerup(net.samagames.tools.powerups.Powerup powerupType, Location location) {

        World world = ((CraftWorld) location.getWorld()).getHandle();
        Powerup powerup = new Powerup(world, location.getX(), location.getY(), location.getZ(), powerupType);

        powerups.add(powerup);

        powerup.spawn();

        return powerup;
    }
}
