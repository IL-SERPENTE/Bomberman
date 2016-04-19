package net.samagames.bomberman.entity;

import net.minecraft.server.v1_9_R1.*;
import net.samagames.bomberman.Bomberman;
import net.samagames.bomberman.GameManager;
import net.samagames.bomberman.map.CaseMap;
import net.samagames.bomberman.player.PlayerBomberman;
import net.samagames.bomberman.powerup.Powerups;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Bomb entity
 *
 * @author Azuxul
 * @version 1.0
 */
public class Bomb extends EntityTNTPrimed {

    private static GameManager gameManager;
    private final int radius;
    private final PlayerBomberman owner;
    private int explodeTicks;

    public Bomb(World world, double x, double y, double z, int fuseTicks, int radius, Player owner) {
        super(world, x, y, z, ((CraftPlayer) owner).getHandle());

        gameManager = Bomberman.getGameManager();
        this.explodeTicks = fuseTicks;
        this.radius = radius;
        this.owner = gameManager.getPlayer(owner.getUniqueId());
        this.motX = 0;
        this.motY = 0;
        this.motZ = 0;
        this.velocityChanged = true;
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

                    explodeBomb(true);
                } else if (owner.getPowerups().equals(Powerups.DESTRUCTOR)) {

                    die(false, true);
                }
            }
        }
        return false;
    }

    @Override
    public void m() {

        if (this.explodeTicks-- <= 0 && isAlive()) {

            this.die();
        } else {
            this.world.addParticle(EnumParticle.SMOKE_NORMAL, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
        }
    }

    public void explodeBomb(boolean removeBomb) {

        die(true, removeBomb);
    }

    @Override
    public void die() {
        die(true, true);
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    public void die(boolean explosionDie, boolean removeBomb) {

        CraftWorld craftWorld = getWorld().getWorld();
        Location baseLocation = new Location(craftWorld, locX, locY, locZ);
        CaseMap caseMap = gameManager.getMapManager().getCaseAtWorldLocation(baseLocation.getBlockX(), baseLocation.getBlockZ());

        super.die();

        if (explosionDie) {

            caseMap.explode(owner.hasPowerup(Powerups.HYPER_BOMB), owner.hasPowerup(Powerups.SUPER_BOMB), owner);

            craftWorld.playSound(baseLocation, Sound.ENTITY_GENERIC_EXPLODE, 10.0f, 20.0f);
        } else
            caseMap.setBomb(null);

        if (removeBomb)
            this.owner.getAliveBombs().remove(this);
        this.owner.setPlacedBombs(this.owner.getPlacedBombs() - 1);
        this.owner.updateInventory();

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
