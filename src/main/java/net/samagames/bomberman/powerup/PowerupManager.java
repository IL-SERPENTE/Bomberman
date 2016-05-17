package net.samagames.bomberman.powerup;

import net.minecraft.server.v1_9_R2.World;
import net.samagames.bomberman.entity.Powerup;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * Powerup manager
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
