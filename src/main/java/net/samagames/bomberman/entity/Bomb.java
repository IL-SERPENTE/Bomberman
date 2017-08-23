package net.samagames.bomberman.entity;

import net.minecraft.server.v1_9_R2.*;
import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.map.CaseMap;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.bomberman.powerup.Powerups;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

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
public class Bomb extends EntityTNTPrimed {

    private static GameManager gameManager;
    private final int radius;
    private final PlayerBomberman owner;
    private boolean normalBomb;
    private int explodeTicks;

    public Bomb(World world, double x, double y, double z, int fuseTicks, int radius, Player owner) {
        super(world, x, y, z, ((CraftPlayer) owner).getHandle());

        gameManager = Bomberman.getGameManager();
        this.explodeTicks = fuseTicks;
        this.setFuseTicks(fuseTicks * 2);
        this.radius = radius;
        this.owner = gameManager.getPlayer(owner.getUniqueId());
        this.motX = 0;
        this.motY = 0;
        this.motZ = 0;
        this.velocityChanged = true;
        this.normalBomb = true;
    }

    public boolean isNormalBomb() {
        return normalBomb;
    }

    public void setNormalBomb(boolean normalBomb) {
        this.normalBomb = normalBomb;
    }

    public int getExplodeTicks() {
        return explodeTicks;
    }

    public void setExplodeTicks(int explodeTicks) {
        this.explodeTicks = explodeTicks;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float damage) {

        Entity damager = damagesource.i();

        if (damager instanceof EntityPlayer) {

            PlayerBomberman playerBomberman = gameManager.getPlayer(damager.getUniqueID());

            if (playerBomberman != null && owner.getPowerups() != null) {
                if (owner.equals(playerBomberman) && owner.getPowerups().equals(Powerups.BOMB_ACTIVATOR)) {

                    die();
                } else if (owner.getPowerups().equals(Powerups.DESTRUCTOR)) {

                    die(false);
                }
            }
        }
        return false;
    }

    @Override
    public void m() {

        super.m();

        if (this.explodeTicks-- <= 0 && isAlive()) {

            this.die();
        } else {
            this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void die() {
        die(true);
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    public void die(boolean explosionDie) {

        CraftWorld craftWorld = getWorld().getWorld();
        Location baseLocation = new Location(craftWorld, locX, locY, locZ);
        CaseMap caseMap = gameManager.getMapManager().getCaseAtWorldLocation(baseLocation.getBlockX(), baseLocation.getBlockZ());

        super.die();

        if (explosionDie) {

            caseMap.explode(owner.hasPowerup(Powerups.HYPER_BOMB), owner.hasPowerup(Powerups.SUPER_BOMB), owner, normalBomb);

            craftWorld.playSound(baseLocation, Sound.ENTITY_GENERIC_EXPLODE, 10.0f, 20.0f);
        } else
            caseMap.setBomb(null);

        if (normalBomb) {
            this.owner.getAliveBombs().remove(this);
            this.owner.setPlacedBombs(this.owner.getPlacedBombs() - 1);
            this.owner.updateInventory();
        }

        baseLocation.add(0, 1, 0).getBlock().setType(Material.AIR, false);
    }

    @Override
    public boolean equals(Object comapeObject) {

        if (this == comapeObject)
            return true;
        if (!(comapeObject instanceof Bomb))
            return false;
        if (!super.equals(comapeObject))
            return false;

        Bomb bomb = (Bomb) comapeObject;
        return radius == bomb.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), radius);
    }
}
