package net.samagames.bomberman.powerup;

import net.minecraft.server.v1_9_R2.World;
import net.samagames.bomberman.entity.Powerup;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;

import java.util.ArrayList;
import java.util.List;

/*
 * This file is part of Bomberman.
 *
 * Bomberman is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bomberman is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bomberman.  If not, see <http://www.gnu.org/licenses/>.
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
